package com.googlecode.assertion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.Iterator;

public class MessageArchive {

	private static final int INDEX_ENTRY_SIZE = 128;

	public enum Mode {
		READ, WRITE;
	}

	private final Mode mode;
	private final RandomAccessFile indexFile;
	private final RandomAccessFile dataFile;
	private long dataByteCount;

	public MessageArchive(final File indexFile, final Mode mode) {
		try {
			this.indexFile = new RandomAccessFile(indexFile, mode == Mode.READ ? "r" : "rw");
			this.mode = mode;
			final String name = indexFile.getName();
			final int prefixEndIndex = name.lastIndexOf(".index");
			final String prefix = name.substring(0, prefixEndIndex);
			dataFile = new RandomAccessFile(new File(indexFile.getAbsoluteFile().getParentFile(), String.format(
					"%s.data", prefix)), mode == Mode.READ ? "r" : "rw");
			dataByteCount = dataFile.length();
			dataFile.seek(dataByteCount);

			this.indexFile.seek(this.indexFile.length());
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void store(final Message message) {
		if (mode != Mode.WRITE) {
			throw new IllegalStateException();
		}
		// data in .data
		final long dataPosition = dataByteCount;
		final String content = message.getContent();
		final byte[] bytes = content == null ? new byte[0] : content.getBytes();
		final int dataLength = bytes.length;
		try {
			dataFile.write(bytes);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		dataByteCount += bytes.length;

		// entry in .index
		final byte[] recordBytes = new byte[MessageArchive.INDEX_ENTRY_SIZE];
		populateRecord(recordBytes, dataPosition, dataLength, message);
		try {
			indexFile.write(recordBytes);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Message readMessage(final int messageIndex) {
		if (mode != Mode.READ) {
			throw new IllegalStateException();
		}
		final long offset = messageIndex * MessageArchive.INDEX_ENTRY_SIZE;

		// entry in .index
		final Record record = readRecord(offset);

		final Message message = record.getMessage();
		// data in .data
		try {
			dataFile.seek(record.getOffset());
			final int dataLength = record.getLength();
			if (dataLength > 0) {
				final byte[] data = new byte[dataLength];
				final int byteReadCount = dataFile.read(data);
				if (byteReadCount != data.length) {
					throw new RuntimeException("did not read all bytes");
				}
				message.setContent(new String(data));
			}
			return message;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void populateRecord(final byte[] recordBytes, final long dataPosition, final int dataLength,
			final Message message) {
		try {
			// final DataInputStream dis = new DataInputStream(new
			// ByteArrayInputStream(recordBytes));
			final ByteArrayOutputStream out = new ByteArrayOutputStream(recordBytes.length);
			final DataOutputStream dos = new DataOutputStream(out);
			dos.writeLong(dataPosition);
			dos.writeInt(dataLength);
			dos.writeLong(message.getDate().getTime());
			dos.writeByte(message.getSource());
			dos.writeByte(message.getTarget());
			final String displayName = message.getDisplayName();
			final int displayNameLength;
			displayNameLength = displayName == null ? 0 : displayName.getBytes().length;
			dos.writeInt(displayNameLength);
			if (displayNameLength > 0) {
				final byte[] displayNameBytes = displayName.getBytes();
				if (displayNameBytes.length > 83) {
					throw new RuntimeException();
				}
				dos.write(displayNameBytes);
			}
			final byte[] byteArray = out.toByteArray();
			System.arraycopy(byteArray, 0, recordBytes, 0, byteArray.length);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Record readRecord(final long indexOffset) {
		try {
			indexFile.seek(indexOffset);

			final byte[] bytes = new byte[MessageArchive.INDEX_ENTRY_SIZE];
			final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			final DataInputStream dis = new DataInputStream(bais);
			final int byteReadCount;
			try {
				byteReadCount = indexFile.read(bytes);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
			if (byteReadCount != bytes.length) {
				throw new RuntimeException(String.format("Incomplete Read: read %d bytes but expected %d bytes",
						byteReadCount, bytes.length));
			}
			final long dataPosition = dis.readLong();
			final int dataLength = dis.readInt();
			final long dateTime = dis.readLong();
			final byte source = dis.readByte();
			final byte target = dis.readByte();
			final int displayNameLength = dis.readInt();
			final String displayName;
			if (displayNameLength == 0) {
				displayName = null;
			} else {
				final byte[] displayNameBytes = new byte[displayNameLength];
				dis.read(displayNameBytes);
				displayName = new String(displayNameBytes);
			}
			final Message message = new Message(source, target, new Date(dateTime), displayName);
			final Record record = new Record(message);
			record.setOffset(dataPosition);
			record.setLength(dataLength);
			return record;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void close() {
		if (mode != Mode.WRITE) {
			try {
				dataFile.close();
			} catch (final IOException e) {
				// ignored
			}
			try {
				indexFile.close();
			} catch (final IOException e) {
				// ignored
			}
		}
	}

	public Iterator<Message> getIterator(final int offset) {
		return new Iterator<Message>() {

			private int currentIndex = offset;

			@Override
			public boolean hasNext() {
				return currentIndex < getMessageCount();
			}

			@Override
			public Message next() {
				final Message message = readMessage(currentIndex);
				currentIndex++;
				return message;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	public int getMessageCount() {
		try {
			return (int) (indexFile.length() / MessageArchive.INDEX_ENTRY_SIZE);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}

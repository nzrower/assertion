package com.googlecode.assertion;


/**
 * A {@link Record} Relates a given message to storage information about the
 * offset in the .data file and the length.
 */
public class Record {

	private final Message message;
	private long offset;
	private int length; // length is an int because arrays can't use longs.

	public Record(final Message message) {
		this.message = message;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(final long offset) {
		this.offset = offset;
	}

	public int getLength() {
		return length;
	}

	public void setLength(final int length) {
		this.length = length;
	}

	public Message getMessage() {
		return message;
	}
}
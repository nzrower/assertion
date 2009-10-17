package com.googlecode.assertion;

import java.io.File;
import java.util.Date;

import com.googlecode.assertion.utility.BaseThread;

/**
 * Creates example.index archive with sample data.
 */
public class ExampleArchiveThreadMain extends BaseThread {

	public static void main(final String[] args) {
		final ExampleArchiveThreadMain messageThreadMain = new ExampleArchiveThreadMain();
		messageThreadMain.start();
	}

	public ExampleArchiveThreadMain() {
		super(ExampleArchiveThreadMain.class.getName());
	}

	@Override
	public void mainLoop() {
		final File currentFile = new File(".").getAbsoluteFile().getParentFile().getParentFile();
		info("Current directory is %s", currentFile.getAbsolutePath());
		final File indexFile = new File(currentFile, "com.googlecode.assertion.ui/example.index");
		info("Attempting to write example file to %s", indexFile.getAbsolutePath());

		final MessageArchive messageArchive = new MessageArchive(indexFile, MessageArchive.Mode.WRITE);
		long start = System.currentTimeMillis() - 60000;
		{
			final Message message = new Message((byte) 0x01, (byte) 0x02, new Date(start), "Request #1");
			message.setContent("Content #1");
			messageArchive.store(message);
		}

		{
			final Message message = new Message((byte) 0x02, (byte) 0x03, new Date(start + 10), "Request #2");
			message.setContent("Content #2");
			messageArchive.store(message);
		}

		{
			final Message message = new Message((byte) 0x03, (byte) 0x02, new Date(start + 5), "Response #2");
			message.setContent("Content #3");
			messageArchive.store(message);
		}

		{
			final Message message = new Message((byte) 0x02, (byte) 0x01, new Date(start + 15), "Response #1");
			message.setContent("Content #4");
			messageArchive.store(message);
		}

		{
			final Message message = new Message((byte) 0x01, (byte) 0x05, new Date(start + 15), "Request #3");
			message.setContent("Content #5");
			messageArchive.store(message);
		}

		{
			final Message message = new Message((byte) 0x05, (byte) 0x01, new Date(start + 15), "Response #3");
			message.setContent("Content #6");
			messageArchive.store(message);
		}

		{
			final Message message = new Message((byte) 0x03, (byte) 0x06, new Date(start + 15), "Request #4");
			message.setContent("Content #7");
			messageArchive.store(message);
		}

		{
			final Message message = new Message((byte) 0x03, (byte) 0x06, new Date(start + 15), "Request #5");
			message.setContent("Content #8");
			messageArchive.store(message);
		}

		{
			final Message message = new Message((byte) 0x03, (byte) 0x06, new Date(start + 15), "Request #6");
			message.setContent("Content #9");
			messageArchive.store(message);
		}
	}

	@Override
	public void info(final String format, final Object... objects) {
		super.logToSystemOut(format, objects);
	}

}
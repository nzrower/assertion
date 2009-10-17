package com.googlecode.assertion;

import java.io.File;
import java.util.Date;

import com.googlecode.assertion.Message;
import com.googlecode.assertion.MessageArchive;

/**
 * Test Class. It creates BATCH_SIZE messages, then sleeps for SLEEP_MILLISECONDS milliseconds.
 */
public class MessageThreadMain extends Thread {

	public static final int BATCH_SIZE = 40;
	public static final int SLEEP_MILLISECONDS = 2000;

	public static void main(final String[] args) {
		final MessageThreadMain messageThreadMain = new MessageThreadMain();
		messageThreadMain.start();
	}

	public MessageThreadMain() {
		super(MessageThreadMain.class.getName());
	}

	@Override
	public void run() {
		final File indexFile = new File("archive.index");
		final MessageArchive messageArchive = new MessageArchive(indexFile, MessageArchive.Mode.WRITE);
		int totalCount = 0;
		while (true) {
			for (int i = 0; i < BATCH_SIZE; i++) {
				final Message message = new Message((byte) 0x01, (byte) 0x02, new Date(), "Hello " + i + " : "
						+ totalCount);
				message.setContent("Content " + totalCount);
				messageArchive.store(message);
				totalCount++;
			}
			System.out.println("Total count " + totalCount);
			try {
				sleep(SLEEP_MILLISECONDS);
			} catch (final InterruptedException e) {
				// ignored
			}
		}
	}
}
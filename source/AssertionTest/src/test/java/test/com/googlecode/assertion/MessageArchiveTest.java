package test.com.googlecode.assertion;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.googlecode.assertion.Message;
import com.googlecode.assertion.MessageArchive;
import com.googlecode.assertion.MessageArchive.Mode;

public class MessageArchiveTest {

	private static final File INDEX_FILE = new File("archive.index");
	private static final File DATA_FILE = new File("data.index");
	private static final File INDEX2_FILE = new File("archive2.index");
	private static final File DATA2_FILE = new File("data2.index");
	private static final File INDEX3_FILE = new File("archive3.index");
	private static final File DATA3_FILE = new File("data3.index");
	public static final byte SOURCE = (byte) 0x7f;
	public static final byte TARGET = (byte) 0x7f;

	public final Date DATE = new Date();

	public static final String DISPLAY_NAME = "Display Name";

	@Before
	@After
	public void clean() {
		MessageArchiveTest.INDEX_FILE.delete();
		MessageArchiveTest.DATA_FILE.delete();
		MessageArchiveTest.INDEX2_FILE.delete();
		MessageArchiveTest.DATA2_FILE.delete();
		MessageArchiveTest.INDEX3_FILE.delete();
		MessageArchiveTest.DATA3_FILE.delete();
	}

	@Test
	public void testMessageRoundTrip() {
		final Message initial = new Message(MessageArchiveTest.SOURCE, MessageArchiveTest.TARGET, DATE,
				MessageArchiveTest.DISPLAY_NAME);
		initial.setContent("Content");
		final MessageArchive writeArchive = new MessageArchive(MessageArchiveTest.INDEX_FILE, Mode.WRITE);
		Assert.assertEquals("Number of messages", 0, writeArchive.getMessageCount());
		writeArchive.store(initial);
		Assert.assertEquals("Number of messages", 1, writeArchive.getMessageCount());
		writeArchive.close();
		final MessageArchive readArchive = new MessageArchive(MessageArchiveTest.INDEX_FILE, Mode.READ);
		final Message actual = readArchive.readMessage(0);
		final Iterator<Message> iterator = readArchive.getIterator(0);
		Assert.assertTrue("Iterator init value", iterator.hasNext());
		final Message actual2 = iterator.next();
		Assert.assertFalse("Iterator end of sequence", iterator.hasNext());
		readArchive.close();
		assertEquals("Round Trip", initial, actual);
		assertEquals("Thru Iterator", actual, actual2);
	}

	@Test
	public void testMessageRoundTripNoDisplayName() {
		final Message initial = new Message(MessageArchiveTest.SOURCE, MessageArchiveTest.TARGET, DATE, null);
		final MessageArchive writeArchive = new MessageArchive(MessageArchiveTest.INDEX3_FILE, Mode.WRITE);
		writeArchive.store(initial);
		writeArchive.close();
		final MessageArchive readArchive = new MessageArchive(MessageArchiveTest.INDEX3_FILE, Mode.READ);
		final Message actual = readArchive.readMessage(0);
		readArchive.close();
		assertEquals("Round Trip", initial, actual);
	}

	@Test
	public void testMessageRoundTripNoContent() {
		final Message initial = new Message(MessageArchiveTest.SOURCE, MessageArchiveTest.TARGET, DATE,
				MessageArchiveTest.DISPLAY_NAME);
		final MessageArchive writeArchive = new MessageArchive(MessageArchiveTest.INDEX2_FILE, Mode.WRITE);
		writeArchive.store(initial);
		writeArchive.close();
		final MessageArchive readArchive = new MessageArchive(MessageArchiveTest.INDEX2_FILE, Mode.READ);
		final Message actual = readArchive.readMessage(0);
		readArchive.close();
		assertEquals("Round Trip", initial, actual);
	}

	private void assertEquals(final String description, final Message expected, final Message actual) {
		Assert.assertEquals(description + " source", expected.getSource(), actual.getSource());
		Assert.assertEquals(description + " target", expected.getTarget(), actual.getTarget());
		Assert.assertEquals(description + " date", expected.getDate(), actual.getDate());
		Assert.assertEquals(description + " display name", expected.getDisplayName(), actual.getDisplayName());
		Assert.assertEquals(description + " content", expected.getContent(), actual.getContent());
	}

}

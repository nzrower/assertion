package test.com.googlecode.assertion;

import junit.framework.Assert;

import org.junit.Test;

import com.googlecode.assertion.utility.Utility;

public class UtilityTest {

	@Test
	public void testJoin() {
		final String[] args = new String[] { "one", "two" };
		final String expected = "one two";
		Assert.assertEquals("join", expected, Utility.join(args, ' '));
	}

}

package com.googlecode.assertion.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public abstract class BaseThread extends Thread {

	public BaseThread(final String name) {
		super(name);
		setDaemon(false);
	}

	@Override
	public void run() {
		final long startMillis = System.currentTimeMillis();
		try {
			info("Starting");
			mainLoop();
			info("Completed");
		} finally {
			info("Exiting after %d ms", (System.currentTimeMillis() - startMillis));
		}
	}

	public abstract void mainLoop();

	public abstract void info(final String format, final Object... objects);

	public void logToSystemOut(final String format, final Object[] objects) {
		// TODO beautify
		final List<Object> args = new ArrayList<Object>();
		args.add(Formats.getLongDateTime().format(new Date()));
		args.addAll(Arrays.asList(objects));
		System.out.println(String.format("%s " + format, args.toArray()));
	}

}

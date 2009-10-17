package com.googlecode.assertion.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class Formats {

	private static final ThreadLocal<DateFormat> LONG_DATE_TIME = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		}

	};

	public static DateFormat getLongDateTime() {
		return LONG_DATE_TIME.get();
	}

}

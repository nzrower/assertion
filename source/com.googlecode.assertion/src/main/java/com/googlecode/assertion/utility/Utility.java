package com.googlecode.assertion.utility;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public abstract class Utility {

	public static String join(final String[] values, final char joinChar) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			if (i != 0) {
				builder.append(joinChar);
			}
			builder.append(values[i]);
		}
		return builder.toString();
	}

	@Deprecated
	public static final ThreadLocal<SimpleDateFormat> LONG_DATE_TIME = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		}
	};

	public static final ThreadLocal<NumberFormat> NUMBER = new ThreadLocal<NumberFormat>() {
		@Override
		protected NumberFormat initialValue() {
			return NumberFormat.getNumberInstance();
		}

	};

	public static void throwIfNull(final Object object) {
		if (object == null) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * TODO attempt to submit for Closeables
	 * 
	 * @param socket
	 */
	public static void closeQuietly(final ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (final SQLException e) {
				// ignored
			}
		}
	}

	/**
	 * TODO attempt to submit for Closeables
	 * 
	 * @param socket
	 */
	public static void closeQuietly(final PreparedStatement preparedStatement) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (final SQLException e) {
				// ignored
			}
		}
	}

	/**
	 * TODO attempt to submit for Closeables
	 * 
	 * @param socket
	 */
	public static void closeQuietly(final Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (final SQLException e) {
				// ignored
			}
		}
	}

	/**
	 * TODO attempt to submit for Closeables
	 * 
	 * @param socket
	 */
	public static void closeQuietly(final Socket serverSocket) {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (final IOException e) {
				// ignored
			}
		}
	}

	/**
	 * TODO attempt to submit for Closeables
	 * 
	 * @param serverSocket
	 */
	public static void closeQuietly(final ServerSocket serverSocket) {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (final IOException e) {
				// ignored
			}
		}
	}

}

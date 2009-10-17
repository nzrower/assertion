package com.googlecode.assertion;

import java.util.Date;

import com.googlecode.assertion.utility.Utility;

/**
 * A {@link Message} is an interaction between two software components which has summary and content fields. A
 * {@link Message} also has a date time that it was sent.
 * 
 * The summary is stored in .index and has a limited maximum size. The content is stored in .data and can be variable
 * length.
 */
public class Message {

	private final byte source;
	private final byte target;
	private final Date date;
	private final String summary;
	private String content;

	public Message(final byte source, final byte target, final Date date, final String summary) {
		this.source = source;
		this.target = target;
		this.date = date;
		this.summary = summary;
	}

	public byte getSource() {
		return source;
	}

	public byte getTarget() {
		return target;
	}

	public Date getDate() {
		return date;
	}

	public String getDisplayName() {
		return summary;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return String.format("%s %d --> %d%n%s%n%s", Utility.LONG_DATE_TIME.get().format(date), source, target,
				summary, content == null ? "" : content);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (content == null ? 0 : content.hashCode());
		result = prime * result + (date == null ? 0 : date.hashCode());
		result = prime * result + (summary == null ? 0 : summary.hashCode());
		result = prime * result + source;
		result = prime * result + target;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Message other = (Message) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		if (date == null) {
			if (other.date != null) {
				return false;
			}
		} else if (!date.equals(other.date)) {
			return false;
		}
		if (summary == null) {
			if (other.summary != null) {
				return false;
			}
		} else if (!summary.equals(other.summary)) {
			return false;
		}
		if (source != other.source) {
			return false;
		}
		if (target != other.target) {
			return false;
		}
		return true;
	}
}
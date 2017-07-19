package com.github.obase.kit;

/**
 * This is a very useful utility class to split a text
 */
public class Spliter {

	char sep;
	String text;
	int mark;

	public Spliter() {
		this('\0', null);
	}

	public Spliter(char sep) {
		this(sep, null);
	}

	public Spliter(char sep, String text) {
		this.sep = sep;
		this.text = text;
	}

	public Spliter reset(char sep, String text) {
		this.text = text;
		this.sep = sep;
		this.mark = 0;
		return this;
	}

	public Spliter reset(String text) {
		return reset(this.sep, text);
	}

	public String next() {

		String ret = null;
		if (mark != -1) {
			int next = text.indexOf(sep, mark);
			if (next != -1) {
				ret = text.substring(mark, next);
				mark = next + 1;
			} else {
				ret = text.substring(mark);
				mark = -1;
			}
		}
		return ret;
	}

	@Override
	public String toString() {
		return this.text;
	}
}

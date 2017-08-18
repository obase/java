package com.github.obase.kit;

import java.util.LinkedList;

public class Joiner {

	char sep;
	final LinkedList<String> list = new LinkedList<String>();
	int len;

	public Joiner(char sep) {
		this.sep = sep;
	}

	public Joiner join(String val) {
		list.add(val);
		len += val == null ? 4 : val.length();
		return this;
	}

	public Joiner reset() {
		return reset(sep);
	}

	public Joiner reset(char sep) {
		this.sep = sep;
		this.list.clear();
		this.len = 0;
		return this;
	}

	public String toString() {
		if (list.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder(len + list.size() + 64);
		for (String item : list) {
			sb.append(item).append(sep);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
}

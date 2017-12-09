package com.github.obase.mysql.xml;

public final class Param {

	public static final Param[] EMPTY_ARRAY = new Param[0];

	public final String name;
	public final int idx;
	public final Object val;
	public final boolean set;

	public Param(String name) {
		this.name = name;
		this.idx = -1;
		this.val = null;
		this.set = false;
	}

	public Param(String name, Object value) {
		this(name, -1, value);
	}

	public Param(String name, int idx, Object value) {
		this.name = name;
		this.idx = idx;
		this.val = value;
		this.set = true;
	}

	/**
	 * result is "xxx" or "xxx!"
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		sb.append(name);
		if (idx != -1) {
			sb.append(':').append(idx);
		}
		if (set) {
			sb.append("!");
		}
		return sb.toString();
	}

}

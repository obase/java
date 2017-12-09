package com.github.obase.mysql.xml;

public final class Param {

	public static final Param[] EMPTY_ARRAY = new Param[0];

	public final String name;
	public final Object value;
	public final boolean setted;

	public Param(String name) {
		this.name = name;
		this.value = null;
		this.setted = false;
	}

	public Param(String name, int index, Object value) {
		this(new StringBuilder(name.length() + 8).append(name).append('[').append(index).append(']').toString(), value);
	}

	public Param(String name, Object value) {
		this.name = name;
		this.value = value;
		this.setted = true;
	}

	/**
	 * result is "xxx" or "xxx!"
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(64);
		sb.append(name);
		if (setted) {
			sb.append("!");
		}
		return sb.toString();
	}

}

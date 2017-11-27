package com.github.obase.mysql.stmt;

public final class Pack {

	public static final int YES = 1;
	public static final int NO = -1;
	public static final int UKW = 0;

	public final int code;
	public final Object value;

	public Pack(int code, Object value) {
		this.code = code;
		this.value = value;
	}

	public static final Pack NIL = new Pack(UKW, null);
}

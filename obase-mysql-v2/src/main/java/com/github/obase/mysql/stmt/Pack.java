package com.github.obase.mysql.stmt;

public final class Pack {

	public static final int CODE_YES = 1;
	public static final int CODE_NO = -1;
	public static final int CODE_UKW = 0;

	public final int code;
	public final Object value;

	public Pack(int code, Object value) {
		this.code = code;
		this.value = value;
	}

	public static final Pack UKW = new Pack(CODE_UKW, null);
	public static final Pack NO = new Pack(CODE_NO, null);
}

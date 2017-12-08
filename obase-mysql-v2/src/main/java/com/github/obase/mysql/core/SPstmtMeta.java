package com.github.obase.mysql.core;

import java.util.Arrays;
import java.util.List;

import com.github.obase.mysql.stmt.Param;

public final class SPstmtMeta extends PstmtMeta {

	public static String[] EMPTY_ARRAY = new String[0];

	public final String[] params;

	public SPstmtMeta(boolean nop, String psql, String[] params) {
		super(nop, psql);
		this.params = params == null ? EMPTY_ARRAY : params;
	}

	@Override
	public String toString() {
		return new StringBuilder(4096).append(psql).append(", ").append(Arrays.toString(params)).toString();
	}

	public static SPstmtMeta getInstance(String psql, List<String> params) {
		String[] ps = null;
		if (params != null && params.size() > 0) {
			ps = params.toArray(new String[params.size()]);
		}
		return new SPstmtMeta(false, psql, ps);
	}

	public static SPstmtMeta getInstance(boolean nop, String psql, Param[] params) {
		String[] ps = null;
		if (params != null && params.length > 0) {
			ps = new String[params.length];
			for (int i = 0, n = params.length; i < n; i++) {
				ps[i] = params[i].name;
			}
		}
		return new SPstmtMeta(nop, psql, ps);
	}

}

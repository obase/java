package com.github.obase.mysql.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.PstmtMeta;

public final class SPstmtMeta extends PstmtMeta {

	public static String[] EMPTY_ARRAY = new String[0];

	final String[] params;

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

	@Override
	public int setParam(PreparedStatement ps, JdbcMeta meta, Object bean) throws SQLException {
		int pos = 0;
		for (String p : params) {
			++pos;
			meta.setParam(ps, pos, bean, p);
		}
		return pos;
	}

}

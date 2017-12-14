package com.github.obase.mysql.xml;

import java.util.List;

import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.impl.ParamBuilder;

public final class Static implements Part {

	public final String psql;
	public final String[] params;

	public Static(String psql, String[] params) {
		this.psql = psql;
		this.params = params == null ? EMPTY_PARAM : params;
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

	@Override
	public String getSeparator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPsql() {
		return this.psql;
	}

	@Override
	public String[] getParams() {
		return this.params;
	}

	@Override
	public boolean processDynamic(JdbcMeta meta, Object bean, StringBuilder psqls, ParamBuilder params, int idx) {
		throw new UnsupportedOperationException();
	}

	public static Static getInstance(String psql, List<String> params) {
		return new Static(psql, params.toArray(new String[params.size()]));
	}

}

package com.github.obase.mysql.stmt;

import java.util.LinkedList;
import java.util.List;

import com.github.obase.mysql.core.Fragment;
import com.github.obase.mysql.core.JdbcMeta;

public final class Static implements Fragment {

	public final String psql;
	public final Param[] params;

	public static Static getInstance(String psql, List<String> params) {
		List<Param> _params = new LinkedList<Param>();
		if (params != null && params.size() > 0) {
			for (String p : params) {
				_params.add(new Param(p));
			}
		}
		return new Static(psql, _params);
	}

	public Static(String psql, List<Param> params) {
		this.psql = psql;
		this.params = params == null ? Param.EMPTY_ARRAY : params.toArray(new Param[params.size()]);
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

	@Override
	public String getPsql() {
		return this.psql;
	}

	@Override
	public Param[] getParams() {
		return this.params;
	}

	@Override
	public boolean processDynamic(JdbcMeta meta, Object bean, StringBuilder psqls, List<Param> params, int idx) {
		throw new UnsupportedOperationException();
	}

}

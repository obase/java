package com.github.obase.mysql.xml;

import java.util.ArrayList;
import java.util.List;

import com.github.obase.mysql.DLink;
import com.github.obase.mysql.JdbcMeta;
import com.github.obase.mysql.Part;

public final class Static implements Part {

	public final String psql;
	public final Param[] params;

	public Static(String psql, Param[] params) {
		this.psql = psql;
		this.params = params == null ? Param.EMPTY_ARRAY : params;
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
	public Param[] getParams() {
		return this.params;
	}

	@Override
	public boolean processDynamic(JdbcMeta meta, Object bean, StringBuilder psqls, DLink<Param> params, int idx) {
		throw new UnsupportedOperationException();
	}

	public static Static getInstance(String psql, List<String> params) {
		Param[] _params = null;
		if (params != null && params.size() > 0) {
			List<Param> list = new ArrayList<Param>(params.size());
			for (String p : params) {
				list.add(new Param(p));
			}
			_params = list.toArray(new Param[list.size()]);
		}
		return new Static(psql, _params);
	}

}

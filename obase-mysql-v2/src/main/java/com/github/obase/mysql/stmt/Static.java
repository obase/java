package com.github.obase.mysql.stmt;

import com.github.obase.mysql.core.DLink;
import com.github.obase.mysql.core.DNode;
import com.github.obase.mysql.core.JdbcMeta;
import com.github.obase.mysql.core.Part;

public final class Static implements Part {

	public final String psql;
	public final DLink<Param> params;

	public Static(String psql, DLink<Param> params) {
		this.psql = psql;
		this.params = params == null ? DLink.nil() : params;
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
	public DLink<Param> getParams() {
		return this.params;
	}

	@Override
	public boolean processDynamic(JdbcMeta meta, Object bean, DLink<String> psqls, DLink<Param> params, int idx) {
		throw new UnsupportedOperationException();
	}

	public static Static getInstance(String psql, DLink<String> params) {
		DLink<Param> _params = new DLink<Param>();
		for (DNode<String> t = params.head; t != null; t = t.next) {
			_params.tail(new Param(t.value));
		}
		return new Static(psql, _params);
	}
}

package com.github.obase.mysql.core;

import java.util.Map;

import com.github.obase.mysql.stmt.Param;

public class PstmtMeta {

	public static final int UNSET = -1;

	public final boolean nop;
	public final String psql;
	public final DLink<Param> params;

	public Map<String, Integer> label;
	public int select = UNSET;
	public int from = UNSET;
	public int where = UNSET;
	public int group = UNSET;
	public int having = UNSET;
	public int order = UNSET;
	public int limit = UNSET;

	// 由selectAll构建
	public String limitPsql;
	public String countPsql;

	// 构造时必须复制外来参数param
	public PstmtMeta(String psql, DLink<Param> params) {
		this(false, psql, params);
	}

	public PstmtMeta(boolean nop, String psql, DLink<Param> params) {
		this.nop = nop;
		this.psql = psql;
		this.params = params;
	}

	@Override
	public String toString() {
		return new StringBuilder(4096).append(psql).append(", ").append(params).toString();
	}

	public static PstmtMeta getInstance(String psql, DLink<String> params) {
		DLink<Param> ps = new DLink<Param>();
		for (DNode<String> t = params.head; t != null; t = t.next) {
			ps.tail(new Param(t.value));
		}
		return new PstmtMeta(psql, ps);
	}

}

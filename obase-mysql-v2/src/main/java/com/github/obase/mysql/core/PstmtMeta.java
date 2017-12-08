package com.github.obase.mysql.core;

import java.util.Map;

public abstract class PstmtMeta {

	public static final int UNSET = -1;

	public final boolean nop;
	public final String psql;

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
	protected PstmtMeta(boolean nop, String psql) {
		this.nop = nop;
		this.psql = psql;
	}

}

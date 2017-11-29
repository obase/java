package com.github.obase.mysql.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.obase.mysql.stmt.Param;

public class PstmtMeta {

	public static final int UNSET = -1;

	public final boolean nop;
	public final String psql;
	public final List<Param> param;

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
	public PstmtMeta(String psql, List<Param> params) {
		this(false, psql, params);
	}

	public PstmtMeta(boolean nop, String psql, List<Param> params) {
		this.nop = nop;
		this.psql = psql;
		this.param = params == null ? Collections.emptyList() : params;
	}

	@Override
	public String toString() {
		return new StringBuilder(4096).append(psql).append(", ").append(param).toString();
	}

	public static PstmtMeta getInstance(String psql, List<String> param) {
		List<Param> ps = new ArrayList<Param>(param == null ? 0 : param.size());
		for (String p : param) {
			ps.add(new Param(p));
		}
		return new PstmtMeta(false, psql, ps);
	}

}

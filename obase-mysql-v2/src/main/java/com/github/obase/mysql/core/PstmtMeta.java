package com.github.obase.mysql.core;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.github.obase.mysql.stmt.Param;

public class PstmtMeta {

	public static final int UNSET = -1;

	public final boolean nop;
	public final String psql;
	public final Param[] params;

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
	public PstmtMeta(String psql, Param[] params) {
		this(false, psql, params);
	}

	public PstmtMeta(boolean nop, String psql, Param[] params) {
		this.nop = nop;
		this.psql = psql;
		this.params = params;
	}

	@Override
	public String toString() {
		return new StringBuilder(4096).append(psql).append(", ").append(Arrays.toString(params)).toString();
	}

	public static PstmtMeta getInstance(String psql, List<String> params) {
		Param[] ps;
		if (params == null || params.isEmpty()) {
			ps = Param.EMPTY_ARRAY;
		} else {
			ps = new Param[params.size()];
			int idx = 0;
			for (String p : params) {
				ps[idx++] = new Param(p);
			}
		}
		return new PstmtMeta(psql, ps);
	}

}

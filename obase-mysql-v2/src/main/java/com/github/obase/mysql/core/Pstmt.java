package com.github.obase.mysql.core;

import java.util.List;

public class Pstmt {

	public final String psql;
	public final List<String> param;

	// 构造时必须复制外来参数param
	public Pstmt(String psql, List<String> param) {
		this.psql = psql;
		this.param = param;
	}

	@Override
	public String toString() {
		return new StringBuilder(4096).append(psql).append(", ").append(param).toString();
	}
}

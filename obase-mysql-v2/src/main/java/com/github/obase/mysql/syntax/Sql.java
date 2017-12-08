package com.github.obase.mysql.syntax;

import com.github.obase.mysql.core.DLink;

public class Sql {

	public final String content;
	public final DLink<String> params;

	// 构造时必须复制外来参数param
	public Sql(String content, DLink<String> params) {
		this.content = content;
		this.params = params;
	}

	@Override
	public String toString() {
		return new StringBuilder(4096).append(content).append(", ").append(params).toString();
	}
}

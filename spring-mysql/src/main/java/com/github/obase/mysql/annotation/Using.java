package com.github.obase.mysql.annotation;

public enum Using {

	NULL(null), BTREE("BTREE"), HASH("HASH");

	public final String sqlValue;

	private Using(String sqlValue) {
		this.sqlValue = sqlValue;
	}
}

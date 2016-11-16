package com.github.obase.mysql.annotation;

public enum Match {

	NULL(null), FULL("FULL"), PARTIAL("PARTIAL"), SIMPLE("SIMPLE");

	final String sqlValue;

	private Match(String sqlValue) {
		this.sqlValue = sqlValue;
	}
}

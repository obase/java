package com.github.obase.mysql.annotation;

public enum Option {

	NULL(null), RESTRICT("RESTRICT"), CASCADE("CASCADE"), SET_NULL("SET NULL"), NO_ACTION("NO ACTION");

	public final String sqlValue;

	private Option(String sqlValue) {
		this.sqlValue = sqlValue;
	}
}

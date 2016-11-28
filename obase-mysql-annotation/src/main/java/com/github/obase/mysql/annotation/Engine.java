package com.github.obase.mysql.annotation;

public enum Engine {

	NULL(null), InnoDB("InnoDB"), MyISAM("MyISAM"), MRG_MYISAM("MRG_MYISAM"), CSV("CSV"), BLACKHOLE("BLACKHOLE"), MEMORY("MEMORY"), ARCHIVE("ARCHIVE"), PERFORMANCE_SCHEMA("PERFORMANCE_SCHEMA");
	public final String sqlValue;

	private Engine(String sqlValue) {
		this.sqlValue = sqlValue;
	}
}

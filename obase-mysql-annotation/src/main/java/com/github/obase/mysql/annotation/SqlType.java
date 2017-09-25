package com.github.obase.mysql.annotation;

import java.sql.Types;

/**
 * The mysql data type supported by mysqlClient
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public enum SqlType {

	NULL(null, Types.NULL), // reflect according java type
	BIT("BIT", Types.BIT), //
	TINYINT("TINYINT", Types.TINYINT), //
	SMALLINT("SMALLINT", Types.SMALLINT), //
	MEDIUMINT("MEDIUMINT", Types.INTEGER), //
	INT("INT", Types.INTEGER), //
	INTEGER("INTEGER", Types.INTEGER), //
	BIGINT("BIGINT", Types.BIGINT), //

	REAL("REAL", Types.REAL), //
	DOUBLE("DOUBLE", Types.DOUBLE), //
	FLOAT("FLOAT", Types.FLOAT), //
	DECIMAL("DECIMAL", Types.DECIMAL, 30, 5), //
	NUMERIC("NUMERIC", Types.NUMERIC, 30, 0), //
	DATE("DATE", Types.DATE), //
	TIME("TIME", Types.TIME), //
	TIMESTAMP("TIMESTAMP", Types.TIMESTAMP), //
	DATETIME("DATETIME", Types.TIMESTAMP), //

	CHAR("CHAR", Types.CHAR, 1, null), //
	CHAR_BINARY("CHAR", Types.CHAR, 1, null, true), //
	VARCHAR("VARCHAR", Types.VARCHAR, 255, null), //
	VARCHAR_BINARY("VARCHAR", Types.VARCHAR, 255, null, true), //
	BINARY("BINARY", Types.BINARY, 255, null), //
	VARBINARY("VARBINARY", Types.VARBINARY, 255, null), //
	BLOB("BLOB", Types.BLOB), //
	LONGBLOB("LONGBLOB", Types.BLOB), //

	TINYTEXT("TINYTEXT", Types.VARCHAR), //
	TINYTEXT_BINARY("TINYTEXT", Types.VARCHAR, null, null, true), //
	TEXT("TEXT", Types.LONGVARCHAR), //
	TEXT_BINARY("TEXT", Types.LONGVARCHAR, null, null, true), //
	LONGTEXT("LONGTEXT", Types.LONGVARCHAR), //
	LONGTEXT_BINARY("LONGTEXT", Types.LONGVARCHAR, null, null, true);

	public final String sqlValue;
	public final int jdbcValue;
	public final Integer defaultLength;
	public final Integer defaultDecimals;
	public final boolean binary; // 是否后加binary

	public static final String BINARY_SUFFIX = "BINARY";

	private SqlType(String sqlValue, int jdbcValue) {
		this(sqlValue, jdbcValue, null, null);
	}

	private SqlType(String sqlValue, int jdbcValue, Integer defaultLength, Integer defaultDecimals) {
		this(sqlValue, jdbcValue, defaultLength, defaultDecimals, false);
	}

	private SqlType(String sqlValue, int jdbcValue, Integer defaultLength, Integer defaultDecimals, boolean binary) {
		this.sqlValue = sqlValue;
		this.jdbcValue = jdbcValue;
		this.defaultLength = defaultLength;
		this.defaultDecimals = defaultDecimals;
		this.binary = binary;
	}
}

package com.github.obase.mysql;

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
	DECIMAL("DECIMAL", Types.DECIMAL), //
	NUMERIC("NUMERIC", Types.NUMERIC), //
	DATE("DATE", Types.DATE), //
	TIME("TIME", Types.TIME), //
	TIMESTAMP("TIMESTAMP", Types.TIMESTAMP), //
	DATETIME("DATETIME", Types.TIMESTAMP), //

	CHAR("CHAR", Types.CHAR), //
	CHAR_BINARY("CHAR BINARY", Types.CHAR), //
	VARCHAR("VARCHAR", Types.VARCHAR), //
	VARCHAR_BINARY("VARCHAR BINARY", Types.VARCHAR), //
	BINARY("BINARY", Types.BINARY), //
	VARBINARY("VARBINARY", Types.VARBINARY), //
	BLOB("BLOB", Types.BLOB), //
	LONGBLOB("LONGBLOB", Types.BLOB), //

	TINYTEXT("TINYTEXT", Types.VARCHAR), //
	TINYTEXT_BINARY("TINYTEXT BINARY", Types.VARCHAR), //
	TEXT("TEXT", Types.LONGVARCHAR), //
	TEXT_BINARY("TEXT BINARY", Types.LONGVARCHAR), //
	LONGTEXT("LONGTEXT", Types.LONGVARCHAR), //
	LONGTEXT_BINARY("LONGTEXT BINARY", Types.LONGVARCHAR);

	public final String sqlValue;
	public final int jdbcValue;

	private SqlType(String sqlValue, int jdbcValue) {
		this.sqlValue = sqlValue;
		this.jdbcValue = jdbcValue;
	}
}

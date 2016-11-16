package com.github.obase.mysql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;

import org.springframework.asm.Type;

/**
 * The scalar type supported by mysqlClient, and its default sqlType, length and decimals for creating table.
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public enum JavaType {

	_boolean(boolean.class, SqlType.BIT, 1, null), //
	_Boolean(Boolean.class, SqlType.BIT, 1, null), //
	_char(char.class, SqlType.CHAR, 1, null), //
	_Character(Character.class, SqlType.CHAR, 1, null), //
	_byte(byte.class, SqlType.TINYINT, null, null), //
	_Byte(Byte.class, SqlType.TINYINT, null, null), //
	_short(short.class, SqlType.SMALLINT, null, null), //
	_Short(Short.class, SqlType.SMALLINT, null, null), //
	_int(int.class, SqlType.INT, null, null), //
	_Integer(Integer.class, SqlType.INT, null, null), //
	_long(long.class, SqlType.BIGINT, null, null), //
	_Long(Long.class, SqlType.BIGINT, null, null), //
	_float(float.class, SqlType.FLOAT, null, null), //
	_Float(Float.class, SqlType.FLOAT, null, null), //
	_double(double.class, SqlType.DOUBLE, null, null), //
	_Double(Double.class, SqlType.DOUBLE, null, null), //
	_String(String.class, SqlType.VARCHAR, 255, null), //
	_BigDecimal(BigDecimal.class, SqlType.DECIMAL, 30, 5), //
	_BigInteger(BigInteger.class, SqlType.NUMERIC, 30, null), //
	_JavaUtilDate(java.util.Date.class, SqlType.DATETIME, null, null), //
	_Date(Date.class, SqlType.DATE, null, null), //
	_Time(Time.class, SqlType.TIME, null, null), //
	_Timestamp(Timestamp.class, SqlType.TIMESTAMP, null, null), //
	_bytes(byte[].class, SqlType.VARBINARY, null, null), //
	_Ref(Ref.class, SqlType.VARCHAR, 255, null), //
	_URL(URL.class, SqlType.VARCHAR, 255, null), //
	_SQLXML(SQLXML.class, SqlType.TEXT, null, null), //
	_Blob(Blob.class, SqlType.BLOB, null, null), //
	_Clob(Clob.class, SqlType.TEXT, null, null), //
	_InputStream(InputStream.class, SqlType.LONGBLOB, null, null), //
	_Reader(Reader.class, SqlType.LONGTEXT, null, null), //
	_Object(Object.class, null, null, null);

	public final Class<?> clazz;
	public final String descriptor;
	public final SqlType defaultSqlType;
	public final Integer defaultLength;
	public final Integer defaultDecimals;

	private JavaType(Class<?> clazz, SqlType defaultSqlType, Integer defaultLength, Integer defaultDecimals) {
		this.clazz = clazz;
		this.descriptor = Type.getDescriptor(clazz);
		this.defaultSqlType = defaultSqlType;
		this.defaultLength = defaultLength;
		this.defaultDecimals = defaultDecimals;
	}

	public static JavaType match(String descriptor) {
		for (JavaType type : values()) {
			if (type.descriptor.equals(descriptor)) {
				return type;
			}
		}
		return _Object;
	}

	public static JavaType match(Class<?> clazz) {
		for (JavaType type : values()) {
			if (type.clazz == clazz) {
				return type;
			}
		}
		return _Object;
	}

}

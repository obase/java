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
import java.util.HashMap;
import java.util.Map;

import org.springframework.asm.Type;

import com.github.obase.mysql.annotation.SqlType;

/**
 * The scalar type supported by mysqlClient, and its default sqlType, length and decimals for creating table.
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public enum JavaType {

	_boolean(boolean.class, SqlType.BIT), //
	_Boolean(Boolean.class, SqlType.BIT), //
	_char(char.class, SqlType.CHAR), //
	_Character(Character.class, SqlType.CHAR), //
	_byte(byte.class, SqlType.TINYINT), //
	_Byte(Byte.class, SqlType.TINYINT), //
	_short(short.class, SqlType.SMALLINT), //
	_Short(Short.class, SqlType.SMALLINT), //
	_int(int.class, SqlType.INT), //
	_Integer(Integer.class, SqlType.INT), //
	_long(long.class, SqlType.BIGINT), //
	_Long(Long.class, SqlType.BIGINT), //
	_float(float.class, SqlType.FLOAT), //
	_Float(Float.class, SqlType.FLOAT), //
	_double(double.class, SqlType.DOUBLE), //
	_Double(Double.class, SqlType.DOUBLE), //
	_String(String.class, SqlType.VARCHAR), //
	_BigDecimal(BigDecimal.class, SqlType.DECIMAL), //
	_BigInteger(BigInteger.class, SqlType.NUMERIC), //
	_JavaUtilDate(java.util.Date.class, SqlType.DATETIME), //
	_Date(Date.class, SqlType.DATE), //
	_Time(Time.class, SqlType.TIME), //
	_Timestamp(Timestamp.class, SqlType.TIMESTAMP), //
	_bytes(byte[].class, SqlType.VARBINARY), //
	_Ref(Ref.class, SqlType.VARCHAR), //
	_URL(URL.class, SqlType.VARCHAR), //
	_SQLXML(SQLXML.class, SqlType.TEXT), //
	_Blob(Blob.class, SqlType.BLOB), //
	_Clob(Clob.class, SqlType.TEXT), //
	_InputStream(InputStream.class, SqlType.LONGBLOB), //
	_Reader(Reader.class, SqlType.LONGTEXT), //
	_Object(Object.class, null);

	public final Class<?> clazz;
	public final String descriptor;
	public final SqlType defaultSqlType;

	private JavaType(Class<?> clazz, SqlType defaultSqlType) {
		this.clazz = clazz;
		this.descriptor = Type.getDescriptor(clazz);
		this.defaultSqlType = defaultSqlType;
	}

	static final Map<Class<?>, JavaType> IDX1 = new HashMap<Class<?>, JavaType>();
	static final Map<String, JavaType> IDX2 = new HashMap<String, JavaType>();
	static {
		for (JavaType jt : JavaType.values()) {
			IDX1.put(jt.clazz, jt);
			IDX2.put(jt.descriptor, jt);
		}
	}

	public static JavaType match(Class<?> clazz) {
		JavaType type = IDX1.get(clazz);
		return type != null ? type : JavaType._Object;
	}

	public static JavaType match(String desc) {
		JavaType type = IDX2.get(desc);
		return type != null ? type : JavaType._Object;
	}

}

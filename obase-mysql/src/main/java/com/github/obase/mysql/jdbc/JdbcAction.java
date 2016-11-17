package com.github.obase.mysql.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cglib.beans.BeanMap;

import com.github.obase.mysql.JavaType;

/**
 * <ul>
 * <li>boolean
 * <li>byte
 * <li>short
 * <li>int
 * <li>long
 * <li>float
 * <li>double
 * <li>------------
 * <li>wrapping class
 * <li>------------
 * <li>String
 * <li>BigDecimal
 * <li>BigInteger
 * 
 * <li>Date
 * <li>Time
 * <li>Timestamp
 * <li>UnitDate
 * <li>----------
 * <li>byte[]
 * <li>InputStream
 * <li>Reader
 * <li>Blob
 * <li>Clob
 * <li>----------
 * <li>Ref
 * <li>URL
 * <li>SQLXML
 * <li>---------
 * <li>Object
 * </ul>
 */
public abstract class JdbcAction {

	public abstract void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException;

	public abstract Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException;

	public abstract void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException;

	public static final void set_boolean(PreparedStatement pstmt, int[] pos, boolean value) throws SQLException {
		for (int p : pos) {
			pstmt.setBoolean(p, value);
		}
	}

	public static final void set_char(PreparedStatement pstmt, int[] pos, char value) throws SQLException {
		for (int p : pos) {
			pstmt.setString(p, String.valueOf(value));
		}
	}

	public static final void set_byte(PreparedStatement pstmt, int[] pos, byte value) throws SQLException {
		for (int p : pos) {
			pstmt.setByte(p, value);
		}
	}

	public static final void set_short(PreparedStatement pstmt, int[] pos, short value) throws SQLException {
		for (int p : pos) {
			pstmt.setShort(p, value);
		}
	}

	public static final void set_int(PreparedStatement pstmt, int[] pos, int value) throws SQLException {
		for (int p : pos) {
			pstmt.setInt(p, value);
		}
	}

	public static final void set_long(PreparedStatement pstmt, int[] pos, long value) throws SQLException {
		for (int p : pos) {
			pstmt.setLong(p, value);
		}
	}

	public static final void set_float(PreparedStatement pstmt, int[] pos, float value) throws SQLException {
		for (int p : pos) {
			pstmt.setFloat(p, value);
		}
	}

	public static final void set_double(PreparedStatement pstmt, int[] pos, double value) throws SQLException {
		for (int p : pos) {
			pstmt.setDouble(p, value);
		}
	}

	public static final void set_Boolean(PreparedStatement pstmt, int[] pos, Boolean value) throws SQLException {

		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setBoolean(p, value);
			}
		}
	}

	public static final void set_Character(PreparedStatement pstmt, int[] pos, Character value) throws SQLException {

		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setString(p, value.toString());
			}
		}
	}

	public static final void set_Byte(PreparedStatement pstmt, int[] pos, Byte value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setByte(p, value);
			}
		}
	}

	public static final void set_Short(PreparedStatement pstmt, int[] pos, Short value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setShort(p, value);
			}
		}
	}

	public static final void set_Integer(PreparedStatement pstmt, int[] pos, Integer value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setInt(p, value);
			}
		}
	}

	public static final void set_Long(PreparedStatement pstmt, int[] pos, Long value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setLong(p, value);
			}
		}
	}

	public static final void set_Float(PreparedStatement pstmt, int[] pos, Float value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setFloat(p, value);
			}
		}
	}

	public static final void set_Double(PreparedStatement pstmt, int[] pos, Double value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setDouble(p, value);
			}
		}
	}

	public static final void set_String(PreparedStatement pstmt, int[] pos, String value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setString(p, value);
			}
		}
	}

	public static final void set_BigDecimal(PreparedStatement pstmt, int[] pos, BigDecimal value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setBigDecimal(p, value);
			}
		}
	}

	public static final void set_BigInteger(PreparedStatement pstmt, int[] pos, BigInteger value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setBigDecimal(p, new BigDecimal(value));
			}
		}
	}

	public static final void set_JavaUtilDate(PreparedStatement pstmt, int[] pos, java.util.Date value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			Timestamp ts = new Timestamp(value.getTime());
			for (int p : pos) {
				pstmt.setTimestamp(p, ts);
			}
		}
	}

	public static final void set_Date(PreparedStatement pstmt, int[] pos, Date value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setDate(p, value);
			}
		}
	}

	public static final void set_Time(PreparedStatement pstmt, int[] pos, Time value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setTime(p, value);
			}
		}
	}

	public static final void set_Timestamp(PreparedStatement pstmt, int[] pos, Timestamp value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setTimestamp(p, value);
			}
		}
	}

	public static final void set_bytes(PreparedStatement pstmt, int[] pos, byte[] value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setBytes(p, value);
			}
		}
	}

	public static final void set_InputStream(PreparedStatement pstmt, int[] pos, InputStream value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setBinaryStream(p, value);
			}
		}
	}

	public static final void set_Reader(PreparedStatement pstmt, int[] pos, Reader value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setCharacterStream(p, value);
			}
		}
	}

	public static final void set_Blob(PreparedStatement pstmt, int[] pos, Blob value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setBlob(p, value);
			}
		}
	}

	public static final void set_Clob(PreparedStatement pstmt, int[] pos, Clob value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setClob(p, value);
			}
		}
	}

	public static final void set_Ref(PreparedStatement pstmt, int[] pos, Ref value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setRef(p, value);
			}
		}
	}

	public static final void set_URL(PreparedStatement pstmt, int[] pos, URL value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setURL(p, value);
			}
		}
	}

	public static final void set_SQLXML(PreparedStatement pstmt, int[] pos, SQLXML value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			for (int p : pos) {
				pstmt.setSQLXML(p, value);
			}
		}
	}

	public static final void set_Object(PreparedStatement pstmt, int[] pos, Object value) throws SQLException {
		if (value == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
		} else {
			ActionMeta setter = ActionMetaCache.get(value.getClass());
			if (setter != null) {
				setter.set(pstmt, pos, value);
			} else {
				for (int p : pos) {
					pstmt.setObject(p, value);
				}
			}
		}
	}

	public static final boolean get_boolean(ResultSet rs, Integer pos) throws SQLException {
		return rs.getBoolean(pos);
	}

	public static final char get_char(ResultSet rs, Integer pos) throws SQLException {
		String val = rs.getString(pos);
		if (val != null && val.length() > 0) {
			return val.charAt(0);
		}
		return '\0';
	}

	public static final byte get_byte(ResultSet rs, Integer pos) throws SQLException {
		return rs.getByte(pos);
	}

	public static final short get_short(ResultSet rs, Integer pos) throws SQLException {
		return rs.getShort(pos);
	}

	public static final int get_int(ResultSet rs, Integer pos) throws SQLException {
		return rs.getInt(pos);
	}

	public static final long get_long(ResultSet rs, Integer pos) throws SQLException {
		return rs.getLong(pos);
	}

	public static final float get_float(ResultSet rs, Integer pos) throws SQLException {
		return rs.getFloat(pos);
	}

	public static final double get_double(ResultSet rs, Integer pos) throws SQLException {
		return rs.getDouble(pos);
	}

	public static final Boolean get_Boolean(ResultSet rs, Integer pos) throws SQLException {
		boolean result = rs.getBoolean(pos);
		return rs.wasNull() ? null : result;
	}

	public static final Character get_Character(ResultSet rs, Integer pos) throws SQLException {
		String val = rs.getString(pos);
		if (val != null && val.length() > 0) {
			return val.charAt(0);
		}
		return null;
	}

	public static final Byte get_Byte(ResultSet rs, Integer pos) throws SQLException {
		byte result = rs.getByte(pos);
		return rs.wasNull() ? null : result;
	}

	public static final Short get_Short(ResultSet rs, Integer pos) throws SQLException {
		short result = rs.getShort(pos);
		return rs.wasNull() ? null : result;
	}

	public static final Integer get_Integer(ResultSet rs, Integer pos) throws SQLException {
		int result = rs.getInt(pos);
		return rs.wasNull() ? null : result;
	}

	public static final Long get_Long(ResultSet rs, Integer pos) throws SQLException {
		long result = rs.getLong(pos);
		return rs.wasNull() ? null : result;
	}

	public static final Float get_Float(ResultSet rs, Integer pos) throws SQLException {
		float result = rs.getFloat(pos);
		return rs.wasNull() ? null : result;
	}

	public static final Double get_Double(ResultSet rs, Integer pos) throws SQLException {
		double result = rs.getDouble(pos);
		return rs.wasNull() ? null : result;
	}

	public static final String get_String(ResultSet rs, Integer pos) throws SQLException {
		return rs.getString(pos);
	}

	public static final BigDecimal get_BigDecimal(ResultSet rs, Integer pos) throws SQLException {
		return rs.getBigDecimal(pos);
	}

	public static final BigInteger get_BigInteger(ResultSet rs, Integer pos) throws SQLException {
		BigDecimal val = rs.getBigDecimal(pos);
		return val == null ? null : val.toBigInteger();
	}

	public static final Date get_Date(ResultSet rs, Integer pos) throws SQLException {
		return rs.getDate(pos);
	}

	public static final Time get_Time(ResultSet rs, Integer pos) throws SQLException {
		return rs.getTime(pos);
	}

	public static final Timestamp get_Timestamp(ResultSet rs, Integer pos) throws SQLException {
		return rs.getTimestamp(pos);
	}

	public static final java.util.Date get_JavaUtilDate(ResultSet rs, Integer pos) throws SQLException {
		return rs.getTimestamp(pos);
	}

	public static final byte[] get_bytes(ResultSet rs, Integer pos) throws SQLException {
		return rs.getBytes(pos);
	}

	public static final InputStream get_InputStream(ResultSet rs, Integer pos) throws SQLException {
		return rs.getBinaryStream(pos);
	}

	public static final Reader get_Reader(ResultSet rs, Integer pos) throws SQLException {
		return rs.getCharacterStream(pos);
	}

	public static final Blob get_Blob(ResultSet rs, Integer pos) throws SQLException {
		return rs.getBlob(pos);
	}

	public static final Clob get_Clob(ResultSet rs, Integer pos) throws SQLException {
		return rs.getClob(pos);
	}

	public static final Ref get_Ref(ResultSet rs, Integer pos) throws SQLException {
		return rs.getRef(pos);
	}

	public static final URL get_URL(ResultSet rs, Integer pos) throws SQLException {
		return rs.getURL(pos);
	}

	public static final SQLXML get_SQLXML(ResultSet rs, Integer pos) throws SQLException {
		return rs.getSQLXML(pos);
	}

	@SuppressWarnings("unchecked")
	public static final <T> T get_Object(ResultSet rs, Integer pos, Class<T> type) throws SQLException {

		ActionMeta getter = ActionMetaCache.get(type);
		if (getter != null) {
			return getter.get(rs, pos, type);
		}
		return (T) rs.getObject(pos);
	}

	public static Map<String, Integer> parseColumnLabels(ResultSet rs) throws SQLException {

		Map<String, Integer> result = new HashMap<String, Integer>();

		ResultSetMetaData rsmd = rs.getMetaData();
		int count = rsmd.getColumnCount();
		for (int i = 1; i <= count; i++) { // base from 1
			result.put(rsmd.getColumnLabel(i), i);
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	static final Map<Class, ActionMeta> ActionMetaCache = new ConcurrentHashMap<Class, ActionMeta>();

	@SuppressWarnings("rawtypes")
	public static void markSqlType(Class type, ActionMeta sqlType) {
		if (!ActionMetaCache.containsKey(type)) {
			synchronized (ActionMetaCache) {
				if (!ActionMetaCache.containsKey(sqlType)) {
					ActionMetaCache.put(type, sqlType);
				}
			}
		}
	}

	public static void setParamByType(PreparedStatement pstmt, int[] pos, Object param) throws SQLException {

		if (param == null) {
			for (int p : pos) {
				pstmt.setNull(p, Types.OTHER);
			}
			return;
		}

		switch (JavaType.match(param.getClass())) {
		case _boolean:
		case _Boolean:
			set_Boolean(pstmt, pos, (Boolean) param);
			break;
		case _char:
		case _Character:
			set_Character(pstmt, pos, (Character) param);
			break;
		case _byte:
		case _Byte:
			set_Byte(pstmt, pos, (Byte) param);
			break;
		case _short:
		case _Short:
			set_Short(pstmt, pos, (Short) param);
			break;
		case _int:
		case _Integer:
			set_Integer(pstmt, pos, (Integer) param);
			break;
		case _long:
		case _Long:
			set_Long(pstmt, pos, (Long) param);
			break;
		case _float:
		case _Float:
			set_Float(pstmt, pos, (Float) param);
			break;
		case _double:
		case _Double:
			set_Double(pstmt, pos, (Double) param);
			break;
		case _String:
			set_String(pstmt, pos, (String) param);
			break;
		case _BigDecimal:
			set_BigDecimal(pstmt, pos, (BigDecimal) param);
			break;
		case _BigInteger:
			set_BigInteger(pstmt, pos, (BigInteger) param);
			break;
		case _JavaUtilDate:
			set_JavaUtilDate(pstmt, pos, (java.util.Date) param);
			break;
		case _Date:
			set_Date(pstmt, pos, (Date) param);
			break;
		case _Time:
			set_Time(pstmt, pos, (Time) param);
			break;
		case _Timestamp:
			set_Timestamp(pstmt, pos, (Timestamp) param);
			break;
		case _bytes:
			set_bytes(pstmt, pos, (byte[]) param);
			break;
		case _Ref:
			set_Ref(pstmt, pos, (Ref) param);
			break;
		case _URL:
			set_URL(pstmt, pos, (URL) param);
			break;
		case _SQLXML:
			set_SQLXML(pstmt, pos, (SQLXML) param);
			break;
		case _Blob:
			set_Blob(pstmt, pos, (Blob) param);
			break;
		case _Clob:
			set_Clob(pstmt, pos, (Clob) param);
			break;
		case _InputStream:
			set_InputStream(pstmt, pos, (InputStream) param);
			break;
		case _Reader:
			set_Reader(pstmt, pos, (Reader) param);
			break;
		case _Object:
			set_Object(pstmt, pos, param);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public static <R> R getResultByType(ResultSet rs, int pos, Class<R> type) throws SQLException {
		switch (JavaType.match(type)) {
		case _boolean:
		case _Boolean:
			return (R) get_Boolean(rs, pos);
		case _char:
		case _Character:
			return (R) get_Character(rs, pos);
		case _byte:
		case _Byte:
			return (R) get_Byte(rs, pos);
		case _short:
		case _Short:
			return (R) get_Short(rs, pos);
		case _int:
		case _Integer:
			return (R) get_Integer(rs, pos);
		case _long:
		case _Long:
			return (R) get_Long(rs, pos);
		case _float:
		case _Float:
			return (R) get_Float(rs, pos);
		case _double:
		case _Double:
			return (R) get_Double(rs, pos);
		case _String:
			return (R) get_String(rs, pos);
		case _BigDecimal:
			return (R) get_BigDecimal(rs, pos);
		case _BigInteger:
			return (R) get_BigInteger(rs, pos);
		case _JavaUtilDate:
			return (R) get_JavaUtilDate(rs, pos);
		case _Date:
			return (R) get_Date(rs, pos);
		case _Time:
			return (R) get_Time(rs, pos);
		case _Timestamp:
			return (R) get_Timestamp(rs, pos);
		case _bytes:
			return (R) get_bytes(rs, pos);
		case _Ref:
			return (R) get_Ref(rs, pos);
		case _URL:
			return (R) get_URL(rs, pos);
		case _SQLXML:
			return (R) get_SQLXML(rs, pos);
		case _Blob:
			return (R) get_Blob(rs, pos);
		case _Clob:
			return (R) get_Clob(rs, pos);
		case _InputStream:
			return (R) get_InputStream(rs, pos);
		case _Reader:
			return (R) get_Reader(rs, pos);
		case _Object:
			return (R) get_Object(rs, pos, type);
		}
		return null;
	}

	public static final JdbcAction MAP_JDBC_ACTION = new JdbcAction() {

		@SuppressWarnings("rawtypes")
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Map map;
				if (obj instanceof Map) {
					map = (Map) obj;
				} else {
					map = BeanMap.create(obj);
				}
				for (Map.Entry<String, int[]> entry : params.entrySet()) {
					setParamByType(pstmt, entry.getValue(), map.get(entry.getKey()));
				}
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			Map map;
			if (obj instanceof Map) {
				map = (Map) obj;
			} else {
				map = BeanMap.create(obj);
			}
			for (Map.Entry<String, Integer> entry : types.entrySet()) {
				map.put(entry.getKey(), rs.getObject(entry.getValue()));
			}
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			Map<String, Object> map = new HashMap<String, Object>(types.size());
			for (Map.Entry<String, Integer> entry : types.entrySet()) {
				map.put(entry.getKey(), rs.getObject(entry.getValue()));
			}
			return map;
		}
	};

	public static final JdbcAction LIST_JDBC_ACTION = new JdbcAction() {

		@SuppressWarnings("rawtypes")
		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				List list = (List) obj;
				int size = list.size();
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p <= size) {
							setParamByType(pstmt, new int[] { p }, list.get(p - 1));
						} else {
							pstmt.setNull(p, Types.OTHER);
						}
					}
				}
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			int size = types.size();
			List list = (List) obj;
			for (int i = 0; i < size; i++) {
				list.add(rs.getObject(i + 1));
			}
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			int size = types.size();
			List list = new ArrayList(size);
			for (int i = 0; i < size; i++) {
				list.add(rs.getObject(i + 1));
			}
			return list;
		}
	};

	public static final JdbcAction ARRAY_JDBC_ACTION = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Object[] array = (Object[]) obj;
				int size = array.length;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p <= size) {
							setParamByType(pstmt, new int[] { p }, array[p - 1]);
						} else {
							pstmt.setNull(p, Types.OTHER);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			int size = types.size();
			Object[] array = (Object[]) obj;
			for (int i = 0; i < size && i < array.length; i++) {
				array[i] = rs.getObject(i + 1);
			}
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			int size = types.size();
			Object[] array = new Object[size];
			for (int i = 0; i < size; i++) {
				array[i] = rs.getObject(i + 1);
			}
			return array;
		}
	};

	public static JdbcAction BooleanJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Boolean val = (Boolean) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setBoolean(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Boolean(rs, 1);
		}
	};

	public static JdbcAction CharacterJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Character val = (Character) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setString(p, val.toString());
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Character(rs, 1);
		}
	};

	public static JdbcAction ByteJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Byte val = (Byte) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setByte(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Byte(rs, 1);
		}
	};

	public static JdbcAction ShortJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Short val = (Short) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setShort(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Short(rs, 1);
		}
	};

	public static JdbcAction IntegerJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Integer val = (Integer) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setInt(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Integer(rs, 1);
		}
	};

	public static JdbcAction LongJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Long val = (Long) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setLong(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Long(rs, 1);
		}
	};

	public static JdbcAction FloatJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Float val = (Float) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setFloat(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Float(rs, 1);
		}
	};

	public static JdbcAction DoubleJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Double val = (Double) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setDouble(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Double(rs, 1);
		}
	};

	public static JdbcAction StringJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				String val = (String) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setString(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_String(rs, 1);
		}
	};

	public static JdbcAction BigDecimalJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				BigDecimal val = (BigDecimal) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setBigDecimal(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_BigDecimal(rs, 1);
		}
	};

	public static JdbcAction BigIntegerJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				BigInteger val = (BigInteger) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setBytes(p, val.toByteArray());
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_BigInteger(rs, 1);
		}
	};

	public static JdbcAction JavaUtilDateJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				java.util.Date val = (java.util.Date) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setTimestamp(p, new Timestamp(val.getTime()));
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_JavaUtilDate(rs, 1);
		}
	};

	public static JdbcAction DateJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Date val = (Date) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setDate(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Date(rs, 1);
		}
	};

	public static JdbcAction TimeJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Time val = (Time) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setTime(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Time(rs, 1);
		}
	};

	public static JdbcAction TimestampJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Timestamp val = (Timestamp) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setTimestamp(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Timestamp(rs, 1);
		}
	};

	public static JdbcAction RefJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Ref val = (Ref) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setRef(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Ref(rs, 1);
		}
	};

	public static JdbcAction URLJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				URL val = (URL) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setURL(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_URL(rs, 1);
		}
	};

	public static JdbcAction SQLXMLJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				SQLXML val = (SQLXML) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setSQLXML(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_SQLXML(rs, 1);
		}
	};

	public static JdbcAction BlobJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Blob val = (Blob) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setBlob(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Blob(rs, 1);
		}
	};

	public static JdbcAction ClobJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Clob val = (Clob) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setClob(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Clob(rs, 1);
		}
	};

	public static JdbcAction BytesJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				byte[] val = (byte[]) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setBytes(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_bytes(rs, 1);
		}
	};

	public static JdbcAction InputStreamJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				InputStream val = (InputStream) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setBinaryStream(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_InputStream(rs, 1);
		}
	};

	public static JdbcAction ReaderJdbcAction = new JdbcAction() {

		@Override
		public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
			if (params.size() > 0) {
				Reader val = (Reader) obj;
				for (int[] pos : params.values()) {
					for (int p : pos) {
						if (p != 1 || val == null) {
							pstmt.setNull(p, Types.OTHER);
						} else {
							pstmt.setCharacterStream(p, val);
						}
					}
				}
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return get_Reader(rs, 1);
		}
	};
}

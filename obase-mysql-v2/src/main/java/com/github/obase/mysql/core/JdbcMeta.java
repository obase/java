package com.github.obase.mysql.core;

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
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.obase.MessageException;
import com.github.obase.WrappedException;
import com.github.obase.mysql.MysqlErrno;
import com.github.obase.mysql.asm.AsmKit;

/**
 * 执行JDBC的元数据类
 *
 */
@SuppressWarnings({})
public abstract class JdbcMeta {

	/***************************************************
	 * 接口抽象方法
	 ***************************************************/

	public abstract Object getValue(Object obj, String name);

	// 与V1版本相比变化在于细粒度化
	public abstract void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException;

	public abstract Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException;

	public abstract void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException;

	// =============================================
	// 常用实例
	// =============================================

	@SuppressWarnings("unchecked")
	public static final JdbcMeta MAP = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return ((Map<String, Object>) obj).get(name);
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			setParamByType(ps, pos, ((Map<String, Object>) obj).get(name));
		}

		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			Map<String, Object> map = (Map<String, Object>) obj;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final JdbcMeta LIST = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			List list = (List) obj;
			if (pos <= list.size()) {
				setParamByType(ps, pos, list.get(pos - 1));
			} else {
				ps.setNull(pos, Types.OTHER);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			List list = (List) obj;
			int size = types.size();
			for (int i = 0; i < size; i++) {
				list.add(rs.getObject(i + 1));
			}
		}

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

	public static final JdbcMeta ARRAY = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Object[] array = (Object[]) obj;
			if (pos <= array.length) {
				setParamByType(ps, pos, array[pos - 1]);
			} else {
				ps.setNull(pos, Types.OTHER);
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

	public static JdbcMeta Boolean = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Boolean val = (Boolean) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setBoolean(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			boolean ret = rs.getBoolean(1);
			return rs.wasNull() ? null : ret;
		}

	};

	public static JdbcMeta Character = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Character val = (Character) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setString(pos, val.toString());
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			String ret = rs.getString(1);
			return ret == null || ret.length() == 0 ? null : ret.charAt(0);
		}

	};

	public static JdbcMeta Byte = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Boolean val = (Boolean) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setBoolean(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			byte ret = rs.getByte(1);
			return rs.wasNull() ? null : ret;
		}

	};

	public static JdbcMeta Short = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Short val = (Short) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setShort(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			short ret = rs.getShort(1);
			return rs.wasNull() ? null : ret;
		}

	};

	public static JdbcMeta Integer = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Integer val = (Integer) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setInt(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			int ret = rs.getInt(1);
			return rs.wasNull() ? null : ret;
		}

	};

	public static JdbcMeta Long = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Long val = (Long) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setLong(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			long ret = rs.getLong(1);
			return rs.wasNull() ? null : ret;
		}

	};

	public static JdbcMeta Float = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Float val = (Float) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setFloat(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			float ret = rs.getInt(1);
			return rs.wasNull() ? null : ret;
		}

	};

	public static JdbcMeta Double = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Double val = (Double) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setDouble(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			double ret = rs.getDouble(1);
			return rs.wasNull() ? null : ret;
		}
	};

	public static JdbcMeta String = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			String val = (String) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setString(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getString(1);
		}

	};

	public static JdbcMeta BigDecimal = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			BigDecimal val = (BigDecimal) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setBigDecimal(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getBigDecimal(1);
		}

	};

	public static JdbcMeta BigInteger = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			BigInteger val = (BigInteger) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setBigDecimal(pos, new BigDecimal(val));
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			BigDecimal val = rs.getBigDecimal(1);
			return val == null ? null : val.toBigInteger();
		}

	};

	public static JdbcMeta JavaUtilDate = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			java.util.Date val = (java.util.Date) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setTimestamp(pos, new Timestamp(val.getTime()));
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			Date ret = rs.getDate(1);
			return ret == null ? null : new java.util.Date(ret.getTime());
		}

	};

	public static JdbcMeta Date = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Date val = (Date) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setDate(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getDate(1);
		}

	};

	public static JdbcMeta Time = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Time val = (Time) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setTime(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getTime(1);
		}

	};

	public static JdbcMeta Timestamp = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Timestamp val = (Timestamp) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setTimestamp(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getTimestamp(1);
		}
	};

	public static JdbcMeta Ref = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Ref val = (Ref) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setRef(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getRef(1);
		}

	};

	public static JdbcMeta URL = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			URL val = (URL) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setURL(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getURL(1);
		}

	};

	public static JdbcMeta SQLXML = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			SQLXML val = (SQLXML) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setSQLXML(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getSQLXML(1);
		}
	};

	public static JdbcMeta Blob = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Blob val = (Blob) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setBlob(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getBlob(1);
		}
	};

	public static JdbcMeta Clob = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Clob val = (Clob) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setClob(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getClob(1);
		}
	};

	public static JdbcMeta Bytes = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			byte[] val = (byte[]) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setBytes(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getBytes(1);
		}
	};

	public static JdbcMeta InputStream = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			InputStream val = (InputStream) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setBinaryStream(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getBinaryStream(1);
		}
	};

	public static JdbcMeta Reader = new JdbcMeta() {

		@Override
		public Object getValue(Object obj, java.lang.String name) {
			return null;
		}

		@Override
		public void setParam(PreparedStatement ps, int pos, Object obj, String name) throws SQLException {
			Reader val = (Reader) obj;
			if (pos != 1 || val == null) {
				ps.setNull(pos, Types.OTHER);
			} else {
				ps.setCharacterStream(pos, val);
			}
		}

		@Override
		public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
			// nohting
		}

		@Override
		public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
			return rs.getCharacterStream(1);
		}
	};

	// =============================================
	// 辅助方法
	// =============================================
	public static void setParamByType(PreparedStatement pstmt, int pos, Object param) throws SQLException {

		if (param == null) {
			pstmt.setNull(pos, Types.OTHER);
			return;
		}

		// 高频排前
		Class<?> type = param.getClass();
		if (type == String.class) {
			pstmt.setString(pos, (String) param);
		} else if (type == Integer.class || type == int.class) {
			pstmt.setInt(pos, (Integer) param);
		} else if (type == Long.class || type == long.class) {
			pstmt.setLong(pos, (Long) param);
		} else if (type == Double.class || type == double.class) {
			pstmt.setDouble(pos, (Double) param);
		} else if (type == Float.class || type == float.class) {
			pstmt.setFloat(pos, (Float) param);
		} else if (type == Short.class || type == short.class) {
			pstmt.setShort(pos, (Short) param);
		} else if (type == java.util.Date.class) {
			pstmt.setDate(pos, new Date(((java.util.Date) param).getTime()));
		} else if (type == Date.class) {
			pstmt.setDate(pos, (Date) param);
		} else if (type == Boolean.class || type == boolean.class) {
			pstmt.setBoolean(pos, (Boolean) param);
		} else if (type == Character.class || type == char.class) {
			pstmt.setString(pos, param.toString());
		} else if (type == Byte.class || type == byte.class) {
			pstmt.setByte(pos, (Byte) param);
		} else if (type == BigDecimal.class) {
			pstmt.setBigDecimal(pos, (BigDecimal) param);
		} else if (type == BigInteger.class) {
			pstmt.setBigDecimal(pos, new BigDecimal((BigInteger) param));
		} else if (type == Time.class) {
			pstmt.setTime(pos, (Time) param);
		} else if (type == Timestamp.class) {
			pstmt.setTimestamp(pos, (Timestamp) param);
		} else if (type == byte[].class) {
			pstmt.setBytes(pos, (byte[]) param);
		} else if (type == Ref.class) {
			pstmt.setRef(pos, (Ref) param);
		} else if (type == URL.class) {
			pstmt.setURL(pos, (URL) param);
		} else if (type == SQLXML.class) {
			pstmt.setSQLXML(pos, (SQLXML) param);
		} else if (type == Blob.class) {
			pstmt.setBlob(pos, (Blob) param);
		} else if (type == Clob.class) {
			pstmt.setClob(pos, (Clob) param);
		} else if (type == InputStream.class) {
			pstmt.setBinaryStream(pos, (InputStream) param);
		} else if (type == Reader.class) {
			pstmt.setCharacterStream(pos, (Reader) param);
		} else {
			JavaTypeMeta meta = JavaTypeMeta.get(type);
			if (meta != null) {
				meta.set(pstmt, pos, param);
			} else {
				pstmt.setObject(pos, param);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <R> R getResultByType(ResultSet rs, int pos, Class<R> type) throws SQLException {

		if (type == String.class) {
			return (R) rs.getString(pos);
		} else if (type == Integer.class || type == int.class) {
			int ret = rs.getInt(pos);
			return (R) (rs.wasNull() ? null : ret);
		} else if (type == Long.class || type == long.class) {
			long ret = rs.getLong(pos);
			return (R) (rs.wasNull() ? null : ret);
		} else if (type == Double.class || type == double.class) {
			double ret = rs.getDouble(pos);
			return (R) (rs.wasNull() ? null : ret);
		} else if (type == Float.class || type == float.class) {
			float ret = rs.getFloat(pos);
			return (R) (rs.wasNull() ? null : ret);
		} else if (type == Short.class || type == short.class) {
			short ret = rs.getShort(pos);
			return (R) (rs.wasNull() ? null : ret);
		} else if (type == java.util.Date.class) {
			Date ret = rs.getDate(pos);
			return (R) (ret == null ? null : new java.util.Date(ret.getTime()));
		} else if (type == Date.class) {
			return (R) rs.getDate(pos);
		} else if (type == Boolean.class || type == boolean.class) {
			boolean ret = rs.getBoolean(pos);
			return (R) (rs.wasNull() ? null : ret);
		} else if (type == Character.class || type == char.class) {
			String ret = rs.getString(pos);
			return (R) (ret == null ? null : ret.charAt(0));
		} else if (type == Byte.class || type == byte.class) {
			byte ret = rs.getByte(pos);
			return (R) (rs.wasNull() ? null : ret);
		} else if (type == BigDecimal.class) {
			return (R) rs.getBigDecimal(pos);
		} else if (type == BigInteger.class) {
			BigDecimal ret = rs.getBigDecimal(pos);
			return (R) (ret == null ? null : ret.toBigInteger());
		} else if (type == Time.class) {
			return (R) rs.getTime(pos);
		} else if (type == Timestamp.class) {
			return (R) rs.getTimestamp(pos);
		} else if (type == byte[].class) {
			return (R) rs.getBytes(pos);
		} else if (type == Ref.class) {
			return (R) rs.getRef(pos);
		} else if (type == URL.class) {
			return (R) rs.getURL(pos);
		} else if (type == SQLXML.class) {
			return (R) rs.getSQLXML(pos);
		} else if (type == Blob.class) {
			return (R) rs.getBlob(pos);
		} else if (type == Clob.class) {
			return (R) rs.getClob(pos);
		} else if (type == InputStream.class) {
			return (R) rs.getBinaryStream(pos);
		} else if (type == Reader.class) {
			return (R) rs.getCharacterStream(pos);
		} else {
			JavaTypeMeta meta = JavaTypeMeta.get(type);
			if (meta != null) {
				return meta.get(rs, pos, type);
			} else {
				return (R) rs.getObject(pos);
			}
		}
	}

	// =============================================
	// 缓存属性: JdbcMetaCache
	// =============================================
	static final Map<Class<?>, JdbcMeta> CACHE = new HashMap<Class<?>, JdbcMeta>(JavaType.values().length + 8);
	static {
		// scalar jdbcAction
		CACHE.put(boolean.class, Boolean);
		CACHE.put(Boolean.class, Boolean);
		CACHE.put(char.class, Character);
		CACHE.put(Character.class, Character);
		CACHE.put(byte.class, Byte);
		CACHE.put(Byte.class, Byte);
		CACHE.put(short.class, Short);
		CACHE.put(Short.class, Short);
		CACHE.put(int.class, Integer);
		CACHE.put(Integer.class, Integer);
		CACHE.put(long.class, Long);
		CACHE.put(Long.class, Long);
		CACHE.put(float.class, Float);
		CACHE.put(Float.class, Float);
		CACHE.put(double.class, Double);
		CACHE.put(String.class, String);
		CACHE.put(Double.class, Double);
		CACHE.put(BigDecimal.class, BigDecimal);
		CACHE.put(BigInteger.class, BigInteger);
		CACHE.put(java.util.Date.class, JavaUtilDate);
		CACHE.put(Date.class, Date);
		CACHE.put(Time.class, Time);
		CACHE.put(Timestamp.class, Timestamp);
		CACHE.put(Ref.class, Ref);
		CACHE.put(URL.class, URL);
		CACHE.put(SQLXML.class, SQLXML);
		CACHE.put(Blob.class, Blob);
		CACHE.put(Clob.class, Clob);
		CACHE.put(InputStream.class, InputStream);
		CACHE.put(Reader.class, Reader);
		CACHE.put(byte[].class, Bytes);
	}

	public static JdbcMeta get(Class<?> type) {

		if (type == null) {
			return ARRAY;
		} else if (Map.class.isAssignableFrom(type)) {
			return MAP;
		} else if (List.class.isAssignableFrom(type)) {
			return LIST;
		}
		JdbcMeta meta = CACHE.get(type);
		if (meta == null) {
			if (type.isArray() || type.isEnum() || type.isInterface() || type.isAnnotation()) {
				throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.JDBC_META_NOT_SUPPORTED, "JdbcMeta don't support array, enum, interface, or annoation type:" + type.getCanonicalName());
			}
			synchronized (CACHE) {
				meta = CACHE.get(type);
				if (meta == null) {
					try {
						meta = AsmKit.newJdbcMeta(type.getCanonicalName());
					} catch (Exception e) {
						throw new WrappedException(e);
					}
				}
			}

		}
		return meta;

	}
}

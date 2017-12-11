package com.github.obase.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Action meta operation for a type
 */
public abstract class JavaTypeMeta {

	public abstract <T> void set(PreparedStatement pstmt, int pos, T value) throws SQLException;

	public abstract <T> T get(ResultSet rs, Integer pos, Class<T> type) throws SQLException;

	// ===========================================
	// 缓存属性
	// ===========================================
	static final Map<Class<?>, JavaTypeMeta> CACHE = new ConcurrentHashMap<Class<?>, JavaTypeMeta>();

	public static void set(Class<?> type, JavaTypeMeta meta) {
		CACHE.put(type, meta);
	}

	public static JavaTypeMeta get(Class<?> type) {
		return CACHE.get(type);
	}

}

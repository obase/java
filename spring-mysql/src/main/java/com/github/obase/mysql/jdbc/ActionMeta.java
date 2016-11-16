package com.github.obase.mysql.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Action meta operation for a type
 */
public interface ActionMeta {

	<T> void set(PreparedStatement pstmt, int[] pos, T value) throws SQLException;

	<T> T get(ResultSet rs, Integer pos, Class<T> type) throws SQLException;

}

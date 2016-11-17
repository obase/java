package com.github.obase.mysql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Callback interface for connection
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public interface ConnectionCallback<T> {

	T doInConnection(Connection conn) throws SQLException;

}

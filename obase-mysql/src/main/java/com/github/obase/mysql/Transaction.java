package com.github.obase.mysql;

import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * The connection transaction interface
 * 
 * @author hezhaowu
 * @since 0.9.1
 */
public interface Transaction extends MysqlClient {

	void commit() throws SQLException;

	void rollback() throws SQLException;

	void rollback(Savepoint savepoint) throws SQLException;

	Savepoint setSavepoint() throws SQLException;

	Savepoint setSavepoint(String name) throws SQLException;

	void releaseSavepoint(Savepoint savepoint) throws SQLException;

}

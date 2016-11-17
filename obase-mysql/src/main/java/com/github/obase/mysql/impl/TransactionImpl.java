package com.github.obase.mysql.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import com.github.obase.mysql.ConnectionCallback;
import com.github.obase.mysql.Page;
import com.github.obase.mysql.Transaction;

final class TransactionImpl implements Transaction {

	final Connection conn;
	final MysqlClientOperation proxy;

	TransactionImpl(Connection conn, MysqlClientOperation proxy) {
		this.conn = conn;
		this.proxy = proxy;
	}

	@Override
	public <T> T callback(ConnectionCallback<T> callback) throws SQLException {
		return proxy.callback(conn, callback);
	}

	@Override
	public <T> int insert(T tableObject) throws SQLException {
		return proxy.insert(conn, tableObject);
	}

	@Override
	public <T> int insertIgnore(T tableObject) throws SQLException {
		return proxy.insertIgnore(conn, tableObject);
	}

	@Override
	public <T, R> R insert(T tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.insert(conn, tableObject, generatedKeyType);
	}

	@Override
	public <T, R> R insertIgnore(T tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.insertIgnore(conn, tableObject, generatedKeyType);
	}

	@Override
	public <T> int update(T tableObject) throws SQLException {
		return proxy.update(conn, tableObject);
	}

	@Override
	public <T> int replace(T tableObject) throws SQLException {
		return proxy.replace(conn, tableObject);
	}

	@Override
	public <T, R> R replace(T tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.replace(conn, tableObject, generatedKeyType);
	}

	@Override
	public <T> int merge(T tableObject) throws SQLException {
		return proxy.merge(conn, tableObject);
	}

	@Override
	public <T, R> R merge(T tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.merge(conn, tableObject, generatedKeyType);
	}

	@Override
	public <T> int delete(T tableObject) throws SQLException {
		return proxy.delete(conn, tableObject);
	}

	@Override
	public <T> int deleteByKey(Class<T> tableType, Object... keys) throws SQLException {
		return proxy.deleteByKey(conn, tableType, keys);
	}

	@Override
	public <T> T select(T tableObject) throws SQLException {
		return proxy.select(conn, tableObject);
	}

	@Override
	public <T> T select2(T tableObject) throws SQLException {
		return proxy.select2(conn, tableObject);
	}

	@Override
	public <T> T selectByKey(Class<T> tableType, Object... keys) throws SQLException {
		return proxy.selectByKey(conn, tableType, keys);
	}

	@Override
	public <T> List<T> selectAll(Class<T> tableType) throws SQLException {
		return proxy.selectAll(conn, tableType);
	}

	@Override
	public <T> List<T> selectRange(Class<T> tableType, int start, int max) throws SQLException {
		return proxy.selectRange(conn, tableType, start, max);
	}

	@Override
	public <T> T selectFirst(Class<T> tableType) throws SQLException {
		return proxy.selectFirst(conn, tableType);
	}

	@Override
	public <T> void selectPage(Class<T> tableType, Page<T> pabe) throws SQLException {
		proxy.selectPage(conn, tableType, pabe);
	}

	@Override
	public <T> int[] batchInsert(T[] tableObject) throws SQLException {
		return proxy.batchInsert(conn, tableObject);
	}

	@Override
	public <T> int[] batchInsertIgnore(T[] tableObject) throws SQLException {
		return proxy.batchInsertIgnore(conn, tableObject);
	}

	@Override
	public <T, R> R[] batchInsert(T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.batchInsert(conn, tableObject, generatedKeyType);
	}

	@Override
	public <T, R> R[] batchInsertIgnore(T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.batchInsertIgnore(conn, tableObject, generatedKeyType);
	}

	@Override
	public <T> int[] batchUpdate(T[] tableObject) throws SQLException {
		return proxy.batchUpdate(conn, tableObject);
	}

	@Override
	public <T> int[] batchReplace(T[] tableObject) throws SQLException {
		return proxy.batchReplace(conn, tableObject);
	}

	@Override
	public <T, R> R[] batchReplace(T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.batchReplace(conn, tableObject, generatedKeyType);
	}

	@Override
	public <T> int[] batchMerge(T[] tableObject) throws SQLException {
		return proxy.batchMerge(conn, tableObject);
	}

	@Override
	public <T, R> R[] batchMerge(T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.batchMerge(conn, tableObject, generatedKeyType);
	}

	@Override
	public <T> int[] batchDelete(T[] tableObjects) throws SQLException {
		return proxy.batchDelete(conn, tableObjects);
	}

	@Override
	public <T> int[] batchDeleteByKey(Class<T> tableType, Object[][] tableObjects) throws SQLException {
		return proxy.batchDeleteByKey(conn, tableType, tableObjects);
	}

	@Override
	public <T> List<T> query(String queryId, Class<T> elemType, Object params) throws SQLException {
		return proxy.query(conn, queryId, elemType, params);
	}

	@Override
	public <T> List<T> queryRange(String queryId, Class<T> elemType, int start, int max, Object params) throws SQLException {
		return proxy.queryRange(conn, queryId, elemType, start, max, params);
	}

	@Override
	public <T> T queryFirst(String queryId, Class<T> elemType, Object params) throws SQLException {
		return proxy.queryFirst(conn, queryId, elemType, params);
	}

	@Override
	public <T> void queryPage(String queryId, Class<T> elemType, Page<T> page, Object params) throws SQLException {
		proxy.queryPage(conn, queryId, elemType, page, params);
	}

	@Override
	public int execute(String updateId, Object params) throws SQLException {
		return proxy.execute(conn, updateId, params);
	}

	@Override
	public <R> R execute(String updateId, Object params, Class<R> generatedKeyType) throws SQLException {
		return proxy.execute(conn, updateId, params, generatedKeyType);
	}

	@Override
	public int[] batchExecute(String updateId, Object[] params) throws SQLException {
		return proxy.batchExecute(conn, updateId, params);
	}

	@Override
	public <T, R> R[] batchExecute(String updateId, T[] params, Class<R> generatedKeyType) throws SQLException {
		return proxy.batchExecute(conn, updateId, params, generatedKeyType);
	}

	@Override
	public void commit() throws SQLException {
		try {
			conn.commit();
		} finally {
			conn.close();
		}
	}

	@Override
	public void rollback() throws SQLException {
		try {
			conn.rollback();
		} finally {
			conn.close();
		}
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		conn.rollback(savepoint);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return conn.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		return conn.setSavepoint(name);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		conn.releaseSavepoint(savepoint);
	}

	@Override
	public void init() throws SQLException {
		conn.setAutoCommit(false);
	}

}

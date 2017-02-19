package com.github.obase.mysql.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.Map;

import com.github.obase.Page;
import com.github.obase.mysql.ConnectionCallback;
import com.github.obase.mysql.Transaction;

final class TransactionImpl implements Transaction {

	final Connection conn;
	final MysqlClientOperation proxy;

	TransactionImpl(Connection conn, MysqlClientOperation proxy) {
		this.conn = conn;
		this.proxy = proxy;
	}

	@Override
	public <T> T callback(ConnectionCallback<T> callback, Object... params) throws SQLException {
		return proxy.callback(conn, callback);
	}

	@Override
	public <T> int insert(Class<?> tableType, T tableObject) throws SQLException {
		return proxy.insert(conn, tableType, tableObject);
	}

	@Override
	public <T> int insertIgnore(Class<?> tableType, T tableObject) throws SQLException {
		return proxy.insertIgnore(conn, tableType, tableObject);
	}

	@Override
	public <T, R> R insert(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.insert(conn, tableType, tableObject, generatedKeyType);
	}

	@Override
	public <T, R> R insertIgnore(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.insertIgnore(conn, tableType, tableObject, generatedKeyType);
	}

	@Override
	public <T> int update(Class<?> tableType, T tableObject) throws SQLException {
		return proxy.update(conn, tableType, tableObject);
	}

	@Override
	public <T> int replace(Class<?> tableType, T tableObject) throws SQLException {
		return proxy.replace(conn, tableType, tableObject);
	}

	@Override
	public <T, R> R replace(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.replace(conn, tableType, tableObject, generatedKeyType);
	}

	@Override
	public <T> int merge(Class<?> tableType, T tableObject) throws SQLException {
		return proxy.merge(conn, tableType, tableObject);
	}

	@Override
	public <T, R> R merge(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.merge(conn, tableType, tableObject, generatedKeyType);
	}

	@Override
	public <T> int delete(Class<?> tableType, T tableObject) throws SQLException {
		return proxy.delete(conn, tableType, tableObject);
	}

	@Override
	public <T> int deleteByKey(Class<T> tableType, Object... keys) throws SQLException {
		return proxy.deleteByKey(conn, tableType, keys);
	}

	@Override
	public <T> T select(Class<?> tableType, T tableObject) throws SQLException {
		return proxy.select(conn, tableType, tableObject);
	}

	@Override
	public <T> T select2(Class<?> tableType, T tableObject) throws SQLException {
		return proxy.select2(conn, tableType, tableObject);
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
	public <T> int[] batchInsert(Class<?> tableType, T[] tableObject) throws SQLException {
		return proxy.batchInsert(conn, tableType, tableObject);
	}

	@Override
	public <T> int[] batchInsertIgnore(Class<?> tableType, T[] tableObject) throws SQLException {
		return proxy.batchInsertIgnore(conn, tableType, tableObject);
	}

	@Override
	public <T, R> R[] batchInsert(Class<?> tableType, T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.batchInsert(conn, tableType, tableObject, generatedKeyType);
	}

	@Override
	public <T, R> R[] batchInsertIgnore(Class<?> tableType, T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.batchInsertIgnore(conn, tableType, tableObject, generatedKeyType);
	}

	@Override
	public <T> int[] batchUpdate(Class<?> tableType, T[] tableObject) throws SQLException {
		return proxy.batchUpdate(conn, tableType, tableObject);
	}

	@Override
	public <T> int[] batchReplace(Class<?> tableType, T[] tableObject) throws SQLException {
		return proxy.batchReplace(conn, tableType, tableObject);
	}

	@Override
	public <T, R> R[] batchReplace(Class<?> tableType, T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.batchReplace(conn, tableType, tableObject, generatedKeyType);
	}

	@Override
	public <T> int[] batchMerge(Class<?> tableType, T[] tableObject) throws SQLException {
		return proxy.batchMerge(conn, tableType, tableObject);
	}

	@Override
	public <T, R> R[] batchMerge(Class<?> tableType, T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		return proxy.batchMerge(conn, tableType, tableObject, generatedKeyType);
	}

	@Override
	public <T> int[] batchDelete(Class<?> tableType, T[] tableObjects) throws SQLException {
		return proxy.batchDelete(conn, tableType, tableObjects);
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

	@Override
	public <T> int insert(T tableObject) throws SQLException {
		return insert(tableObject.getClass(), tableObject);
	}

	@Override
	public <T> int insertIgnore(T tableObject) throws SQLException {
		return insertIgnore(tableObject.getClass(), tableObject);
	}

	@Override
	public <T, R> R insert(T tableObject, Class<R> generatedKeyType) throws SQLException {
		return insert(tableObject.getClass(), tableObject, generatedKeyType);
	}

	@Override
	public <T, R> R insertIgnore(T tableObject, Class<R> generatedKeyType) throws SQLException {
		return insertIgnore(tableObject.getClass(), tableObject, generatedKeyType);
	}

	@Override
	public <T> int update(T tableObject) throws SQLException {
		return update(tableObject.getClass(), tableObject);
	}

	@Override
	public <T> int replace(T tableObject) throws SQLException {
		return replace(tableObject.getClass(), tableObject);
	}

	@Override
	public <T, R> R replace(T tableObject, Class<R> generatedKeyType) throws SQLException {
		return replace(tableObject.getClass(), tableObject, generatedKeyType);
	}

	@Override
	public <T> int merge(T tableObject) throws SQLException {
		return merge(tableObject.getClass(), tableObject);
	}

	@Override
	public <T, R> R merge(T tableObject, Class<R> generatedKeyType) throws SQLException {
		return merge(tableObject.getClass(), tableObject, generatedKeyType);
	}

	@Override
	public <T> int delete(T tableObject) throws SQLException {
		return delete(tableObject.getClass(), tableObject);
	}

	@Override
	public <T> T select(T tableObject) throws SQLException {
		return select(tableObject.getClass(), tableObject);
	}

	@Override
	public <T> T select2(T tableObject) throws SQLException {
		return select2(tableObject.getClass(), tableObject);
	}

	@Override
	public <T> int[] batchInsert(T[] tableObjects) throws SQLException {
		return batchInsert(tableObjects[0].getClass(), tableObjects);
	}

	@Override
	public <T> int[] batchInsertIgnore(T[] tableObjects) throws SQLException {
		return batchInsertIgnore(tableObjects[0].getClass(), tableObjects);
	}

	@Override
	public <T, R> R[] batchInsert(T[] tableObjects, Class<R> generatedKeyType) throws SQLException {
		return batchInsert(tableObjects[0].getClass(), tableObjects, generatedKeyType);
	}

	@Override
	public <T, R> R[] batchInsertIgnore(T[] tableObjects, Class<R> generatedKeyType) throws SQLException {
		return batchInsertIgnore(tableObjects[0].getClass(), tableObjects, generatedKeyType);
	}

	@Override
	public <T> int[] batchUpdate(T[] tableObjects) throws SQLException {
		return batchUpdate(tableObjects[0].getClass(), tableObjects);
	}

	@Override
	public <T> int[] batchReplace(T[] tableObjects) throws SQLException {
		return batchReplace(tableObjects[0].getClass(), tableObjects);
	}

	@Override
	public <T, R> R[] batchReplace(T[] tableObjects, Class<R> generatedKeyType) throws SQLException {
		return batchReplace(tableObjects[0].getClass(), tableObjects, generatedKeyType);
	}

	@Override
	public <T> int[] batchMerge(T[] tableObjects) throws SQLException {
		return batchMerge(tableObjects[0].getClass(), tableObjects);
	}

	@Override
	public <T, R> R[] batchMerge(T[] tableObjects, Class<R> generatedKeyType) throws SQLException {
		return batchMerge(tableObjects[0].getClass(), tableObjects, generatedKeyType);
	}

	@Override
	public <T> int[] batchDelete(T[] tableObjects) throws SQLException {
		return batchDelete(tableObjects[0].getClass(), tableObjects);
	}

	@Override
	public <T> List<T> queryExtc(String queryId, Class<T> elemType, Map<String, Object> params) throws SQLException {
		return proxy.queryExtc(conn, queryId, elemType, params);
	}

	@Override
	public <T> List<T> queryRangeExtc(String queryId, Class<T> elemType, int start, int max, Map<String, Object> params) throws SQLException {
		return proxy.queryRangeExtc(conn, queryId, elemType, start, max, params);
	}

	@Override
	public <T> T queryFirstExtc(String queryId, Class<T> elemType, Map<String, Object> params) throws SQLException {
		return proxy.queryFirstExtc(conn, queryId, elemType, params);
	}

	@Override
	public <T> void queryPageExtc(String queryId, Class<T> elemType, Page<T> page, Map<String, Object> params) throws SQLException {
		proxy.queryPageExtc(conn, queryId, elemType, page, params);
	}
}

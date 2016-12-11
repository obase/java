package com.github.obase.mysql.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.obase.mysql.ConnectionCallback;
import com.github.obase.mysql.MysqlClientTxn;
import com.github.obase.Page;
import com.github.obase.mysql.Transaction;

public class MysqlClientTxnImpl extends MysqlClientOperation implements MysqlClientTxn {

	@Override
	public Transaction beginTransaction() throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			TransactionImpl tx = new TransactionImpl(conn, this);
			tx.init();
			return tx;
		} catch (SQLException e) {
			if (conn != null) {
				conn.close();
			}
			throw e;
		}
	}

	@Override
	public <T> T callback(ConnectionCallback<T> callback, Object... params) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			T result = callback(conn, callback);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T> int insert(Class<?> tableType, T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return insert(conn, tableType, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int insertIgnore(Class<?> tableType, T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return insertIgnore(conn, tableType, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R insert(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return insert(conn, tableType, tableObject, generatedKeyType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R insertIgnore(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return insertIgnore(conn, tableType, tableObject, generatedKeyType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int update(Class<?> tableType, T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return update(conn, tableType, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int replace(Class<?> tableType, T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return replace(conn, tableType, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R replace(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return replace(conn, tableType, tableObject, generatedKeyType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int merge(Class<?> tableType, T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return merge(conn, tableType, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R merge(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return merge(conn, tableType, tableObject, generatedKeyType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int delete(Class<?> tableType, T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return delete(conn, tableType, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int deleteByKey(Class<T> tableType, Object... keys) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return deleteByKey(conn, tableType, keys);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> T select(Class<?> tableType, T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return select(conn, tableType, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> T select2(Class<?> tableType, T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return select2(conn, tableType, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> T selectByKey(Class<T> tableType, Object... keys) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return selectByKey(tableType, keys);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> List<T> selectAll(Class<T> tableType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return selectAll(conn, tableType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> List<T> selectRange(Class<T> tableType, int start, int max) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return selectRange(conn, tableType, start, max);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> T selectFirst(Class<T> tableType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return selectFirst(conn, tableType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> void selectPage(Class<T> tableType, Page<T> page) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			selectPage(conn, tableType, page);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int[] batchInsert(Class<?> tableType, T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchInsert(conn, tableType, tableObject);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T> int[] batchInsertIgnore(Class<?> tableType, T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchInsertIgnore(conn, tableType, tableObject);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R[] batchInsert(Class<?> tableType, T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			R[] result = batchInsert(conn, tableType, tableObject, generatedKeyType);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R[] batchInsertIgnore(Class<?> tableType, T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			R[] result = batchInsertIgnore(conn, tableType, tableObject, generatedKeyType);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T> int[] batchUpdate(Class<?> tableType, T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchUpdate(conn, tableType, tableObject);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T> int[] batchReplace(Class<?> tableType, T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchReplace(conn, tableType, tableObject);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R[] batchReplace(Class<?> tableType, T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			R[] result = batchReplace(conn, tableType, tableObject, generatedKeyType);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T> int[] batchMerge(Class<?> tableType, T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchMerge(conn, tableType, tableObject);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R[] batchMerge(Class<?> tableType, T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			R[] result = batchMerge(conn, tableType, tableObject, generatedKeyType);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T> int[] batchDelete(Class<?> tableType, T[] tableObjects) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchDelete(conn, tableType, tableObjects);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T> int[] batchDeleteByKey(Class<T> tableType, Object[][] keys) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchDeleteByKey(conn, tableType, keys);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T> List<T> query(String queryId, Class<T> elemType, Object params) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return query(conn, queryId, elemType, params);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> List<T> queryRange(String queryId, Class<T> elemType, int start, int max, Object params) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return queryRange(conn, queryId, elemType, start, max, params);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> T queryFirst(String queryId, Class<T> elemType, Object params) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return queryFirst(conn, queryId, elemType, params);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> void queryPage(String queryId, Class<T> elemType, Page<T> page, Object params) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			queryPage(conn, queryId, elemType, page, params);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public int execute(String updateId, Object params) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return execute(conn, updateId, params);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <R> R execute(String updateId, Object params, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return execute(conn, updateId, params, generatedKeyType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public int[] batchExecute(String updateId, Object[] params) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchExecute(updateId, params);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R[] batchExecute(String updateId, T[] params, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			R[] result = batchExecute(conn, updateId, params, generatedKeyType);
			done = true;
			return result;
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
	}

	@Override
	public void init() throws Exception {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			init(conn);
		} finally {
			if (conn != null) {
				if (done) {
					conn.commit();
				} else {
					conn.rollback();
				}
				conn.close();
			}
		}
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

}

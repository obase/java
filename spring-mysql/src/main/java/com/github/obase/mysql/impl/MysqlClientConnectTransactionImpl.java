package com.github.obase.mysql.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.obase.mysql.ConnectionCallback;
import com.github.obase.mysql.MysqlClientExt;
import com.github.obase.mysql.Page;
import com.github.obase.mysql.Transaction;

public class MysqlClientConnectTransactionImpl extends MysqlClientOperation implements MysqlClientExt {

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
	public <T> T callback(ConnectionCallback<T> callback) throws SQLException {
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
	public <T> int insert(T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return insert(conn, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int insertIgnore(T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return insertIgnore(conn, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R insert(T tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return insert(conn, tableObject, generatedKeyType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R insertIgnore(T tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return insertIgnore(conn, tableObject, generatedKeyType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int update(T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return update(conn, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int replace(T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return replace(conn, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R replace(T tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return replace(conn, tableObject, generatedKeyType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int merge(T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return merge(conn, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T, R> R merge(T tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return merge(conn, tableObject, generatedKeyType);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> int delete(T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return delete(conn, tableObject);
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
	public <T> T select(T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return select(conn, tableObject);
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	@Override
	public <T> T select2(T tableObject) throws SQLException {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			return select2(conn, tableObject);
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
	public <T> int[] batchInsert(T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchInsert(conn, tableObject);
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
	public <T> int[] batchInsertIgnore(T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchInsertIgnore(conn, tableObject);
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
	public <T, R> R[] batchInsert(T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			R[] result = batchInsert(conn, tableObject, generatedKeyType);
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
	public <T, R> R[] batchInsertIgnore(T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			R[] result = batchInsertIgnore(conn, tableObject, generatedKeyType);
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
	public <T> int[] batchUpdate(T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchUpdate(conn, tableObject);
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
	public <T> int[] batchReplace(T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchReplace(conn, tableObject);
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
	public <T, R> R[] batchReplace(T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			R[] result = batchReplace(conn, tableObject, generatedKeyType);
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
	public <T> int[] batchMerge(T[] tableObject) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchMerge(conn, tableObject);
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
	public <T, R> R[] batchMerge(T[] tableObject, Class<R> generatedKeyType) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			R[] result = batchMerge(conn, tableObject, generatedKeyType);
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
	public <T> int[] batchDelete(T[] tableObjects) throws SQLException {
		Connection conn = null;
		boolean done = false;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			int[] result = batchDelete(conn, tableObjects);
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

}

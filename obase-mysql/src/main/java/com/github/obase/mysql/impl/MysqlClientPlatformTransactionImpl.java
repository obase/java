package com.github.obase.mysql.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.github.obase.mysql.ConnectionCallback;
import com.github.obase.mysql.MysqlClient;
import com.github.obase.mysql.Page;
import com.github.obase.spring.transaction.DataSourceUtils;

public class MysqlClientPlatformTransactionImpl extends MysqlClientOperation implements MysqlClient {

	@Override
	public <T> T callback(ConnectionCallback<T> action) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return action.doInConnection(conn);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int insert(T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return insert(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int insertIgnore(T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return insertIgnore(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> R insert(T tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return insert(conn, tableObject, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> R insertIgnore(T tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return insertIgnore(conn, tableObject, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int update(T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return update(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int replace(T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return replace(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> R replace(T tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return replace(conn, tableObject, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int merge(T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return merge(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> R merge(T tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return merge(conn, tableObject, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int delete(T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return delete(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int deleteByKey(Class<T> tableType, Object... keys) throws SQLException {
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return deleteByKey(conn, tableType, keys);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> T select(T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return select(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> T select2(T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return select2(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> T selectByKey(Class<T> tableType, Object... keys) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return selectByKey(conn, tableType, keys);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> List<T> selectAll(Class<T> tableType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return selectAll(conn, tableType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> List<T> selectRange(Class<T> tableType, int start, int max) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return selectRange(conn, tableType, start, max);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> T selectFirst(Class<T> tableType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return selectFirst(conn, tableType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> void selectPage(Class<T> tableType, Page<T> page) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			selectPage(conn, tableType, page);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int[] batchInsert(T[] tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchInsert(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int[] batchInsertIgnore(T[] tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchInsertIgnore(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> R[] batchInsert(T[] tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchInsert(conn, tableObject, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> R[] batchInsertIgnore(T[] tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchInsertIgnore(conn, tableObject, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int[] batchUpdate(T[] tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchUpdate(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int[] batchReplace(T[] tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchReplace(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> R[] batchReplace(T[] tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchReplace(conn, tableObject, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int[] batchMerge(T[] tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchMerge(conn, tableObject);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> R[] batchMerge(T[] tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchMerge(conn, tableObject, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int[] batchDelete(T[] tableObjects) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchDelete(conn, tableObjects);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int[] batchDeleteByKey(Class<T> tableType, Object[][] keys) throws SQLException {
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchDeleteByKey(conn, tableType, keys);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> List<T> query(String queryId, Class<T> elemType, Object params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return query(conn, queryId, elemType, params);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> List<T> queryRange(String queryId, Class<T> elemType, int start, int max, Object params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return queryRange(conn, queryId, elemType, start, max, params);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> T queryFirst(String queryId, Class<T> elemType, Object params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return queryFirst(conn, queryId, elemType, params);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> void queryPage(String queryId, Class<T> elemType, Page<T> page, Object params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			queryPage(conn, queryId, elemType, page, params);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public int execute(String updateId, Object params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return execute(conn, updateId, params);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <R> R execute(String updateId, Object params, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return execute(conn, updateId, params, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public int[] batchExecute(String updateId, Object[] params) throws SQLException {
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchExecute(conn, updateId, params);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> R[] batchExecute(String updateId, T[] params, Class<R> generatedKeyType) throws SQLException {
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchExecute(conn, updateId, params, generatedKeyType);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public void init() throws Exception {
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			conn = dataSource.getConnection();
			init(conn);
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

}

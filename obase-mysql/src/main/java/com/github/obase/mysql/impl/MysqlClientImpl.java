package com.github.obase.mysql.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.github.obase.Page;
import com.github.obase.mysql.ConnectionCallback;
import com.github.obase.mysql.MysqlClient;
import com.github.obase.spring.transaction.DataSourceUtils;

public class MysqlClientImpl extends MysqlClientOperation implements MysqlClient {

	@Override
	public <T> T callback(ConnectionCallback<T> action, Object... params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return action.doInConnection(conn, params);
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
	public <T> int insert(Class<?> tableType, T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return insert(conn, tableType, tableObject);
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
	public <T> int insertIgnore(Class<?> tableType, T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return insertIgnore(conn, tableType, tableObject);
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
	public <T, R> R insert(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return insert(conn, tableType, tableObject, generatedKeyType);
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
	public <T, R> R insertIgnore(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return insertIgnore(conn, tableType, tableObject, generatedKeyType);
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
	public <T> int update(Class<?> tableType, T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return update(conn, tableType, tableObject);
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
	public <T> int replace(Class<?> tableType, T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return replace(conn, tableType, tableObject);
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
	public <T, R> R replace(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return replace(conn, tableType, tableObject, generatedKeyType);
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
	public <T> int merge(Class<?> tableType, T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return merge(conn, tableType, tableObject);
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
	public <T, R> R merge(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return merge(conn, tableType, tableObject, generatedKeyType);
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
	public <T> int delete(Class<?> tableType, T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return delete(conn, tableType, tableObject);
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
	public <T> T select(Class<?> tableType, T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return select(conn, tableType, tableObject);
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
	public <T> T select2(Class<?> tableType, T tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return select2(conn, tableType, tableObject);
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
	public <T> int[] batchInsert(Class<?> tableType, T[] tableObject) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchInsert(conn, tableType, tableObject);
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
	public <T> int[] batchInsertIgnore(Class<?> tableType, T[] tableObjects) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchInsertIgnore(conn, tableType, tableObjects);
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
	public <T, R> R[] batchInsert(Class<?> tableType, T[] tableObjects, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchInsert(conn, tableType, tableObjects, generatedKeyType);
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
	public <T, R> R[] batchInsertIgnore(Class<?> tableType, T[] tableObjects, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchInsertIgnore(conn, tableType, tableObjects, generatedKeyType);
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
	public <T> int[] batchUpdate(Class<?> tableType, T[] tableObjects) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchUpdate(conn, tableType, tableObjects);
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
	public <T> int[] batchReplace(Class<?> tableType, T[] tableObjects) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchReplace(conn, tableType, tableObjects);
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
	public <T, R> R[] batchReplace(Class<?> tableType, T[] tableObjects, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchReplace(conn, tableType, tableObjects, generatedKeyType);
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
	public <T> int[] batchMerge(Class<?> tableType, T[] tableObjects) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchMerge(conn, tableType, tableObjects);
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
	public <T, R> R[] batchMerge(Class<?> tableType, T[] tableObjects, Class<R> generatedKeyType) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchMerge(conn, tableType, tableObjects, generatedKeyType);
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
	public <T> int[] batchDelete(Class<?> tableType, T[] tableObjects) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			if (conn.getAutoCommit()) {
				conn.setAutoCommit(false);
			}
			return batchDelete(conn, tableType, tableObjects);
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
	public <T> List<T> queryWithCollects(String queryId, Class<T> elemType, Map<String, Object> params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return queryWithCollects(conn, queryId, elemType, params);
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
	public <T> List<T> queryRangeWithCollects(String queryId, Class<T> elemType, int start, int max, Map<String, Object> params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return queryRangeWithCollects(conn, queryId, elemType, start, max, params);
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
	public <T> T queryFirstWithCollects(String queryId, Class<T> elemType, Map<String, Object> params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			return queryFirstWithCollects(conn, queryId, elemType, params);
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
	public <T> void queryPageWithCollects(String queryId, Class<T> elemType, Page<T> page, Map<String, Object> params) throws SQLException {

		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			queryPageWithCollects(conn, queryId, elemType, page, params);
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

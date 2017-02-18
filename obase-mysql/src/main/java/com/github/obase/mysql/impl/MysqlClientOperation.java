package com.github.obase.mysql.impl;

import static com.github.obase.kit.StringKit.isNotEmpty;

import java.io.InputStream;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.github.obase.mysql.ConnectionCallback;
import com.github.obase.mysql.MysqlErrno;
import com.github.obase.MessageException;
import com.github.obase.Page;
import com.github.obase.WrappedException;
import com.github.obase.kit.ClassKit;
import com.github.obase.kit.MapKit;
import com.github.obase.mysql.asm.AsmKit;
import com.github.obase.mysql.config.ConfigMetaInfo;
import com.github.obase.mysql.config.ConfigSAXParser;
import com.github.obase.mysql.data.ClassMetaInfo;
import com.github.obase.mysql.jdbc.JdbcAction;
import com.github.obase.mysql.jdbc.SqlDdlKit;
import com.github.obase.mysql.jdbc.SqlMeta;
import com.github.obase.mysql.jdbc.SqlMetaKit;

@SuppressWarnings("rawtypes")
abstract class MysqlClientOperation {

	private static final Log logger = LogFactory.getLog(MysqlClientOperation.class);

	public static final String DIRECTION_DESC = "DESC";

	/*
	 * Config properties
	 */
	DataSource dataSource;
	String packagesToScan; // multi-value separated by comma ","
	String configLocations; // multi-value separated by comma ","
	boolean showSql; // show sql or not
	boolean checkConfig; // check sql config or not, not used now
	boolean updateTable; // update table or not

	/*
	 * Properties
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setPackagesToScan(String packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	public void setConfigLocations(String configLocations) {
		this.configLocations = configLocations;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public void setUpdateTable(boolean updateTable) {
		this.updateTable = updateTable;
	}

	public void setCheckConfig(boolean checkConfig) {
		this.checkConfig = checkConfig;
	}

	/*
	 * Entity operations
	 */

	protected <T> T callback(Connection conn, ConnectionCallback<T> callback) throws SQLException {
		return callback.doInConnection(conn);
	}

	protected <T> int insert(Connection conn, Class<?> tableType, T tableObject) throws SQLException {

		SqlMeta meta = insertSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for insert: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			action.setParam(pstmt, meta.params, tableObject);
			return pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int insertIgnore(Connection conn, Class<?> tableType, T tableObject) throws SQLException {

		SqlMeta meta = insertSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		}

		String psql = SqlMetaKit.modifyPsqlForInsertIgnore(meta);
		if (showSql) {
			logger.info("Sql for insertIgnore: " + meta.toString(psql));
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(psql);
			action.setParam(pstmt, meta.params, tableObject);
			return pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T, R> R insert(Connection conn, Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = insertSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for insert: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql, Statement.RETURN_GENERATED_KEYS);
			action.setParam(pstmt, meta.params, tableObject);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				return JdbcAction.getResultByType(rs, 1, generatedKeyType);
			}
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T, R> R insertIgnore(Connection conn, Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = insertSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		}
		String psql = SqlMetaKit.modifyPsqlForInsertIgnore(meta);
		if (showSql) {
			logger.info("Sql for insertIgnore: " + meta.toString(psql));
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(psql, Statement.RETURN_GENERATED_KEYS);
			action.setParam(pstmt, meta.params, tableObject);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				return JdbcAction.getResultByType(rs, 1, generatedKeyType);
			}
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int update(Connection conn, Class<?> tableType, T tableObject) throws SQLException {

		SqlMeta meta = updateSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for update: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			action.setParam(pstmt, meta.params, tableObject);
			return pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int replace(Connection conn, Class<?> tableType, T tableObject) throws SQLException {

		SqlMeta meta = replaceSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for replace: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			action.setParam(pstmt, meta.params, tableObject);
			return pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T, R> R replace(Connection conn, Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = replaceSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for replace: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql, Statement.RETURN_GENERATED_KEYS);
			action.setParam(pstmt, meta.params, tableObject);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				return JdbcAction.getResultByType(rs, 1, generatedKeyType);
			}
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int merge(Connection conn, Class<?> tableType, T tableObject) throws SQLException {

		SqlMeta meta = mergeSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for merge: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			action.setParam(pstmt, meta.params, tableObject);
			return pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T, R> R merge(Connection conn, Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = mergeSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for merge: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql, Statement.RETURN_GENERATED_KEYS);
			action.setParam(pstmt, meta.params, tableObject);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				return JdbcAction.getResultByType(rs, 1, generatedKeyType);
			}
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int delete(Connection conn, Class<?> tableType, T tableObject) throws SQLException {

		SqlMeta meta = deleteSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for delete: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			action.setParam(pstmt, meta.params, tableObject);
			return pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int deleteByKey(Connection conn, Class<?> tableType, Object... keys) throws SQLException {

		SqlMeta meta = deleteSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for delete: " + meta);
		}

		JdbcAction setAction = JdbcAction.ARRAY_JDBC_ACTION;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			setAction.setParam(pstmt, meta.params, keys);
			return pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T select(Connection conn, Class<?> tableType, T tableObject) throws SQLException {

		SqlMeta meta = selectSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for select: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			action.setParam(pstmt, meta.params, tableObject);
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			if (rs.next()) {
				return (T) action.getResult(rs, meta.labels);
			}

			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}

	}

	protected <T> T select2(Connection conn, Class<?> tableType, T tableObject) throws SQLException {

		SqlMeta meta = selectSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for select2: " + meta);
		}

		JdbcAction action = getJdbcAction(tableObject.getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			action.setParam(pstmt, meta.params, tableObject);
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			if (rs.next()) {
				action.getResult2(rs, meta.labels, tableObject);
				return tableObject;
			}
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T selectByKey(Connection conn, Class<T> tableType, Object... keys) throws SQLException {

		SqlMeta meta = selectSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for selectByKey: " + meta);
		}

		JdbcAction setAction = JdbcAction.ARRAY_JDBC_ACTION;
		JdbcAction getAction = getJdbcAction(tableType);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			setAction.setParam(pstmt, meta.params, keys);
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			if (rs.next()) {
				return (T) getAction.getResult(rs, meta.labels);
			}

			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}

	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> selectAll(Connection conn, Class<T> tableType) throws SQLException {

		SqlMeta meta = selectAllSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for selectAll: " + meta);
		}

		JdbcAction action = getJdbcAction(tableType);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			List<T> result = new LinkedList<T>();
			while (rs.next()) {
				result.add((T) action.getResult(rs, meta.labels));
			}
			return result;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> selectRange(Connection conn, Class<T> tableType, int start, int max) throws SQLException {

		SqlMeta meta = selectAllSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		}

		String psql = SqlMetaKit.modifyPsqlForLimit(meta, start, max);
		if (showSql) {
			logger.info("Sql for selectRange: " + meta.toString(psql));
		}

		JdbcAction action = getJdbcAction(tableType);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(psql);
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			List<T> result = new LinkedList<T>();
			while (rs.next()) {
				result.add((T) action.getResult(rs, meta.labels));
			}

			return result;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T selectFirst(Connection conn, Class<T> tableType) throws SQLException {
		SqlMeta meta = selectAllSqlMetaCache.get(tableType);

		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		}

		String psql = SqlMetaKit.modifyPsqlForLimit(meta, 0, 1);
		if (showSql) {
			logger.info("Sql for selectFirst: " + meta.toString(psql));
		}

		JdbcAction action = getJdbcAction(tableType);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(psql);
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			if (rs.next()) {
				return (T) action.getResult(rs, meta.labels);
			}
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> void selectPage(Connection conn, Class<T> tableType, Page<T> page) throws SQLException {

		int offset = page.start;
		if (offset < 0) {
			offset = 0;
		}
		int count = page.limit;
		if (count <= 0) {
			count = Integer.MAX_VALUE;
		}

		SqlMeta meta = selectAllSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		}

		String psql = SqlMetaKit.modifyPsqlForPageLimit(meta, offset, count, page.field, DIRECTION_DESC.equals(page.direction));
		if (showSql) {
			logger.info("Sql for selectPage: " + meta.toString(psql));
		}

		JdbcAction action = getJdbcAction(tableType);
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(psql);
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			List<T> result = new LinkedList<T>();
			while (rs.next()) {
				result.add((T) action.getResult(rs, meta.labels));
			}
			page.setRows(result);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}

		if ((offset == 0 && count == Integer.MAX_VALUE) || (page.getRows().size() < count)) {
			page.setResults(page.getRows().size() + offset);
		} else {
			try {
				pstmt = conn.prepareStatement(SqlMetaKit.modifyPsqlForPageTotal(meta));
				rs = pstmt.executeQuery();
				int total = 0;
				if (rs.next()) {
					total = rs.getInt(1);
				}
				page.setResults(total);
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			}
		}
	}

	protected <T> int[] batchInsert(Connection conn, Class<?> tableType, T[] tableObjects) throws SQLException {

		SqlMeta meta = insertSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for batchInsert: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchInsert");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			return pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int[] batchInsertIgnore(Connection conn, Class<?> tableType, T[] tableObjects) throws SQLException {

		SqlMeta meta = insertSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		}

		String psql = SqlMetaKit.modifyPsqlForInsertIgnore(meta);
		if (showSql) {
			logger.info("Sql for batchInsertIgnore: " + psql);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchInsertIgnore");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(psql);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			return pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T, R> R[] batchInsert(Connection conn, Class<?> tableType, T[] tableObjects, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = insertSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for batchInsert: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchInsert");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql, Statement.RETURN_GENERATED_KEYS);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			int[] rows = pstmt.executeBatch();

			@SuppressWarnings("unchecked")
			R[] result = (R[]) Array.newInstance(generatedKeyType, rows.length);
			rs = pstmt.getGeneratedKeys();
			for (int i = 0; i < result.length; i++) {
				if (rows[i] > 0 && rs.next()) {
					result[i] = JdbcAction.getResultByType(rs, 1, generatedKeyType);
				}
			}
			return result;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T, R> R[] batchInsertIgnore(Connection conn, Class<?> tableType, T[] tableObjects, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = insertSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		}

		String psql = SqlMetaKit.modifyPsqlForInsertIgnore(meta);
		if (showSql) {
			logger.info("Sql for batchInsertIgnore: " + meta.toString(psql));
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchInsertIgnore");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(psql, Statement.RETURN_GENERATED_KEYS);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			int[] rows = pstmt.executeBatch();

			@SuppressWarnings("unchecked")
			R[] result = (R[]) Array.newInstance(generatedKeyType, rows.length);
			rs = pstmt.getGeneratedKeys();
			for (int i = 0; i < result.length; i++) {
				if (rows[i] > 0 && rs.next()) {
					result[i] = JdbcAction.getResultByType(rs, 1, generatedKeyType);
				}
			}
			return result;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int[] batchUpdate(Connection conn, Class<?> tableType, T[] tableObjects) throws SQLException {

		SqlMeta meta = updateSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for batchUpdate: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchUpdate");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			return pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int[] batchReplace(Connection conn, Class<?> tableType, T[] tableObjects) throws SQLException {

		SqlMeta meta = replaceSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for batchReplace: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchReplace");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			return pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T, R> R[] batchReplace(Connection conn, Class<?> tableType, T[] tableObjects, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = replaceSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for batchReplace: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchReplace");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql, Statement.RETURN_GENERATED_KEYS);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			int[] rows = pstmt.executeBatch();

			@SuppressWarnings("unchecked")
			R[] result = (R[]) Array.newInstance(generatedKeyType, rows.length);
			rs = pstmt.getGeneratedKeys();
			for (int i = 0; i < result.length; i++) {
				if (rows[i] > 0 && rs.next()) {
					result[i] = JdbcAction.getResultByType(rs, 1, generatedKeyType);
				}
			}
			return result;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int[] batchMerge(Connection conn, Class<?> tableType, T[] tableObjects) throws SQLException {

		SqlMeta meta = mergeSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for batchMerge: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchMerge");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			return pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T, R> R[] batchMerge(Connection conn, Class<?> tableType, T[] tableObjects, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = mergeSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for batchMerge: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchMerge");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql, Statement.RETURN_GENERATED_KEYS);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			int[] rows = pstmt.executeBatch();
			@SuppressWarnings("unchecked")
			R[] result = (R[]) Array.newInstance(generatedKeyType, rows.length);
			rs = pstmt.getGeneratedKeys();
			for (int i = 0; i < result.length; i++) {
				if (rows[i] > 0 && rs.next()) {
					result[i] = JdbcAction.getResultByType(rs, 1, generatedKeyType);
				}
			}
			return result;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int[] batchDelete(Connection conn, Class<?> tableType, T[] tableObjects) throws SQLException {

		SqlMeta meta = deleteSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for batchDelete: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchDelete");
		}

		JdbcAction action = getJdbcAction(tableObjects[0].getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			for (T tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			return pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <T> int[] batchDeleteByKey(Connection conn, Class<T> tableType, Object[][] tableObjects) throws SQLException {

		SqlMeta meta = deleteSqlMetaCache.get(tableType);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table class: " + tableType);
		} else if (showSql) {
			logger.info("Sql for batchDeleteByKey: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchDeleteByKey");
		}

		JdbcAction action = JdbcAction.ARRAY_JDBC_ACTION;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			for (Object[] tableObject : tableObjects) {
				action.setParam(pstmt, meta.params, tableObject);
				pstmt.addBatch();
			}
			return pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	/*
	 * Sql config operations
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> query(Connection conn, String queryId, Class<T> elemType, Object params) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		} else if (showSql) {
			logger.info("Sql for query: " + meta);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			if (params != null) {
				JdbcAction setAction = getJdbcAction(params.getClass());
				setAction.setParam(pstmt, meta.params, params);
			}
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			List<T> list = new LinkedList<T>();
			JdbcAction getAction = getJdbcAction(elemType);
			while (rs.next()) {
				list.add((T) getAction.getResult(rs, meta.labels));
			}
			return list;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> queryRange(Connection conn, String queryId, Class<T> elemType, int start, int max, Object params) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		}

		String psql = SqlMetaKit.modifyPsqlForLimit(meta, start, max);
		if (showSql) {
			logger.info("Sql for queryRange: " + meta.toString(psql));
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(psql);
			if (params != null) {
				JdbcAction setAction = getJdbcAction(params.getClass());
				setAction.setParam(pstmt, meta.params, params);
			}
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			List<T> list = new LinkedList<T>();
			JdbcAction getAction = getJdbcAction(elemType);
			while (rs.next()) {
				list.add((T) getAction.getResult(rs, meta.labels));
			}
			return list;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T queryFirst(Connection conn, String queryId, Class<T> elemType, Object params) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		} else if (showSql) {
			logger.info("Sql for queryFirst: " + meta);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			if (params != null) {
				JdbcAction setAction = getJdbcAction(params.getClass());
				setAction.setParam(pstmt, meta.params, params);
			}
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			JdbcAction getAction = getJdbcAction(elemType);
			if (rs.next()) {
				return (T) getAction.getResult(rs, meta.labels);
			}
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> void queryPage(Connection conn, String queryId, Class<T> elemType, Page<T> page, Object params) throws SQLException {

		int offset = page.start;
		if (offset < 0) {
			offset = 0;
		}
		int count = page.limit;
		if (count <= 0) {
			count = Integer.MAX_VALUE;
		}

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		}

		String psql = SqlMetaKit.modifyPsqlForPageLimit(meta, offset, count, page.field, DIRECTION_DESC.equals(page.direction));
		if (showSql) {
			logger.info("Sql for queryPage: " + meta.toString(psql));
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JdbcAction setAction = null;

		try {
			pstmt = conn.prepareStatement(psql);
			if (params != null) {
				if (setAction == null) {
					setAction = getJdbcAction(params.getClass());
				}
				setAction.setParam(pstmt, meta.params, params);
			}
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			JdbcAction getAction = getJdbcAction(elemType);
			List<T> data = new LinkedList<T>();
			while (rs.next()) {
				data.add((T) getAction.getResult(rs, meta.labels));
			}
			page.setRows(data);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}

		if ((offset == 0 && count == Integer.MAX_VALUE) || (page.getRows().size() < count)) {
			page.setResults(page.getRows().size() + offset);
		} else {
			try {
				pstmt = conn.prepareStatement(SqlMetaKit.modifyPsqlForPageTotal(meta));
				if (params != null) {
					if (setAction == null) {
						setAction = getJdbcAction(params.getClass());
					}
					setAction.setParam(pstmt, meta.params, params);
				}
				rs = pstmt.executeQuery();
				if (rs.next()) {
					page.setResults(rs.getInt(1));
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			}
		}
	}

	protected <T> int execute(Connection conn, String updateId, Object params) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(updateId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + updateId);
		} else if (showSql) {
			logger.info("Sql for execute: " + meta);
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			if (params != null) {
				JdbcAction setAction = getJdbcAction(params.getClass());
				setAction.setParam(pstmt, meta.params, params);
			}
			return pstmt.executeUpdate();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <R> R execute(Connection conn, String updateId, Object params, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(updateId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + updateId);
		} else if (showSql) {
			logger.info("Sql for execute: " + meta);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql, Statement.RETURN_GENERATED_KEYS);
			if (params != null) {
				JdbcAction setAction = getJdbcAction(params.getClass());
				setAction.setParam(pstmt, meta.params, params);
			}
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				return JdbcAction.getResultByType(rs, 1, generatedKeyType);
			}
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected int[] batchExecute(Connection conn, String updateId, Object[] params) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(updateId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + updateId);
		} else if (showSql) {
			logger.info("Sql for batchExecute: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchExecute");
		}

		JdbcAction setAction = getJdbcAction(params[0].getClass());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			for (Object param : params) {
				setAction.setParam(pstmt, meta.params, param);
				pstmt.addBatch();
			}
			return pstmt.executeBatch();
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	protected <R> R[] batchExecute(Connection conn, String updateId, Object[] params, Class<R> generatedKeyType) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(updateId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + updateId);
		} else if (showSql) {
			logger.info("Sql for batchExecute: " + meta);
		}

		if (conn.getAutoCommit()) {
			logger.warn("Not setAutoCommit(false) before executing batchExecute");
		}

		JdbcAction setAction = getJdbcAction(params[0].getClass());
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql, Statement.RETURN_GENERATED_KEYS);
			for (Object param : params) {
				setAction.setParam(pstmt, meta.params, param);
				pstmt.addBatch();
			}
			int[] rows = pstmt.executeBatch();
			@SuppressWarnings("unchecked")
			R[] result = (R[]) Array.newInstance(generatedKeyType, rows.length);
			rs = pstmt.getGeneratedKeys();
			for (int i = 0; i < result.length; i++) {
				if (rows[i] > 0 && rs.next()) {
					result[i] = JdbcAction.getResultByType(rs, 1, generatedKeyType);
				}
			}
			return result;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	/*
	 * Initialization for mysqlclient
	 */
	protected void init(Connection conn) throws Exception {

		final Pattern separator = Pattern.compile("\\s*,\\s*");
		final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		Map<String, ClassMetaInfo> classMetaInfoMap = new HashMap<String, ClassMetaInfo>(); // key is classname
		Map<String, ClassMetaInfo> tableMetaInfoMap = new HashMap<String, ClassMetaInfo>(); // key is tablename
		Map<String, String> sqlMap = new HashMap<String, String>();

		ClassMetaInfo classMetaInfo, tableMetaInfo;
		StringBuilder sb = new StringBuilder(128);
		String key;

		if (isNotEmpty(packagesToScan)) {
			String[] pkgs = separator.split(packagesToScan);
			for (String pkg : pkgs) {
				if (isNotEmpty(pkg)) {
					sb.setLength(0);
					String packageSearchPath = sb.append(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX).append(ClassKit.getInternalNameFromClassName(pkg)).append("/**/*.class").toString();
					Resource[] rss = resolver.getResources(packageSearchPath);
					for (Resource rs : rss) {
						classMetaInfo = AsmKit.getAnnotationClassMetaInfo(rs);

						if (classMetaInfo.tableAnnotation != null || classMetaInfo.metaAnnotation != null) {
							classMetaInfoMap.put(ClassKit.getClassNameFromInternalName(classMetaInfo.internalName), classMetaInfo);
							if (classMetaInfo.tableAnnotation != null) {
								if (logger.isInfoEnabled()) {
									logger.info(String.format("Load @Table: %s %s", classMetaInfo.tableName, classMetaInfo.columns));
								}
								if ((tableMetaInfo = tableMetaInfoMap.put(classMetaInfo.tableName, classMetaInfo)) != null) {
									throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_DUBLICATE_TABLE, "Duplicate @Table: " + classMetaInfo.tableName + ", please check class:"
											+ classMetaInfo.internalName + "," + tableMetaInfo.internalName);
								}
							}
						}
					}
				}
			}
		}
		if (isNotEmpty(configLocations)) {
			String[] locations = separator.split(configLocations);
			for (String location : locations) {
				if (isNotEmpty(location)) {
					Resource[] rss = resolver.getResources(location);
					for (Resource rs : rss) {
						ConfigMetaInfo configMetaInfo = ConfigSAXParser.parse(rs);
						for (String className : configMetaInfo.tables) {

							if (!classMetaInfoMap.containsKey(className)) {
								classMetaInfo = AsmKit.getAnnotationClassMetaInfo(className);

								if (classMetaInfo.tableAnnotation != null || classMetaInfo.metaAnnotation != null) {
									classMetaInfoMap.put(className, classMetaInfo);
									if (classMetaInfo.tableAnnotation != null) {
										if (logger.isInfoEnabled()) {
											logger.info(String.format("Load @Table: %s %s", classMetaInfo.tableName, classMetaInfo.columns));
										}
										if ((tableMetaInfo = tableMetaInfoMap.put(classMetaInfo.tableName, classMetaInfo)) != null) {
											throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_DUBLICATE_TABLE, "Duplicate @Table: " + classMetaInfo.tableName
													+ ", please check class:" + classMetaInfo.internalName + "," + tableMetaInfo.internalName);
										}
									}
								}
							}
						}
						for (String meta : configMetaInfo.metas) {

							if (!classMetaInfoMap.containsKey(meta)) {
								classMetaInfo = AsmKit.getClassMetaInfo(meta);
								classMetaInfo.tableAnnotation = null; // FIXBUG:<meta>的类不是t<table>
								classMetaInfoMap.put(meta, classMetaInfo);
							}
						}
						for (Map.Entry<String, String> entry : configMetaInfo.sqls.entrySet()) {
							// key = namespace + . + id
							sb.setLength(0);
							if (isNotEmpty(configMetaInfo.namespace)) {
								// ignore if not setting namespace
								sb.append(configMetaInfo.namespace).append('.');
							}
							key = sb.append(entry.getKey()).toString();
							if (sqlMap.put(key, entry.getValue()) != null) {
								throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_DUPLICATE, "Duplicate sql id: " + key);
							}
						}
					}
				}
			}
		}

		Class<?> clazz;
		for (Map.Entry<String, ClassMetaInfo> entry : classMetaInfoMap.entrySet()) {

			clazz = ClassKit.forName(entry.getKey());
			classMetaInfo = entry.getValue();

			setJdbcAction(clazz, AsmKit.newJdbcAction(classMetaInfo));

			if (classMetaInfo.tableAnnotation != null) {
				selectAllSqlMetaCache.put(clazz, SqlMetaKit.genSelectAllSqlMeta(classMetaInfo));
				selectSqlMetaCache.put(clazz, SqlMetaKit.genSelectSqlMeta(classMetaInfo));
				insertSqlMetaCache.put(clazz, SqlMetaKit.genInsertSqlMeta(classMetaInfo));
				updateSqlMetaCache.put(clazz, SqlMetaKit.genUpdateSqlMeta(classMetaInfo));
				replaceSqlMetaCache.put(clazz, SqlMetaKit.genReplaceSqlMeta(classMetaInfo));
				mergeSqlMetaCache.put(clazz, SqlMetaKit.genMergeSqlMeta(classMetaInfo));
				deleteSqlMetaCache.put(clazz, SqlMetaKit.genDeleteSqlMeta(classMetaInfo));
			}

		}

		for (Map.Entry<String, String> entry : sqlMap.entrySet()) {
			configSqlMetaCache.put(entry.getKey(), SqlMetaKit.genConfigSqlMeta(entry.getKey(), entry.getValue()));
		}

		if (updateTable) {
			if (tableMetaInfoMap.size() > 0) {
				SqlDdlKit.processUpdateTable(conn, tableMetaInfoMap);
			}
		}

		if (checkConfig) {
			if (selectAllSqlMetaCache.size() > 0) {
				SqlDdlKit.processCheckConfig(conn, selectAllSqlMetaCache.values());
			}
			if (selectSqlMetaCache.size() > 0) {
				SqlDdlKit.processCheckConfig(conn, selectSqlMetaCache.values());
			}
			if (insertSqlMetaCache.size() > 0) {
				SqlDdlKit.processCheckConfig(conn, insertSqlMetaCache.values());
			}
			if (updateSqlMetaCache.size() > 0) {
				SqlDdlKit.processCheckConfig(conn, updateSqlMetaCache.values());
			}
			if (replaceSqlMetaCache.size() > 0) {
				SqlDdlKit.processCheckConfig(conn, replaceSqlMetaCache.values());
			}
			if (mergeSqlMetaCache.size() > 0) {
				SqlDdlKit.processCheckConfig(conn, mergeSqlMetaCache.values());
			}
			if (deleteSqlMetaCache.size() > 0) {
				SqlDdlKit.processCheckConfig(conn, deleteSqlMetaCache.values());
			}
			if (configSqlMetaCache.size() > 0) {
				SqlDdlKit.processCheckConfig(conn, configSqlMetaCache.values());
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Mysqlclient initialization successful, loading %d Tables, %d Metas, and %d SQLs", tableMetaInfoMap.size(), classMetaInfoMap.size(), sqlMap.size()));
		}
	}

	/*
	 * SqlMeta global cache
	 */
	final Map<Class, SqlMeta> selectAllSqlMetaCache = new HashMap<Class, SqlMeta>();
	final Map<Class, SqlMeta> selectSqlMetaCache = new HashMap<Class, SqlMeta>();
	final Map<Class, SqlMeta> insertSqlMetaCache = new HashMap<Class, SqlMeta>();
	final Map<Class, SqlMeta> updateSqlMetaCache = new HashMap<Class, SqlMeta>();
	final Map<Class, SqlMeta> replaceSqlMetaCache = new HashMap<Class, SqlMeta>();
	final Map<Class, SqlMeta> mergeSqlMetaCache = new HashMap<Class, SqlMeta>();
	final Map<Class, SqlMeta> deleteSqlMetaCache = new HashMap<Class, SqlMeta>();
	final Map<String, SqlMeta> configSqlMetaCache = new HashMap<String, SqlMeta>();

	/*
	 * SqlAction global cache, and temporary instance allowed to release
	 */
	// There are 2 jdbc actions: persist and temporary
	final Map<Class, JdbcAction> persistJdbcActionCache = new HashMap<Class, JdbcAction>();
	final Map<Class, SoftReference<JdbcAction>> tempoJdbcActionCache = new HashMap<Class, SoftReference<JdbcAction>>();

	{ // scalar jdbcAction
		persistJdbcActionCache.put(boolean.class, JdbcAction.BooleanJdbcAction);
		persistJdbcActionCache.put(Boolean.class, JdbcAction.BooleanJdbcAction);
		persistJdbcActionCache.put(char.class, JdbcAction.CharacterJdbcAction);
		persistJdbcActionCache.put(Character.class, JdbcAction.CharacterJdbcAction);
		persistJdbcActionCache.put(byte.class, JdbcAction.ByteJdbcAction);
		persistJdbcActionCache.put(Byte.class, JdbcAction.ByteJdbcAction);
		persistJdbcActionCache.put(short.class, JdbcAction.ShortJdbcAction);
		persistJdbcActionCache.put(Short.class, JdbcAction.ShortJdbcAction);
		persistJdbcActionCache.put(int.class, JdbcAction.IntegerJdbcAction);
		persistJdbcActionCache.put(Integer.class, JdbcAction.IntegerJdbcAction);
		persistJdbcActionCache.put(long.class, JdbcAction.LongJdbcAction);
		persistJdbcActionCache.put(Long.class, JdbcAction.LongJdbcAction);
		persistJdbcActionCache.put(float.class, JdbcAction.FloatJdbcAction);
		persistJdbcActionCache.put(Float.class, JdbcAction.FloatJdbcAction);
		persistJdbcActionCache.put(double.class, JdbcAction.DoubleJdbcAction);
		persistJdbcActionCache.put(String.class, JdbcAction.StringJdbcAction);
		persistJdbcActionCache.put(Double.class, JdbcAction.DoubleJdbcAction);
		persistJdbcActionCache.put(BigDecimal.class, JdbcAction.BigDecimalJdbcAction);
		persistJdbcActionCache.put(BigInteger.class, JdbcAction.BigIntegerJdbcAction);
		persistJdbcActionCache.put(java.util.Date.class, JdbcAction.JavaUtilDateJdbcAction);
		persistJdbcActionCache.put(Date.class, JdbcAction.DateJdbcAction);
		persistJdbcActionCache.put(Time.class, JdbcAction.TimeJdbcAction);
		persistJdbcActionCache.put(Timestamp.class, JdbcAction.TimestampJdbcAction);
		persistJdbcActionCache.put(Ref.class, JdbcAction.RefJdbcAction);
		persistJdbcActionCache.put(URL.class, JdbcAction.URLJdbcAction);
		persistJdbcActionCache.put(SQLXML.class, JdbcAction.SQLXMLJdbcAction);
		persistJdbcActionCache.put(Blob.class, JdbcAction.BlobJdbcAction);
		persistJdbcActionCache.put(Clob.class, JdbcAction.ClobJdbcAction);
		persistJdbcActionCache.put(InputStream.class, JdbcAction.InputStreamJdbcAction);
		persistJdbcActionCache.put(Reader.class, JdbcAction.ReaderJdbcAction);
		persistJdbcActionCache.put(byte[].class, JdbcAction.BytesJdbcAction);
	}

	public final void setJdbcAction(Class type, JdbcAction jdbcAction) {
		if (!persistJdbcActionCache.containsKey(type)) {
			synchronized (persistJdbcActionCache) {
				if (!persistJdbcActionCache.containsKey(type)) {
					persistJdbcActionCache.put(type, jdbcAction);
				}
			}
		}
	}

	protected final JdbcAction getJdbcAction(Class type) {

		if (type == null) {
			return JdbcAction.ARRAY_JDBC_ACTION;
		} else if (Map.class.isAssignableFrom(type)) {
			return JdbcAction.MAP_JDBC_ACTION;
		} else if (List.class.isAssignableFrom(type)) {
			return JdbcAction.LIST_JDBC_ACTION;
		}
		JdbcAction action = persistJdbcActionCache.get(type);
		if (action == null) {
			SoftReference<JdbcAction> ref = tempoJdbcActionCache.get(type);
			if (ref == null || (action = ref.get()) == null) {
				if (type.isArray() || type.isEnum() || type.isInterface() || type.isAnnotation()) {
					throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.JDBC_ACTION_NOT_SUPPORTED, "JdbcAction don't support array, enum, interface, or annoation type:" + type.getCanonicalName());
				}
				synchronized (tempoJdbcActionCache) {
					ref = tempoJdbcActionCache.get(type);
					if (ref == null || (action = ref.get()) == null) {
						try {
							action = AsmKit.newJdbcAction(type.getCanonicalName());
							ref = new SoftReference<JdbcAction>(action);
							tempoJdbcActionCache.put(type, ref);
						} catch (Exception e) {
							throw new WrappedException(e);
						}
					}
				}
			}
		}
		return action;
	}

	/*
	 * special config query to process collection parameters
	 */

	private static String newParamName(Object name, int idx) {
		return new StringBuilder(64).append(name).append("_$").append(idx).toString();
	}

	/** scan to recognize collection params and convert it. return collection params */
	private static Map<String, Object> extcCollectValues(Map<String, Object> values, Map<String, Integer> collects) {

		Map<String, Object> valuesNew = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();
			if (val instanceof Collection) {
				Collection<?> c = (Collection<?>) val;
				int size = c.size();
				if (size == 1) {
					valuesNew.put(key, c.iterator().next());
				} else if (size == 0) {
					valuesNew.put(key, null);
				} else {
					int idx = 0;
					for (Iterator<?> iter = c.iterator(); iter.hasNext(); idx++) {
						String nameNew = newParamName(iter.next(), idx);
						valuesNew.put(nameNew, iter.next());
					}
					collects.put(key, size);
				}
			} else {
				valuesNew.put(key, val);
			}
		}
		return valuesNew;
	}

	/** Position params, start from 1 */
	private static String[] origSqlMetaParams(SqlMeta meta) {
		if (MapKit.isEmpty(meta.params)) {
			return null;
		}
		int size = 1;
		for (int[] arr : meta.params.values()) {
			size += arr.length;
		}
		String[] params = new String[size];
		for (Map.Entry<String, int[]> entry : meta.params.entrySet()) {
			String key = entry.getKey();
			for (int pos : entry.getValue()) {
				params[pos] = key;
			}
		}
		return params;
	}

	/** syntax:((...?..)) */
	private static void extcCollectPsql(StringBuilder sqlb, int size, int idx, int lbound, int rbound) throws SQLException {
		int pidx = sqlb.lastIndexOf("((", idx);
		if (pidx < 0 || pidx <= lbound) {
			throw new SQLException("Syntax error for param near index " + idx + ": " + sqlb);
		}
		int sidx = sqlb.indexOf("))", idx);
		if (sidx < 0 || sidx >= rbound) {
			throw new SQLException("Syntax error for param near index " + idx + ": " + sqlb);
		}
		pidx += 1;
		sidx += 1;
		String str = " OR " + sqlb.substring(pidx, sidx);
		for (int i = 1; i < size; i++) {
			sqlb.insert(sidx, str);
		}
	}

	private static int[] toIntArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		int idx = 0;
		for (Integer p : list) {
			ret[idx++] = p.intValue();
		}
		return ret;
	}

	private static Map<String, int[]> extcCollectParams(List<String> params) {

		Map<String, List<Integer>> temp = new HashMap<String, List<Integer>>();
		int pos = 0;
		for (String param : params) {
			List<Integer> poses = temp.get(param);
			if (poses == null) {
				poses = new LinkedList<Integer>();
			}
			poses.add(++pos);
		}

		Map<String, int[]> extcParams = new HashMap<String, int[]>(temp.size());
		for (Map.Entry<String, List<Integer>> entry : temp.entrySet()) {
			extcParams.put(entry.getKey(), toIntArray(entry.getValue()));
		}
		return extcParams;
	}

	private static SqlMeta extcCollectSqlMeta(SqlMeta meta, Map<String, Integer> collects, boolean limitIndexed) throws SQLException {

		String[] origParams = origSqlMetaParams(meta); // position start from 1

		LinkedList<String> tempParams = new LinkedList<String>();
		StringBuilder tempPsql = new StringBuilder(meta.psql);
		String param;
		Integer size;
		for (int limit = meta.psql.length(), last = origParams.length - 1, pos = last; pos > 0; pos--) {
			param = origParams[pos];
			size = collects.get(param);
			if (size != null) {
				for (int idx = size - 1; idx >= 0; idx--) {
					tempParams.addFirst(newParamName(param, idx));
				}
				extcCollectPsql(tempPsql, size, meta.psqlParamIndex[pos], pos > 1 ? meta.psqlParamIndex[pos - 1] : -1, pos < last ? meta.psqlParamIndex[pos + 1] : limit);
			} else {
				tempParams.addFirst(param);
			}
		}
		// optional: limitIndex
		return new SqlMeta(tempPsql.toString(), extcCollectParams(tempParams), limitIndexed ? SqlMetaKit.parseLimitIndexIfExist(tempPsql) : -1);
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> queryExtc(Connection conn, String queryId, Class<T> elemType, Map<String, Object> values) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		} else if (showSql) {
			logger.info("Sql for query: " + meta);
		}

		Map<String, Integer> collects = new HashMap<String, Integer>();
		Map<String, Object> extcValues = extcCollectValues(values, collects);
		if (MapKit.isNotEmpty(collects)) {
			meta = extcCollectSqlMeta(meta, collects, false);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			if (extcValues != null) {
				JdbcAction setAction = JdbcAction.MAP_JDBC_ACTION;
				setAction.setParam(pstmt, meta.params, extcValues);
			}
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			List<T> list = new LinkedList<T>();
			JdbcAction getAction = getJdbcAction(elemType);
			while (rs.next()) {
				list.add((T) getAction.getResult(rs, meta.labels));
			}
			return list;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> List<T> queryRangeExtc(Connection conn, String queryId, Class<T> elemType, int start, int max, Map<String, Object> values) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		}

		String psql = SqlMetaKit.modifyPsqlForLimit(meta, start, max);
		if (showSql) {
			logger.info("Sql for queryRange: " + meta.toString(psql));
		}

		Map<String, Integer> collects = new HashMap<String, Integer>();
		Map<String, Object> extcValues = extcCollectValues(values, collects);
		if (MapKit.isNotEmpty(collects)) {
			meta = extcCollectSqlMeta(meta, collects, true);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(psql);
			if (extcValues != null) {
				JdbcAction setAction = JdbcAction.MAP_JDBC_ACTION;
				setAction.setParam(pstmt, meta.params, extcValues);
			}
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			List<T> list = new LinkedList<T>();
			JdbcAction getAction = getJdbcAction(elemType);
			while (rs.next()) {
				list.add((T) getAction.getResult(rs, meta.labels));
			}
			return list;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T queryFirstExtc(Connection conn, String queryId, Class<T> elemType, Map<String, Object> values) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		} else if (showSql) {
			logger.info("Sql for queryFirst: " + meta);
		}

		Map<String, Integer> collects = new HashMap<String, Integer>();
		Map<String, Object> extcValues = extcCollectValues(values, collects);
		if (MapKit.isNotEmpty(collects)) {
			meta = extcCollectSqlMeta(meta, collects, true);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(meta.psql);
			if (extcValues != null) {
				JdbcAction setAction = JdbcAction.MAP_JDBC_ACTION;
				setAction.setParam(pstmt, meta.params, extcValues);
			}
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			JdbcAction getAction = getJdbcAction(elemType);
			if (rs.next()) {
				return (T) getAction.getResult(rs, meta.labels);
			}
			return null;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> void queryPageExtc(Connection conn, String queryId, Class<T> elemType, Page<T> page, Map<String, Object> values) throws SQLException {

		int offset = page.start;
		if (offset < 0) {
			offset = 0;
		}
		int count = page.limit;
		if (count <= 0) {
			count = Integer.MAX_VALUE;
		}

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		}

		String psql = SqlMetaKit.modifyPsqlForPageLimit(meta, offset, count, page.field, DIRECTION_DESC.equals(page.direction));
		if (showSql) {
			logger.info("Sql for queryPage: " + meta.toString(psql));
		}

		Map<String, Integer> collects = new HashMap<String, Integer>();
		Map<String, Object> extcValues = extcCollectValues(values, collects);
		if (MapKit.isNotEmpty(collects)) {
			meta = extcCollectSqlMeta(meta, collects, true);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		JdbcAction setAction = JdbcAction.MAP_JDBC_ACTION;

		try {
			pstmt = conn.prepareStatement(psql);
			if (extcValues != null) {
				setAction.setParam(pstmt, meta.params, extcValues);
			}
			rs = pstmt.executeQuery();
			if (meta.labels == null) {
				SqlMetaKit.fillSqlMetaLables(rs, meta);
			}
			JdbcAction getAction = getJdbcAction(elemType);
			List<T> data = new LinkedList<T>();
			while (rs.next()) {
				data.add((T) getAction.getResult(rs, meta.labels));
			}
			page.setRows(data);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		}

		if ((offset == 0 && count == Integer.MAX_VALUE) || (page.getRows().size() < count)) {
			page.setResults(page.getRows().size() + offset);
		} else {
			try {
				pstmt = conn.prepareStatement(SqlMetaKit.modifyPsqlForPageTotal(meta));
				if (extcValues != null) {
					setAction.setParam(pstmt, meta.params, extcValues);
				}
				rs = pstmt.executeQuery();
				if (rs.next()) {
					page.setResults(rs.getInt(1));
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			}
		}
	}
}

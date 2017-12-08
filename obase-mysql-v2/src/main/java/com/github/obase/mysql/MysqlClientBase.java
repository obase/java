package com.github.obase.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.obase.MessageException;
import com.github.obase.Page;
import com.github.obase.kit.CollectKit;
import com.github.obase.mysql.core.DNode;
import com.github.obase.mysql.core.JdbcMeta;
import com.github.obase.mysql.core.PstmtMeta;
import com.github.obase.mysql.stmt.Param;
import com.github.obase.mysql.stmt.Statement;
import com.github.obase.mysql.syntax.SqlDqlKit;
import com.github.obase.spring.transaction.DataSourceUtils;

/**
 * MysqlClient实现类, 处理缓存与初始化相关工作
 */
@SuppressWarnings("unchecked")
public abstract class MysqlClientBase implements MysqlClient {

	static final Log logger = LogFactory.getLog(MysqlClientBase.class);

	// =============================================
	// 核心属性
	// =============================================
	protected DataSource dataSource;
	protected boolean showSql; // show sql or not

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	// =============================================
	// 辅助方法
	// =============================================
	private void releaseStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				logger.error("Close preapred statement failed", e);
			}
		}
	}

	@Override
	public final void init() throws Exception {
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			doInit(conn);
		} catch (Exception ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	// 由子类初始化
	protected abstract void doInit(Connection conn) throws Exception;

	// =============================================
	// 基础方法
	// =============================================

	@Override
	public <T> T queryFirst(PstmtMeta pstmt, Class<T> type, Object param) throws SQLException {

		if (showSql) {
			logger.info("[SQL] " + pstmt);
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			ps = conn.prepareStatement(pstmt.psql);
			if (param != null) {
				JdbcMeta setjm = JdbcMeta.get(param.getClass());
				int pos = 0;
				Param p;
				for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
					p = t.value;
					++pos;
					if (p.setted) {
						JdbcMeta.setParamByType(ps, pos, p.value);
					} else {
						setjm.setParam(ps, pos, param, p.name);
					}
				}
			}
			rs = ps.executeQuery();

			// 设置查询结果标签
			if (pstmt.label == null) {
				SqlDqlKit.parsePstmtLabel(rs, pstmt);
			}

			JdbcMeta getjm = JdbcMeta.get(type);
			if (rs.next()) {
				return (T) getjm.getResult(rs, pstmt.label);
			}

			return null;
		} catch (

		SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public boolean queryFirst2(PstmtMeta pstmt, Object param) throws SQLException {

		if (showSql) {
			logger.info("[SQL] " + pstmt);
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			ps = conn.prepareStatement(pstmt.psql);
			JdbcMeta jm = JdbcMeta.get(param.getClass());
			int pos = 0;
			Param p;
			for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
				p = t.value;
				++pos;
				if (p.setted) {
					JdbcMeta.setParamByType(ps, pos, p.value);
				} else {
					jm.setParam(ps, pos, param, p.name);
				}
			}
			rs = ps.executeQuery();

			// 设置查询结果标签
			if (pstmt.label == null) {
				SqlDqlKit.parsePstmtLabel(rs, pstmt);
			}

			if (rs.next()) {
				jm.getResult2(rs, pstmt.label, param);
				return true;
			}
			return false;
		} catch (

		SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> List<T> queryList(PstmtMeta pstmt, Class<T> type, Object param) throws SQLException {

		if (showSql) {
			logger.info("[SQL] " + pstmt);
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			ps = conn.prepareStatement(pstmt.psql);
			if (param != null) {
				JdbcMeta setjm = JdbcMeta.get(param.getClass());
				int pos = 0;
				Param p;
				for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
					p = t.value;
					++pos;
					if (p.setted) {
						JdbcMeta.setParamByType(ps, pos, p.value);
					} else {
						setjm.setParam(ps, pos, param, p.name);
					}
				}
			}
			rs = ps.executeQuery();

			// 设置查询结果标签
			if (pstmt.label == null) {
				SqlDqlKit.parsePstmtLabel(rs, pstmt);
			}

			JdbcMeta getjm = JdbcMeta.get(type);
			List<T> list = new LinkedList<T>();
			while (rs.next()) {
				list.add((T) getjm.getResult(rs, pstmt.label));
			}
			return list;
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> List<T> queryRange(PstmtMeta pstmt, Class<T> type, int offset, int count, Object param) throws SQLException {

		if (showSql) {
			logger.info("[SQL] " + pstmt);
		}

		if (offset < 0) {
			offset = 0;
		}

		if (count <= 0) {
			count = Integer.MAX_VALUE;
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {

			// 解析关键词下标位置用于编辑SQL
			if (pstmt.limitPsql == null) {
				SqlDqlKit.parsePstmtLimit(pstmt);
			}

			ps = conn.prepareStatement(pstmt.limitPsql);
			int pos = 0;
			if (param != null) {
				JdbcMeta setjm = JdbcMeta.get(param.getClass());
				Param p;
				for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
					p = t.value;
					++pos;
					if (p.setted) {
						JdbcMeta.setParamByType(ps, pos, p.value);
					} else {
						setjm.setParam(ps, pos, param, p.name);
					}
				}
			}
			// 设置最好limit的参数
			ps.setInt(++pos, offset);
			ps.setInt(++pos, count);

			rs = ps.executeQuery();

			// 设置查询结果标签
			if (pstmt.label == null) {
				SqlDqlKit.parsePstmtLabel(rs, pstmt);
			}

			JdbcMeta getjm = JdbcMeta.get(type);
			List<T> list = new LinkedList<T>();
			while (rs.next()) {
				list.add((T) getjm.getResult(rs, pstmt.label));
			}
			return list;
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	public <T> void queryPage(PstmtMeta pstmt, Class<T> type, Page<T> page, Object param) throws SQLException {

		if (showSql) {
			logger.info("[SQL] " + pstmt);
		}

		int offset = page.start;
		if (offset < 0) {
			offset = 0;
		}
		int count = page.limit;
		if (count <= 0) {
			count = Integer.MAX_VALUE;
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {

			// 解析关键词下标位置用于编辑SQL
			String pagePsql = null;
			if (page.field == null) {
				if (pstmt.limitPsql == null) {
					SqlDqlKit.parsePstmtLimit(pstmt);
				}
				pagePsql = pstmt.limitPsql;
			} else {
				pagePsql = SqlDqlKit.parsePstmtOrderLimit(pstmt, page.field, page.direction);
			}

			ps = conn.prepareStatement(pagePsql);
			JdbcMeta setjm = null;
			int pos = 0;
			if (param != null) {
				setjm = JdbcMeta.get(param.getClass());
				Param p;
				for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
					p = t.value;
					++pos;
					if (p.setted) {
						JdbcMeta.setParamByType(ps, pos, p.value);
					} else {
						setjm.setParam(ps, pos, param, p.name);
					}
				}
			}
			// 设置最好limit的参数
			ps.setInt(++pos, offset);
			ps.setInt(++pos, count);

			rs = ps.executeQuery();

			// 设置查询结果标签
			if (pstmt.label == null) {
				SqlDqlKit.parsePstmtLabel(rs, pstmt);
			}

			JdbcMeta getjm = JdbcMeta.get(type);
			List<T> list = new LinkedList<T>();
			while (rs.next()) {
				list.add((T) getjm.getResult(rs, pstmt.label));
			}
			releaseStatement(ps); // 释放data的查询

			page.setRows(list);
			int size = list.size();
			if ((offset == 0 && count == Integer.MAX_VALUE) || (size > 0 && size < count)) {
				page.setResults(size + offset);
			} else {
				if (pstmt.countPsql == null) {
					SqlDqlKit.parsePstmtCount(pstmt);
				}
				ps = conn.prepareStatement(pstmt.countPsql);
				if (param != null) {
					pos = 0;
					Param p;
					for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
						p = t.value;
						++pos;
						if (p.setted) {
							JdbcMeta.setParamByType(ps, pos, p.value);
						} else {
							setjm.setParam(ps, pos, param, p.name);
						}
					}
				}
				rs = ps.executeQuery();
				if (rs.next()) {
					page.setResults(rs.getInt(1));
				}
			}

		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public int executeUpdate(PstmtMeta pstmt, Object param) throws SQLException {

		if (showSql) {
			logger.info("[SQL] " + pstmt);
		}

		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			ps = conn.prepareStatement(pstmt.psql);
			if (param != null) {
				JdbcMeta setjm = JdbcMeta.get(param.getClass());
				int pos = 0;
				Param p;
				for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
					p = t.value;
					++pos;
					if (p.setted) {
						JdbcMeta.setParamByType(ps, pos, p.value);
					} else {
						setjm.setParam(ps, pos, param, p.name);
					}
				}
			}
			return ps.executeUpdate();
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <R> R executeUpdate(PstmtMeta pstmt, Class<R> generateKeyType, Object param) throws SQLException {

		if (showSql) {
			logger.info("[SQL] " + pstmt);
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			ps = conn.prepareStatement(pstmt.psql);
			if (param != null) {
				JdbcMeta setjm = JdbcMeta.get(param.getClass());
				int pos = 0;
				Param p;
				for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
					p = t.value;
					++pos;
					if (p.setted) {
						JdbcMeta.setParamByType(ps, pos, p.value);
					} else {
						setjm.setParam(ps, pos, param, p.name);
					}
				}
			}
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return JdbcMeta.getResultByType(rs, 1, generateKeyType);
			}
			return null;
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> int[] executeBatch(PstmtMeta pstmt, List<T> params) throws SQLException {

		if (showSql) {
			logger.info("[SQL] " + pstmt);
		}

		// 如果参数为空直接返回null表示未执行
		if (CollectKit.isEmpty(params)) {
			return null;
		}

		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			ps = conn.prepareStatement(pstmt.psql);
			JdbcMeta setjm = JdbcMeta.get(params.get(0).getClass());
			for (T param : params) {
				int pos = 0;
				Param p;
				for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
					p = t.value;
					++pos;
					if (p.setted) {
						JdbcMeta.setParamByType(ps, pos, p.value);
					} else {
						setjm.setParam(ps, pos, param, p.name);
					}
				}
				ps.addBatch();
			}
			return ps.executeBatch();
		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T, R> List<R> executeBatch(PstmtMeta pstmt, Class<R> generateKeyType, List<T> params) throws SQLException {
		// 如果参数为空直接返回null表示未执行
		if (CollectKit.isEmpty(params)) {
			return null;
		}

		if (showSql) {
			logger.info("[SQL] " + pstmt);
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			ps = conn.prepareStatement(pstmt.psql);
			JdbcMeta setjm = JdbcMeta.get(params.get(0).getClass());
			for (T param : params) {
				int pos = 0;
				Param p;
				for (DNode<Param> t = pstmt.params.head; t != null; t = t.next) {
					p = t.value;
					++pos;
					if (p.setted) {
						JdbcMeta.setParamByType(ps, pos, p.value);
					} else {
						setjm.setParam(ps, pos, param, p.name);
					}
				}
				ps.addBatch();
			}
			ps.executeBatch();

			List<R> list = new ArrayList<R>(params.size());
			rs = ps.getGeneratedKeys();
			while (rs.next()) {
				list.add(JdbcMeta.getResultByType(rs, 1, generateKeyType));
			}
			return list;

		} catch (SQLException ex) {
			// Release Connection early, to avoid potential connection pool deadlock
			// in the case when the exception translator hasn't been initialized yet.
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
			conn = null;
			throw ex;
		} finally {
			releaseStatement(ps);
			DataSourceUtils.releaseConnection(conn, dataSource);
		}
	}

	@Override
	public <T> T queryFirst(Statement xstmt, Class<T> type, Object param) throws SQLException {
		PstmtMeta pstmt = xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(param == null ? null : JdbcMeta.get(param.getClass()), param);
		return queryFirst(pstmt, type, param);
	}

	@Override
	public boolean queryFirst2(Statement xstmt, Object param) throws SQLException {
		PstmtMeta pstmt = xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(param == null ? null : JdbcMeta.get(param.getClass()), param);
		return queryFirst2(pstmt, param);
	}

	@Override
	public <T> List<T> queryList(Statement xstmt, Class<T> type, Object param) throws SQLException {
		PstmtMeta pstmt = xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(param == null ? null : JdbcMeta.get(param.getClass()), param);
		return queryList(pstmt, type, param);
	}

	@Override
	public <T> List<T> queryRange(Statement xstmt, Class<T> type, int offset, int count, Object param) throws SQLException {
		PstmtMeta pstmt = xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(param == null ? null : JdbcMeta.get(param.getClass()), param);
		return queryRange(pstmt, type, offset, count, param);
	}

	@Override
	public <T> void queryPage(Statement xstmt, Class<T> type, Page<T> page, Object param) throws SQLException {
		PstmtMeta pstmt = xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(param == null ? null : JdbcMeta.get(param.getClass()), param);
		queryPage(pstmt, type, page, param);
	}

	@Override
	public int executeUpdate(Statement xstmt, Object param) throws SQLException {
		PstmtMeta pstmt = xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(param == null ? null : JdbcMeta.get(param.getClass()), param);
		return executeUpdate(pstmt, param);
	}

	@Override
	public <R> R executeUpdate(Statement xstmt, Class<R> generateKeyType, Object param) throws SQLException {
		PstmtMeta pstmt = xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(param == null ? null : JdbcMeta.get(param.getClass()), param);
		return executeUpdate(pstmt, generateKeyType, param);
	}

	@Override
	public <T> int[] executeBatch(Statement xstmt, List<T> params) throws SQLException {
		if (xstmt.staticPstmtMeta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_DYNAMIC_NOT_SUPPORT, "executeBatch don't support dynamic statement: " + xstmt.id);
		}
		return executeBatch(xstmt.staticPstmtMeta, params);
	}

	@Override
	public <T, R> List<R> executeBatch(Statement xstmt, Class<R> generateKeyType, List<T> params) throws SQLException {
		if (xstmt.staticPstmtMeta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_DYNAMIC_NOT_SUPPORT, "executeBatch don't support dynamic statement: " + xstmt.id);
		}
		return executeBatch(xstmt.staticPstmtMeta, generateKeyType, params);
	}

}

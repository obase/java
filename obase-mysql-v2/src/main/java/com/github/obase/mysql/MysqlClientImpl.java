package com.github.obase.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.obase.mysql.core.JdbcMeta;
import com.github.obase.mysql.core.PstmtMeta;
import com.github.obase.mysql.stmt.Param;
import com.github.obase.mysql.syntax.SqlDqlKit;
import com.github.obase.spring.transaction.DataSourceUtils;

/**
 * MysqlClient实现类, 处理缓存与初始化相关工作
 */
@SuppressWarnings("unchecked")
public class MysqlClientImpl implements MysqlClient {

	static final Log logger = LogFactory.getLog(MysqlClientImpl.class);

	// =============================================
	// 基础属性及设置
	// =============================================
	DataSource dataSource;
	String packagesToScan; // multi-value separated by comma ","
	String configLocations; // multi-value separated by comma ","
	boolean showSql; // show sql or not
	boolean updateTable; // update table or not

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

	// =============================================
	// 基础方法
	// =============================================

	@Override
	public void init() throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T queryFirst(PstmtMeta pstmt, Class<T> type, Object param) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			ps = conn.prepareStatement(pstmt.psql);

			JdbcMeta setjm = null;
			if (param != null) {
				setjm = JdbcMeta.get(type);
			}
			int pos = 0;
			for (Param p : pstmt.param) {
				++pos;
				if (p.setted) {
					JdbcMeta.setParamByType(ps, pos, p.value);
				} else if (setjm != null) {
					setjm.setParam(ps, pos, param, p.name);
				} else {
					ps.setNull(pos, Types.OTHER);
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
	public <T> List<T> queryList(PstmtMeta pstmt, Class<T> type, Object param) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {
			ps = conn.prepareStatement(pstmt.psql);
			JdbcMeta setjm = null;
			if (param != null) {
				setjm = JdbcMeta.get(type);
			}
			int pos = 0;
			for (Param p : pstmt.param) {
				++pos;
				if (p.setted) {
					JdbcMeta.setParamByType(ps, pos, p.value);
				} else if (setjm != null) {
					setjm.setParam(ps, pos, param, p.name);
				} else {
					ps.setNull(pos, Types.OTHER);
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

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection conn = DataSourceUtils.getConnection(dataSource);
		try {

			if (pstmt.select == PstmtMeta.UNSET) {
				SqlDqlKit.parsePstmtIndex(pstmt);
			}

			ps = conn.prepareStatement(pstmt.psql);
			JdbcMeta setjm = null;
			if (param != null) {
				setjm = JdbcMeta.get(type);
			}
			int pos = 0;
			for (Param p : pstmt.param) {
				++pos;
				if (p.setted) {
					JdbcMeta.setParamByType(ps, pos, p.value);
				} else if (setjm != null) {
					setjm.setParam(ps, pos, param, p.name);
				} else {
					ps.setNull(pos, Types.OTHER);
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

}

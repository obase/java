package com.github.obase.mysql;

import java.sql.SQLException;
import java.util.List;

import com.github.obase.Page;
import com.github.obase.mysql.core.PstmtMeta;
import com.mysql.jdbc.Statement;

/**
 * Spring-mysqlclient core interface, which provides CRUD its bulk form @Table class.
 * 
 * <dl>
 * <dt>MysqlClient there are two important realization:
 * <dd>MysqlClientPlatformTransactionImpl with the Spring container-managed transaction to perform JDBC operation
 * <dd>MysqlConnectTransactionImpl manage its own affairs Connection transaction
 * </dl>
 * 
 * @author hezhaowu
 * @since 1.1.1-SNAPSHOT
 */
public interface MysqlClient {

	String DIRECTION_DESC = "DESC";
	String DIRECTION_ASC = "ASC";

	void init() throws SQLException;

	// =====================================================
	// 静态SQL处理方法
	// =====================================================
	<T> T queryFirst(PstmtMeta pstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryList(PstmtMeta pstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryRange(PstmtMeta pstmt, Class<T> type, int offset, int count, Object param) throws SQLException;

	<T> void queryPage(PstmtMeta cpstmt, PstmtMeta dpstmt, Page<T> page, Object param) throws SQLException;

	<T> void queryPage(PstmtMeta pstmt, Page<T> page, Object param) throws SQLException;

	int executeUpdate(PstmtMeta pstmt, Object param) throws SQLException;

	<R> R executeUpdate(PstmtMeta pstmt, Class<R> generateKeyType, Object param) throws SQLException;

	int[] executeBatch(PstmtMeta pstmt, List<Object> params) throws SQLException;

	<R> List<R> executeBatch(PstmtMeta pstmt, Class<R> generateKeyType, List<Object> params) throws SQLException;

	// =====================================================
	// 动态SQL处理方法
	// =====================================================
	<T> T queryFirst(Statement xstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryList(Statement xstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryRange(Statement xstmt, Class<T> type, int offset, int count, Object param) throws SQLException;

	<T> void queryPage(Statement cstmt, Statement dstmt, Page<T> page, Object param) throws SQLException;

	<T> void queryPage(Statement xstmt, Page<T> page, Object param) throws SQLException;

	int executeUpdate(Statement xstmt, Object param) throws SQLException;

	<R> R executeUpdate(Statement xstmt, Class<R> generateKeyType, Object param) throws SQLException;

	int[] executeBatch(Statement xstmt, List<Object> params) throws SQLException;

	<R> List<R> executeBatch(Statement xstmt, Class<R> generateKeyType, List<Object> params) throws SQLException;
}
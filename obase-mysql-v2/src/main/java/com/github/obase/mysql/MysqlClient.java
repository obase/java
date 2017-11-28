package com.github.obase.mysql;

import java.sql.SQLException;
import java.util.List;

import com.github.obase.Page;
import com.github.obase.mysql.core.PstmtMeta;
import com.github.obase.mysql.stmt.Statement;

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

	void init() throws Exception;

	// =====================================================
	// 静态SQL处理方法
	// =====================================================
	<T> T queryFirst(PstmtMeta pstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryList(PstmtMeta pstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryRange(PstmtMeta pstmt, Class<T> type, int offset, int count, Object param) throws SQLException;

	<T> void queryPage(PstmtMeta pstmt, Class<T> type, Page<T> page, Object param) throws SQLException;

	int executeUpdate(PstmtMeta pstmt, Object param) throws SQLException;

	<R> R executeUpdate(PstmtMeta pstmt, Class<R> generateKeyType, Object param) throws SQLException;

	<T> int[] executeBatch(PstmtMeta pstmt, List<T> params) throws SQLException;

	<T, R> List<R> executeBatch(PstmtMeta pstmt, Class<R> generateKeyType, List<T> params) throws SQLException;

	// =====================================================
	// 动态SQL处理方法
	// =====================================================
	<T> T queryFirst(Statement xstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryList(Statement xstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryRange(Statement xstmt, Class<T> type, int offset, int count, Object param) throws SQLException;

	<T> void queryPage(Statement xstmt, Class<T> type, Page<T> page, Object param) throws SQLException;

	int executeUpdate(Statement xstmt, Object param) throws SQLException;

	<R> R executeUpdate(Statement xstmt, Class<R> generateKeyType, Object param) throws SQLException;

	<T> int[] executeBatch(Statement xstmt, List<T> params) throws SQLException;

	<T, R> List<R> executeBatch(Statement xstmt, Class<R> generateKeyType, List<T> params) throws SQLException;

	// =====================================================
	// ORM相关方法
	// =====================================================
	<T> List<T> selectList(Class<T> table) throws SQLException;

	<T> T selectFirst(Class<T> tableType) throws SQLException;

	<T> List<T> selectRange(Class<T> tableType, int offset, int count) throws SQLException;

	<T> void selectPage(Class<T> tableType, Page<T> page) throws SQLException;

	<T> T select(Class<T> table, Object object) throws SQLException;

	<T> void select2(Class<T> table, Object object) throws SQLException;

	<T> T selectByKey(Class<T> table, Object... keys) throws SQLException;

	<T> int insert(Class<T> table, Object object) throws SQLException;

	<T, R> R insert(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException;

	<T> int insertIgnore(Class<?> tableType, T tableObject) throws SQLException;

	<T, R> R insertIgnore(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException;

	<T> int replace(Class<?> tableType, T tableObject) throws SQLException;

	<T, R> R replace(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException;

	<T> int merge(Class<?> tableType, T tableObject) throws SQLException;

	<T, R> R merge(Class<?> tableType, T tableObject, Class<R> generatedKeyType) throws SQLException;

	<T> int update(Class<?> tableType, T tableObject) throws SQLException;

	<T> int delete(Class<?> tableType, T tableObject) throws SQLException;

	<T> int deleteByKey(Class<T> tableType, Object... keys) throws SQLException;

	<T> int[] batchInsert(Class<?> tableType, List<T> tableObject) throws SQLException;

	<T, R> List<R> batchInsert(Class<?> tableType, List<T> tableObject, Class<R> generatedKeyType) throws SQLException;

	<T> int[] batchUpdate(Class<?> tableType, List<T> tableObject) throws SQLException;

	<T> int[] batchDelete(Class<?> tableType, List<T> tableObjects) throws SQLException;

	<T> int[] batchDeleteByKey(Class<T> tableType, List<Object[]> keys) throws SQLException;

	// =====================================================
	// Query相关方法
	// =====================================================

	<T> List<T> queryList(String queryId, Class<T> elemType, Object params) throws SQLException;

	<T> List<T> queryRange(String queryId, Class<T> elemType, int offset, int count, Object params) throws SQLException;

	<T> T queryFirst(String queryId, Class<T> elemType, Object params) throws SQLException;

	<T> void queryPage(String queryId, Class<T> elemType, Page<T> page, Object params) throws SQLException;

	int executeUpdate(String updateId, Object params) throws SQLException;

	<R> R executeUpdate(String updateId, Class<R> generateKeyType, Object param) throws SQLException;

	<T> int[] executeBatch(String updateId, List<T> params) throws SQLException;

	<T, R> List<R> executeBatch(String updateId, Class<R> generateKeyType, List<T> params) throws SQLException;
}
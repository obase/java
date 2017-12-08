package com.github.obase.mysql;

import java.sql.SQLException;
import java.util.List;

import com.github.obase.Page;
import com.github.obase.mysql.core.DPstmtMeta;
import com.github.obase.mysql.core.SPstmtMeta;

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
	<T> T queryFirst(SPstmtMeta pstmt, Class<T> type, Object param) throws SQLException;

	boolean queryFirst2(SPstmtMeta pstmt, Object param) throws SQLException;

	<T> List<T> queryList(SPstmtMeta pstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryRange(SPstmtMeta pstmt, Class<T> type, int offset, int count, Object param) throws SQLException;

	<T> void queryPage(SPstmtMeta pstmt, Class<T> type, Page<T> page, Object param) throws SQLException;

	int executeUpdate(SPstmtMeta pstmt, Object param) throws SQLException;

	<R> R executeUpdate(SPstmtMeta pstmt, Class<R> generateKeyType, Object param) throws SQLException;

	<T> int[] executeBatch(SPstmtMeta pstmt, List<T> params) throws SQLException;

	<T, R> List<R> executeBatch(SPstmtMeta pstmt, Class<R> generateKeyType, List<T> params) throws SQLException;

	// =====================================================
	// 动态SQL处理方法
	// =====================================================
	<T> T queryFirst(DPstmtMeta pstmt, Class<T> type, Object param) throws SQLException;

	boolean queryFirst2(DPstmtMeta pstmt, Object param) throws SQLException;

	<T> List<T> queryList(DPstmtMeta pstmt, Class<T> type, Object param) throws SQLException;

	<T> List<T> queryRange(DPstmtMeta pstmt, Class<T> type, int offset, int count, Object param) throws SQLException;

	<T> void queryPage(DPstmtMeta pstmt, Class<T> type, Page<T> page, Object param) throws SQLException;

	int executeUpdate(DPstmtMeta pstmt, Object param) throws SQLException;

	<R> R executeUpdate(DPstmtMeta pstmt, Class<R> generateKeyType, Object param) throws SQLException;

	<T> int[] executeBatch(DPstmtMeta pstmt, List<T> params) throws SQLException;

	<T, R> List<R> executeBatch(DPstmtMeta pstmt, Class<R> generateKeyType, List<T> params) throws SQLException;

	// =====================================================
	// ORM相关方法
	// =====================================================
	<T> List<T> selectList(Class<T> table) throws SQLException;

	<T> T selectFirst(Class<T> table) throws SQLException;

	<T> List<T> selectRange(Class<T> table, int offset, int count) throws SQLException;

	<T> void selectPage(Class<T> table, Page<T> page) throws SQLException;

	<T> T select(Class<T> table, Object object) throws SQLException;

	<T> boolean select2(Class<T> table, Object object) throws SQLException;

	<T> T selectByKey(Class<T> table, Object keys) throws SQLException;

	<T> T selectByKeys(Class<T> table, Object... keys) throws SQLException;

	int insert(Class<?> table, Object object) throws SQLException;

	<R> R insert(Class<?> table, Class<R> generatedKeyType, Object object) throws SQLException;

	int insertIgnore(Class<?> table, Object object) throws SQLException;

	<R> R insertIgnore(Class<?> table, Class<R> generatedKeyType, Object object) throws SQLException;

	int replace(Class<?> table, Object object) throws SQLException;

	<R> R replace(Class<?> table, Class<R> generatedKeyType, Object object) throws SQLException;

	int merge(Class<?> table, Object object) throws SQLException;

	<R> R merge(Class<?> table, Class<R> generatedKeyType, Object object) throws SQLException;

	int update(Class<?> table, Object object) throws SQLException;

	int delete(Class<?> table, Object object) throws SQLException;

	int deleteByKey(Class<?> table, Object keys) throws SQLException;

	int deleteByKeys(Class<?> table, Object... keys) throws SQLException;

	<T> int[] batchInsert(Class<?> table, List<T> objects) throws SQLException;

	<T, R> List<R> batchInsert(Class<?> table, Class<R> generatedKeyType, List<T> objects) throws SQLException;

	<T> int[] batchInsertIgnore(Class<?> table, List<T> objects) throws SQLException;

	<T, R> List<R> batchInsertIgnore(Class<?> table, Class<R> generatedKeyType, List<T> objects) throws SQLException;

	<T> int[] batchReplace(Class<?> table, List<T> objects) throws SQLException;

	<T, R> List<R> batchReplace(Class<?> table, Class<R> generatedKeyType, List<T> objects) throws SQLException;

	<T> int[] batchMerge(Class<?> table, List<T> objects) throws SQLException;

	<T, R> List<R> batchMerge(Class<?> table, Class<R> generatedKeyType, List<T> objects) throws SQLException;

	<T> int[] batchUpdate(Class<?> table, List<T> objects) throws SQLException;

	<T> int[] batchDelete(Class<?> table, List<T> objects) throws SQLException;

	<T> int[] batchDeleteByKey(Class<?> table, List<T> keys) throws SQLException;

	// =====================================================
	// XML处理方法
	// =====================================================

	<T> List<T> queryList(String queryId, Class<T> elemType, Object params) throws SQLException;

	<T> List<T> queryRange(String queryId, Class<T> elemType, int offset, int count, Object params) throws SQLException;

	<T> T queryFirst(String queryId, Class<T> elemType, Object params) throws SQLException;

	boolean queryFirst2(String queryId, Object params) throws SQLException;

	<T> void queryPage(String queryId, Class<T> elemType, Page<T> page, Object params) throws SQLException;

	int executeUpdate(String updateId, Object params) throws SQLException;

	<R> R executeUpdate(String updateId, Class<R> generateKeyType, Object param) throws SQLException;

	<T> int[] executeBatch(String updateId, List<T> params) throws SQLException;

	<T, R> List<R> executeBatch(String updateId, Class<R> generateKeyType, List<T> params) throws SQLException;

}
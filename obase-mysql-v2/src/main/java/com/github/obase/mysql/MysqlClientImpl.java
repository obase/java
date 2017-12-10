package com.github.obase.mysql;

import static com.github.obase.kit.StringKit.isNotEmpty;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.github.obase.MessageException;
import com.github.obase.Page;
import com.github.obase.kit.ClassKit;
import com.github.obase.mysql.asm.AsmKit;
import com.github.obase.mysql.data.ClassMetaInfo;
import com.github.obase.mysql.sql.SqlDdlKit;
import com.github.obase.mysql.sql.SqlMetaKit;
import com.github.obase.mysql.xml.ObaseMysqlObject;
import com.github.obase.mysql.xml.ObaseMysqlParser;
import com.github.obase.mysql.xml.Statement;

public class MysqlClientImpl extends MysqlClientOperation {

	static final Log logger = LogFactory.getLog(MysqlClientOperation.class);

	// =============================================
	// 基础属性及设置
	// =============================================
	protected String packagesToScan; // multi-value separated by comma ","
	protected String configLocations; // multi-value separated by comma ","

	boolean updateTable; // update table or not

	public void setPackagesToScan(String packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	public void setConfigLocations(String configLocations) {
		this.configLocations = configLocations;
	}

	public void setUpdateTable(boolean updateTable) {
		this.updateTable = updateTable;
	}

	// =============================================
	// 全局缓存属性, 由metainfo快速生成的缓存.部分如limit,count,
	// insertIgnore, replace等合并到对应的SQL中
	// =============================================
	final Map<String, Statement> statementCache = new HashMap<String, Statement>(); // xml中statement缓存
	final Map<Class<?>, SPstmtMeta> selectAllCache = new HashMap<Class<?>, SPstmtMeta>(); // 全表查询,同时缓存limit,count
	final Map<Class<?>, SPstmtMeta> selectCache = new HashMap<Class<?>, SPstmtMeta>(); // 记录查询
	final Map<Class<?>, SPstmtMeta> insertCache = new HashMap<Class<?>, SPstmtMeta>(); // 插入
	final Map<Class<?>, SPstmtMeta> insertIgnoreCache = new HashMap<Class<?>, SPstmtMeta>(); // 插入忽略
	final Map<Class<?>, SPstmtMeta> replaceCache = new HashMap<Class<?>, SPstmtMeta>(); // 替换
	final Map<Class<?>, SPstmtMeta> mergeCache = new HashMap<Class<?>, SPstmtMeta>(); // 合并非null,采用insert or update语法
	final Map<Class<?>, SPstmtMeta> updateCache = new HashMap<Class<?>, SPstmtMeta>(); // 更新
	final Map<Class<?>, SPstmtMeta> deleteCache = new HashMap<Class<?>, SPstmtMeta>(); // 删除

	protected void doInit(Connection conn) throws Exception {
		final Pattern separator = Pattern.compile("\\s*,\\s*");
		final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		Map<String, ClassMetaInfo> metaMetaInfoMap = new HashMap<String, ClassMetaInfo>(); // key is classname
		Map<String, ClassMetaInfo> tableMetaInfoMap = new HashMap<String, ClassMetaInfo>(); // key is tablename

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
							metaMetaInfoMap.put(ClassKit.getClassNameFromInternalName(classMetaInfo.internalName), classMetaInfo);
							if (classMetaInfo.tableAnnotation != null) {
								if (logger.isInfoEnabled()) {
									logger.info(String.format("Load @Table: %s %s", classMetaInfo.tableName, classMetaInfo.columns));
								}
								if ((tableMetaInfo = tableMetaInfoMap.put(classMetaInfo.tableName, classMetaInfo)) != null) {
									throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_DUBLICATE_TABLE, "Duplicate table: " + classMetaInfo.tableName + ", please check class:" + classMetaInfo.internalName + "," + tableMetaInfo.internalName);
								}
							}
						}
					}
				}
			}
		}
		if (isNotEmpty(configLocations)) {
			ObaseMysqlParser parser = new ObaseMysqlParser();
			String[] locations = separator.split(configLocations);
			for (String location : locations) {
				if (isNotEmpty(location)) {
					Resource[] rss = resolver.getResources(location);
					for (Resource rs : rss) {
						ObaseMysqlObject configMetaInfo = parser.parse(rs);
						for (Class<?> clazz : configMetaInfo.tableClassList) {
							String className = clazz.getCanonicalName();
							if (!metaMetaInfoMap.containsKey(className)) {
								classMetaInfo = AsmKit.getAnnotationClassMetaInfo(className);

								if (classMetaInfo.tableAnnotation != null || classMetaInfo.metaAnnotation != null) {
									metaMetaInfoMap.put(className, classMetaInfo);
									if (classMetaInfo.tableAnnotation != null) {
										if (logger.isInfoEnabled()) {
											logger.info(String.format("Load @Table: %s %s", classMetaInfo.tableName, classMetaInfo.columns));
										}
										if ((tableMetaInfo = tableMetaInfoMap.put(classMetaInfo.tableName, classMetaInfo)) != null) {
											throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_DUBLICATE_TABLE, "Duplicate table: " + classMetaInfo.tableName + ", please check class:" + classMetaInfo.internalName + "," + tableMetaInfo.internalName);
										}
									}
								}
							}
						}
						for (Class<?> clazz : configMetaInfo.metaClassList) {
							String className = clazz.getCanonicalName();
							if (!metaMetaInfoMap.containsKey(className)) {
								classMetaInfo = AsmKit.getClassMetaInfo(className);
								classMetaInfo.tableAnnotation = null; // FIXBUG:<meta>的类不是<table>
								metaMetaInfoMap.put(className, classMetaInfo);
							}
						}
						for (Statement stmt : configMetaInfo.statementList) {
							// key = namespace + . + id
							sb.setLength(0);
							if (isNotEmpty(configMetaInfo.namespace)) {
								// ignore if not setting namespace
								sb.append(configMetaInfo.namespace).append('.');
							}
							key = sb.append(stmt.id).toString();
							if (statementCache.put(key, stmt) != null) {
								throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_DUPLICATE, "Duplicate statement id: " + key);
							}
						}
					}
				}
			}
		}

		Class<?> clazz;
		for (Map.Entry<String, ClassMetaInfo> entry : metaMetaInfoMap.entrySet()) {

			clazz = ClassKit.forName(entry.getKey());
			classMetaInfo = entry.getValue();
			// 初始化JdbcMeta
			JdbcMeta.set(clazz, AsmKit.newJdbcMeta(classMetaInfo), false);

			// 初始化ORM相关的SQL
			if (classMetaInfo.tableAnnotation != null) {
				selectAllCache.put(clazz, SqlMetaKit.genSelectAllPstmt(classMetaInfo));
				selectCache.put(clazz, SqlMetaKit.genSelectPstmt(classMetaInfo));
				insertCache.put(clazz, SqlMetaKit.genInsertPstmt(classMetaInfo));
				insertIgnoreCache.put(clazz, SqlMetaKit.genInsertIgnorePstmt(classMetaInfo));
				replaceCache.put(clazz, SqlMetaKit.genReplacePstmt(classMetaInfo));
				mergeCache.put(clazz, SqlMetaKit.genMergePstmt(classMetaInfo));
				updateCache.put(clazz, SqlMetaKit.genUpdatePstmt(classMetaInfo));
				deleteCache.put(clazz, SqlMetaKit.genDeletePstmt(classMetaInfo));
			}
		}

		if (updateTable) {
			if (tableMetaInfoMap.size() > 0) {
				SqlDdlKit.processUpdateTable(conn, tableMetaInfoMap);
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Mysqlclient initialization successful, load %d tables, %d metas, and %d statements", tableMetaInfoMap.size(), metaMetaInfoMap.size(), statementCache.size()));
		}
	}

	@Override
	public <T> List<T> selectList(Class<T> table) throws SQLException {
		SPstmtMeta pstmt = selectAllCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return queryList(pstmt, table, null);
	}

	@Override
	public <T> T selectFirst(Class<T> table) throws SQLException {
		SPstmtMeta pstmt = selectAllCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return queryFirst(pstmt, table, null);
	}

	@Override
	public <T> List<T> selectRange(Class<T> table, int offset, int count) throws SQLException {
		SPstmtMeta pstmt = selectAllCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return queryRange(pstmt, table, offset, count, null);
	}

	@Override
	public <T> void selectPage(Class<T> table, Page<T> page) throws SQLException {
		SPstmtMeta pstmt = selectAllCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		queryPage(pstmt, table, page, null);
	}

	@Override
	public <T> T select(Class<T> table, Object object) throws SQLException {
		SPstmtMeta pstmt = selectCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return queryFirst(pstmt, table, object);
	}

	@Override
	public <T> boolean select2(Class<T> table, Object object) throws SQLException {
		SPstmtMeta pstmt = selectCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return queryFirst2(pstmt, object);
	}

	@Override
	public <T> T selectByKey(Class<T> table, Object keys) throws SQLException {
		SPstmtMeta pstmt = selectCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return queryFirst(pstmt, table, keys);
	}

	@Override
	public <T> T selectByKeys(Class<T> table, Object... keys) throws SQLException {
		SPstmtMeta pstmt = selectCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return queryFirst(pstmt, table, keys);
	}

	@Override
	public int insert(Class<?> table, Object object) throws SQLException {
		SPstmtMeta pstmt = insertCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, object);
	}

	@Override
	public <R> R insert(Class<?> table, Class<R> generatedKeyType, Object object) throws SQLException {
		SPstmtMeta pstmt = insertCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, generatedKeyType, object);
	}

	@Override
	public int insertIgnore(Class<?> table, Object object) throws SQLException {
		SPstmtMeta pstmt = insertIgnoreCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, object);
	}

	@Override
	public <R> R insertIgnore(Class<?> table, Class<R> generatedKeyType, Object object) throws SQLException {
		SPstmtMeta pstmt = insertIgnoreCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, generatedKeyType, object);
	}

	@Override
	public int replace(Class<?> table, Object object) throws SQLException {
		SPstmtMeta pstmt = replaceCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, object);
	}

	@Override
	public <R> R replace(Class<?> table, Class<R> generatedKeyType, Object object) throws SQLException {
		SPstmtMeta pstmt = replaceCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, generatedKeyType, object);
	}

	@Override
	public int merge(Class<?> table, Object object) throws SQLException {
		SPstmtMeta pstmt = mergeCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, object);
	}

	@Override
	public <R> R merge(Class<?> table, Class<R> generatedKeyType, Object object) throws SQLException {
		SPstmtMeta pstmt = mergeCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, generatedKeyType, object);
	}

	@Override
	public int update(Class<?> table, Object object) throws SQLException {
		SPstmtMeta pstmt = updateCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, object);
	}

	@Override
	public int delete(Class<?> table, Object object) throws SQLException {
		SPstmtMeta pstmt = deleteCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, object);
	}

	@Override
	public int deleteByKey(Class<?> table, Object keys) throws SQLException {
		SPstmtMeta pstmt = deleteCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, keys);
	}

	@Override
	public int deleteByKeys(Class<?> table, Object... keys) throws SQLException {
		SPstmtMeta pstmt = deleteCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeUpdate(pstmt, keys);
	}

	@Override
	public <T> int[] batchInsert(Class<?> table, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = insertCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, objects);
	}

	@Override
	public <T, R> List<R> batchInsert(Class<?> table, Class<R> generatedKeyType, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = insertCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, generatedKeyType, objects);
	}

	@Override
	public <T> int[] batchUpdate(Class<?> table, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = updateCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, objects);
	}

	@Override
	public <T> int[] batchInsertIgnore(Class<?> table, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = insertIgnoreCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, objects);
	}

	@Override
	public <T, R> List<R> batchInsertIgnore(Class<?> table, Class<R> generatedKeyType, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = insertIgnoreCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, generatedKeyType, objects);
	}

	@Override
	public <T> int[] batchReplace(Class<?> table, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = replaceCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, objects);
	}

	@Override
	public <T, R> List<R> batchReplace(Class<?> table, Class<R> generatedKeyType, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = replaceCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, generatedKeyType, objects);
	}

	@Override
	public <T> int[] batchMerge(Class<?> table, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = mergeCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, objects);
	}

	@Override
	public <T, R> List<R> batchMerge(Class<?> table, Class<R> generatedKeyType, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = mergeCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, generatedKeyType, objects);
	}

	@Override
	public <T> int[] batchDelete(Class<?> table, List<T> objects) throws SQLException {
		SPstmtMeta pstmt = deleteCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, objects);
	}

	@Override
	public <T> int[] batchDeleteByKey(Class<?> table, List<T> keys) throws SQLException {
		SPstmtMeta pstmt = deleteCache.get(table);
		if (pstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found table: " + table);
		}
		return executeBatch(pstmt, keys);
	}

	@Override
	public <T> List<T> queryList(String queryId, Class<T> elemType, Object params) throws SQLException {
		Statement xstmt = statementCache.get(queryId);
		if (xstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found statement: " + queryId);
		}
		return queryList(xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(JdbcMeta.getByObj(params), params), elemType, params);
	}

	@Override
	public <T> List<T> queryRange(String queryId, Class<T> elemType, int offset, int count, Object params) throws SQLException {
		Statement xstmt = statementCache.get(queryId);
		if (xstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found statement: " + queryId);
		}
		return queryRange(xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(JdbcMeta.getByObj(params), params), elemType, offset, count, params);
	}

	@Override
	public <T> T queryFirst(String queryId, Class<T> elemType, Object params) throws SQLException {
		Statement xstmt = statementCache.get(queryId);
		if (xstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found statement: " + queryId);
		}
		return queryFirst(xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(JdbcMeta.getByObj(params), params), elemType, params);
	}

	@Override
	public boolean queryFirst2(String queryId, Object params) throws SQLException {
		Statement xstmt = statementCache.get(queryId);
		if (xstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found statement: " + queryId);
		}
		return queryFirst2(xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(JdbcMeta.getByObj(params), params), params);
	}

	@Override
	public <T> void queryPage(String queryId, Class<T> elemType, Page<T> page, Object params) throws SQLException {
		Statement xstmt = statementCache.get(queryId);
		if (xstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found statement: " + queryId);
		}
		queryPage(xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(JdbcMeta.getByObj(params), params), elemType, page, params);
	}

	@Override
	public int executeUpdate(String updateId, Object params) throws SQLException {
		Statement xstmt = statementCache.get(updateId);
		if (xstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found statement: " + updateId);
		}
		return executeUpdate(xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(JdbcMeta.getByObj(params), params), params);
	}

	@Override
	public <R> R executeUpdate(String updateId, Class<R> generateKeyType, Object params) throws SQLException {
		Statement xstmt = statementCache.get(updateId);
		if (xstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found statement: " + updateId);
		}
		return executeUpdate(xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(JdbcMeta.getByObj(params), params), generateKeyType, params);
	}

	@Override
	public <T> int[] executeBatch(String updateId, List<T> params) throws SQLException {
		Statement xstmt = statementCache.get(updateId);
		if (xstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found statement: " + updateId);
		}
		if (xstmt.staticPstmtMeta != null) {
			return executeBatch(xstmt.staticPstmtMeta != null ? xstmt.staticPstmtMeta : xstmt.dynamicPstmtMeta(JdbcMeta.getByObj(params), params), params);
		}
		throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_DYNAMIC_NOT_SUPPORT, "executeBatch don't support dynamic statement: " + xstmt.id);
	}

	@Override
	public <T, R> List<R> executeBatch(String updateId, Class<R> generateKeyType, List<T> params) throws SQLException {
		Statement xstmt = statementCache.get(updateId);
		if (xstmt == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_NOT_FOUND, "Not found statement: " + updateId);
		}
		if (xstmt.staticPstmtMeta != null) {
			return executeBatch(xstmt.staticPstmtMeta, generateKeyType, params);
		}
		throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_DYNAMIC_NOT_SUPPORT, "executeBatch don't support dynamic statement: " + xstmt.id);
	}

}

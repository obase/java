package com.github.obase.mysql;

import static com.github.obase.kit.StringKit.isNotEmpty;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.github.obase.MessageException;
import com.github.obase.kit.ClassKit;
import com.github.obase.mysql.asm.AsmKit;
import com.github.obase.mysql.core.JdbcMeta;
import com.github.obase.mysql.core.PstmtMeta;
import com.github.obase.mysql.data.ClassMetaInfo;
import com.github.obase.mysql.stmt.Statement;
import com.github.obase.mysql.syntax.SqlDdlKit;
import com.github.obase.mysql.syntax.SqlMetaKit;
import com.github.obase.mysql.xml.ObaseMysqlObject;
import com.github.obase.mysql.xml.ObaseMysqlParser;

public class MysqlClientImpl extends MysqlClientBase {

	static final Log logger = LogFactory.getLog(MysqlClientBase.class);

	// =============================================
	// 基础属性及设置
	// =============================================
	String packagesToScan; // multi-value separated by comma ","
	String configLocations; // multi-value separated by comma ","
	boolean showSql; // show sql or not
	boolean updateTable; // update table or not

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
	// 全局缓存属性, 由metainfo快速生成的缓存.部分如limit,count,
	// insertIgnore, replace等合并到对应的SQL中
	// =============================================
	final Map<String, Statement> statementCache = new HashMap<String, Statement>(); // xml中statement缓存
	// selectAllCache同时缓存limit,count
	final Map<Class<?>, PstmtMeta> selectAllCache = new HashMap<Class<?>, PstmtMeta>(); // table全表搜索缓存
	final Map<Class<?>, PstmtMeta> selectCache = new HashMap<Class<?>, PstmtMeta>(); // table全表搜索缓存
	// insertCache同时缓存insertIgnore, replace
	final Map<Class<?>, PstmtMeta> insertCache = new HashMap<Class<?>, PstmtMeta>(); // table全表搜索缓存
	// 采用insert or update语法
	final Map<Class<?>, PstmtMeta> mergeCache = new HashMap<Class<?>, PstmtMeta>(); // table全表搜索缓存
	final Map<Class<?>, PstmtMeta> updateCache = new HashMap<Class<?>, PstmtMeta>(); // table全表搜索缓存
	final Map<Class<?>, PstmtMeta> deleteCache = new HashMap<Class<?>, PstmtMeta>(); // table全表搜索缓存

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
									throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_DUBLICATE_TABLE,
											"Duplicate @Table: " + classMetaInfo.tableName + ", please check class:" + classMetaInfo.internalName + "," + tableMetaInfo.internalName);
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
											throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.META_INFO_DUBLICATE_TABLE,
													"Duplicate @Table: " + classMetaInfo.tableName + ", please check class:" + classMetaInfo.internalName + "," + tableMetaInfo.internalName);
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
			logger.info(String.format("Mysqlclient initialization successful, loading %d Tables, %d Metas, and %d SQLs", tableMetaInfoMap.size(), metaMetaInfoMap.size(), statementCache.size()));
		}
	}

}

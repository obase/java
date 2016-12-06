package com.github.obase.mysql.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.obase.mysql.JavaType;
import com.github.obase.mysql.MysqlClientException;
import com.github.obase.mysql.annotation.SqlType;
import com.github.obase.mysql.asm.AsmKit;
import com.github.obase.mysql.data.ClassMetaInfo;
import com.github.obase.mysql.data.ColumnAnnotation;
import com.github.obase.mysql.data.FieldMetaInfo;
import com.github.obase.mysql.data.IndexAnnotation;
import com.github.obase.mysql.data.PrimaryKeyAnnotation;
import com.github.obase.mysql.data.ReferenceAnnotation;
import com.github.obase.mysql.data.TableAnnotation;

public class SqlDdlKit extends SqlKit {

	private static final Log logger = LogFactory.getLog(SqlDdlKit.class);

	public static void processUpdateTable(Connection conn, Map<String, ClassMetaInfo> tableMetaInfoMap) throws SQLException {

		LinkedList<String> depends = new LinkedList<String>();
		String name;
		for (ClassMetaInfo classMetaInfo : tableMetaInfoMap.values()) {
			name = classMetaInfo.tableName;
			depends.remove(name);
			depends.addFirst(name);
			if (classMetaInfo.foreignKeyAnnotation != null) {
				for (ReferenceAnnotation ref : classMetaInfo.foreignKeyAnnotation) {
					name = ref.targetTable;
					depends.remove(name);
					depends.addFirst(name);
				}
			}
		}

		Set<String> tableSet = getUpperCaseTableNames(conn);
		for (String table : depends) {
			ClassMetaInfo classMetaInfo = tableMetaInfoMap.get(table);
			if (tableSet.contains(classMetaInfo.tableName.toUpperCase())) {
				logger.info("Check Table: " + classMetaInfo.tableName);
				checkAndAddColumns(conn, classMetaInfo, classMetaInfo.tableName);
				checkAndAddPrimaryKey(conn, classMetaInfo, classMetaInfo.tableName);
				checkAndAddForeignKey(conn, classMetaInfo, classMetaInfo.tableName);
				checkAndAddIndexes(conn, classMetaInfo, classMetaInfo.tableName);
			} else {
				logger.info("Create Table: " + classMetaInfo.tableName);
				createTable(conn, classMetaInfo);
			}
		}
	}

	public static void processCheckConfig(Connection conn, Collection<SqlMeta> sqlMetas) {
		// @TODO
	}

	private static void createTable(Connection conn, ClassMetaInfo classMetaInfo) throws SQLException {

		String ddl = genTableDdl(classMetaInfo);
		logger.info(ddl);

		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(ddl);
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static Set<String> getUpperCaseTableNames(Connection conn) throws SQLException {
		Set<String> tables = new HashSet<String>();
		ResultSet rs = null;
		try {
			DatabaseMetaData dbmd = conn.getMetaData();
			rs = dbmd.getTables(null, null, null, null);
			while (rs.next()) {
				tables.add(rs.getString(3).toUpperCase());
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}
		return tables;
	}

	public static void checkAndAddColumns(Connection conn, ClassMetaInfo classMetaInfo, String table) throws SQLException {

		Map<String, ColInfo> colInfoMap = new HashMap<String, ColInfo>(classMetaInfo.fields.size());

		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = null;
		try {
			rs = dbmd.getColumns(null, null, table, null);
			while (rs.next()) {
				ColInfo ca = new ColInfo();
				ca.name = rs.getString("COLUMN_NAME"); // COLUMN_NAME
				ca.type = rs.getInt("DATA_TYPE");// DATA_TYPE
				ca.length = rs.getInt("COLUMN_SIZE");// COLUMN_SIZE
				ca.decimals = rs.getInt("DECIMAL_DIGITS");// DECIMAL_DIGITS
				ca.notNull = "YES".equals(rs.getString("IS_NULLABLE"));// NULLABLE
				ca.autoIncrement = "YES".equals(rs.getString("IS_AUTOINCREMENT"));// IS_AUTOINCREMENT
				colInfoMap.put(ca.name, ca);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		ColumnAnnotation columnAnnotation;
		Statement stmt = null;

		for (FieldMetaInfo fieldMetaInfo : classMetaInfo.fields.values()) {
			if ((columnAnnotation = fieldMetaInfo.columnAnnotation) != null) {
				String name = getColumnName(fieldMetaInfo, columnAnnotation);
				ColInfo colInfo = colInfoMap.get(name);
				if (colInfo == null) {
					StringBuilder sb = new StringBuilder(256);
					sb.append("ALTER TABLE ").append(identifier(classMetaInfo.tableName)).append(" ADD COLUMN ").append(genColumnDdl(fieldMetaInfo, columnAnnotation));
					String sql = sb.toString();
					logger.info(sql);

					try {
						stmt = conn.createStatement();
						stmt.executeUpdate(sql);
					} catch (SQLException e) {
						throw new MysqlClientException("Add column failed for: " + classMetaInfo.tableName, e);
					} finally {
						if (stmt != null) {
							stmt.close();
						}
					}
				}
			}
		}

	}

	private static final String getColumnName(FieldMetaInfo fieldMetaInfo, ColumnAnnotation columnAnnotation) {
		String name = columnAnnotation.name;
		if (AsmKit.isEmpty(name)) {
			name = fieldMetaInfo.name;
		}
		return name;
	}

	private static final SqlType getColumnSqlType(ColumnAnnotation columnAnnotation, JavaType javaType) {
		SqlType type = columnAnnotation.type;
		if (type == null) {
			type = javaType.defaultSqlType;
		}
		return type;
	}

	private static final Integer getColumnLength(ColumnAnnotation columnAnnotation, JavaType javaType) {
		Integer length = columnAnnotation.length;
		if (length == null) {
			length = javaType.defaultLength;
		}
		return length;
	}

	private static final Integer getColumnDecimal(ColumnAnnotation columnAnnotation, JavaType javaType) {
		Integer decimals = columnAnnotation.decimals;
		if (decimals == null) {
			decimals = javaType.defaultDecimals;
		}
		return decimals;
	}

	static class ColInfo {
		String name;
		int type;
		int length;
		int decimals;
		boolean notNull;
		boolean autoIncrement;
	}

	public static void checkAndAddPrimaryKey(Connection conn, ClassMetaInfo classMetaInfo, String table) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();

		List<String> keys = new LinkedList<String>();
		ResultSet rs = null;
		try {
			rs = dbmd.getPrimaryKeys(null, null, table);
			while (rs.next()) {
				keys.add(rs.getString("COLUMN_NAME"));
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
		}

		if (keys.size() == 0) {
			if (classMetaInfo.keys != null && classMetaInfo.keys.size() > 0) {
				StringBuilder sb = new StringBuilder(128);
				sb.append("ALTER TABLE ").append(identifier(classMetaInfo.tableName)).append("ADD PRIMARY KEY(");
				for (String key : classMetaInfo.keys) {
					sb.append(identifier(key)).append(',');
				}
				sb.setCharAt(sb.length() - 1, ')');
				logger.info(sb.toString());

				Statement stmt = null;
				try {
					stmt = conn.createStatement();
					stmt.executeUpdate(sb.toString());
				} catch (SQLException e) {
					throw new MysqlClientException("Add primary key failed for: " + classMetaInfo.tableName, e);
				} finally {
					if (stmt != null) {
						stmt.close();
					}
				}
			}
		} else if (!equalsIgnoreOrder(keys, classMetaInfo.keys)) {
			logger.warn("Table primary keys conflict: meta" + classMetaInfo.keys + ",ddl" + keys);
		}
	}

	private static boolean equalsIgnoreOrder(List<String> list1, List<String> list2) {
		if (list1 == list2) {
			return true;
		}
		if (list1 == null || list2 == null) {
			return false;
		}
		return list1.size() == list2.size() && list1.containsAll(list2);
	}

	public static void checkAndAddForeignKey(Connection conn, ClassMetaInfo classMetaInfo, String table) throws SQLException {
		List<ReferenceAnnotation> foreignKeyAnnotation = classMetaInfo.foreignKeyAnnotation;
		if (foreignKeyAnnotation != null && foreignKeyAnnotation.size() > 0) {
			Set<String> names = new HashSet<String>();
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = null;
			try {
				rs = dbmd.getImportedKeys(null, null, table);
				while (rs.next()) {
					names.add(rs.getString("FK_NAME"));
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}
			for (ReferenceAnnotation ra : foreignKeyAnnotation) {
				if (!names.contains(ra.name)) {
					StringBuilder sb = new StringBuilder(256);
					sb.append("ALTER TABLE ").append(identifier(table)).append(" ADD ").append(genForeignKeyDdl(ra));

					logger.info(sb.toString());
					Statement stmt = null;
					try {
						stmt = conn.createStatement();
						stmt.executeUpdate(sb.toString());
					} catch (SQLException e) {
						throw new MysqlClientException("Craate foreign key failed for: " + classMetaInfo.tableName, e);
					} finally {
						if (stmt != null) {
							stmt.close();
						}
					}
				}
			}
		}
	}

	public static void checkAndAddIndexes(Connection conn, ClassMetaInfo classMetaInfo, String table) throws SQLException {
		List<IndexAnnotation> indexesAnnotation = classMetaInfo.indexesAnnotation;
		if (indexesAnnotation != null && indexesAnnotation.size() > 0) {
			Set<String> names = new HashSet<String>();
			DatabaseMetaData dbmd = conn.getMetaData();
			ResultSet rs = null;
			try {
				rs = dbmd.getIndexInfo(null, null, table, false, true);
				while (rs.next()) {
					names.add(rs.getString("INDEX_NAME"));
				}
			} finally {
				if (rs != null) {
					rs.close();
				}
			}

			for (IndexAnnotation ia : indexesAnnotation) {
				if (!names.contains(ia.name)) {
					StringBuilder sb = new StringBuilder(256);
					sb.append("ALTER TABLE ").append(identifier(table)).append(" ADD ").append(genIndexesDdl(ia));

					logger.info(sb.toString());
					Statement stmt = null;
					try {
						stmt = conn.createStatement();
						stmt.executeUpdate(sb.toString());
					} catch (SQLException e) {
						throw new MysqlClientException("Add index failed for: " + classMetaInfo.tableName, e);
					} finally {
						if (stmt != null) {
							stmt.close();
						}
					}
				}
			}
		}
	}

	public static String genTableDdl(ClassMetaInfo classMetaInfo) {

		StringBuilder cols = new StringBuilder(128);
		for (FieldMetaInfo fieldMetaInfo : classMetaInfo.fields.values()) {
			ColumnAnnotation columnAnnotation = fieldMetaInfo.columnAnnotation;
			if (columnAnnotation != null) {
				if (cols.length() > 0) {
					cols.append(",\n");
				}
				cols.append(genColumnDdl(fieldMetaInfo, columnAnnotation));
			}
		}

		PrimaryKeyAnnotation primaryKeyAnnotation = classMetaInfo.primaryKeyAnnotation;
		if (primaryKeyAnnotation != null) {
			cols.append(",\n");
			cols.append(genPrimaryKeyDdl(primaryKeyAnnotation));
		}

		List<ReferenceAnnotation> foreignKeyAnnotation = classMetaInfo.foreignKeyAnnotation;
		if (foreignKeyAnnotation != null && foreignKeyAnnotation.size() > 0) {
			for (ReferenceAnnotation ra : foreignKeyAnnotation) {
				cols.append(",\n").append(genForeignKeyDdl(ra));
			}
		}

		List<IndexAnnotation> indexesAnnotation = classMetaInfo.indexesAnnotation;
		if (indexesAnnotation != null && indexesAnnotation.size() > 0) {
			for (IndexAnnotation ia : indexesAnnotation) {
				cols.append(",\n").append(genIndexesDdl(ia));
			}
		}

		StringBuilder sb = new StringBuilder(2048);
		sb.append("CREATE TABLE IF NOT EXISTS ").append(identifier(classMetaInfo.tableName)).append("(\n").append(cols).append("\n)");

		TableAnnotation tableAnnotation = classMetaInfo.tableAnnotation;
		if (tableAnnotation.engine != null && tableAnnotation.engine.sqlValue != null) {
			sb.append("\nENGINE=").append(tableAnnotation.engine.sqlValue);
		}

		if (AsmKit.isNotEmpty(tableAnnotation.characterSet)) {
			sb.append("\nDEFAULT CHARACTER SET=").append(tableAnnotation.characterSet);
		}

		if (AsmKit.isNotEmpty(tableAnnotation.collate)) {
			sb.append("\nCOLLATE=").append(tableAnnotation.collate);
		}
		if (AsmKit.isNotEmpty(tableAnnotation.comment)) {
			sb.append("\nCOMMENT=").append('\'').append(tableAnnotation.comment).append('\'');
		}

		return sb.toString();
	}

	private static String genPrimaryKeyDdl(PrimaryKeyAnnotation primaryKeyAnnotation) {
		StringBuilder cols = new StringBuilder(256);
		cols.append("PRIMARY KEY(");
		for (String key : primaryKeyAnnotation.columns) {
			cols.append(identifier(key)).append(',');
		}
		cols.setCharAt(cols.length() - 1, ')');

		if (primaryKeyAnnotation.using != null && primaryKeyAnnotation.using.sqlValue != null) {
			cols.append(" USING ").append(primaryKeyAnnotation.using.sqlValue);
		}
		return cols.toString();
	}

	private static String genIndexesDdl(IndexAnnotation ia) {
		StringBuilder cols = new StringBuilder(1024);

		if (ia.type != null && ia.type.sqlValue != null) {
			cols.append(ia.type.sqlValue).append(" ");
		}
		cols.append("INDEX ").append(identifier(ia.name)).append("(");
		for (String col : ia.columns) {
			cols.append(identifier(col)).append(',');
		}
		cols.setCharAt(cols.length() - 1, ')');
		if (ia.using != null && ia.using.sqlValue != null) {
			cols.append(" USING ").append(ia.using.sqlValue);
		}
		return cols.toString();
	}

	private static String genForeignKeyDdl(ReferenceAnnotation ra) {
		StringBuilder cols = new StringBuilder(1024);

		cols.append("CONSTRAINT ").append(identifier(ra.name)).append(" FOREIGN KEY(");
		for (String col : ra.columns) {
			cols.append(identifier(col)).append(',');
		}
		cols.setCharAt(cols.length() - 1, ')');
		cols.append(" REFERENCES ").append(identifier(ra.targetTable)).append("(");
		for (String col : ra.targetColumns) {
			cols.append(identifier(col)).append(',');
		}
		cols.setCharAt(cols.length() - 1, ')');

		return cols.toString();
	}

	private static String genColumnDdl(FieldMetaInfo fieldMetaInfo, ColumnAnnotation columnAnnotation) {
		StringBuilder cols = new StringBuilder(128);

		String name = getColumnName(fieldMetaInfo, columnAnnotation);
		JavaType javaType = JavaType.match(fieldMetaInfo.descriptor);
		SqlType sqlType = getColumnSqlType(columnAnnotation, javaType);
		if (sqlType == null) {
			throw new MysqlClientException("Not specify sqlType for field: " + fieldMetaInfo.name);
		}
		cols.append(identifier(name)).append(" ").append(sqlType.sqlValue);

		Integer length = getColumnLength(columnAnnotation, javaType);
		if (length != null) {
			cols.append("(").append(length);
			Integer decimals = getColumnDecimal(columnAnnotation, javaType);
			if (decimals != null) {
				cols.append(",").append(decimals);
			}
			cols.append(")");
		}
		if (Boolean.TRUE.equals(columnAnnotation.notNull)) {
			cols.append(" NOT NULL");
		}
		if (Boolean.TRUE.equals(columnAnnotation.unique)) {
			cols.append(" UNIQUE");
		}
		if (Boolean.TRUE.equals(columnAnnotation.autoIncrement)) {
			cols.append(" AUTO_INCREMENT");
		}
		if (Boolean.TRUE.equals(columnAnnotation.key)) {
			cols.append(" PRIMARY KEY");
		}
		if (!"\0".equals(columnAnnotation.defaultValue)) { // FIXBUG: default value may be ''
			cols.append(" DEFAULT ").append(formatDefaultValue(columnAnnotation.defaultValue, sqlType));
		}
		if (AsmKit.isNotEmpty(columnAnnotation.comment)) {
			cols.append(" COMMENT ").append('\'').append(columnAnnotation.comment).append('\'');
		}

		return cols.toString();
	}

	private static String formatDefaultValue(String defaultValue, SqlType sqlType) {
		switch (sqlType) {
		case NULL:
		case BIT:
		case TINYINT:
		case SMALLINT:
		case MEDIUMINT:
		case INT:
		case INTEGER:
		case BIGINT:
		case REAL:
		case DOUBLE:
		case FLOAT:
		case DECIMAL:
		case NUMERIC:
			return defaultValue;
		default:
			StringBuilder sb = new StringBuilder(defaultValue.length() + 4);
			sb.append(defaultValue);
			if (sb.length() > 0) {
				if (sb.charAt(0) != '\'') {
					sb.insert(0, '\'');
				}
				if (sb.charAt(sb.length() - 1) != '\'') {
					sb.append('\'');
				}
			} else {
				sb.append("''");
			}
			return sb.toString();
		}
	}

}

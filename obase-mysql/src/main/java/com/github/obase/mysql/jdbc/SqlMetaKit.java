package com.github.obase.mysql.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.obase.mysql.data.ClassMetaInfo;

public final class SqlMetaKit extends SqlKit {

	public static String modifyPsqlForInsertIgnore(SqlMeta meta) {
		StringBuilder sb = new StringBuilder(meta.psql.length() + 16);
		sb.append(meta.psql);
		sb.insert("INSERT".length(), " IGNORE");
		return sb.toString();
	}

	public static String modifyPsqlForLimit(SqlMeta meta, int start, int max) {
		StringBuilder sb = new StringBuilder(meta.psql.length() + 16);
		sb.append(meta.psql);
		if (meta.limitIndex > 0) {
			sb.delete(meta.limitIndex, sb.length());
		}
		sb.append(" LIMIT ").append(start).append(',').append(max);
		return sb.toString();
	}

	public static String modifyPsqlForPageTotal(SqlMeta meta) {
		StringBuilder sb = new StringBuilder(meta.psql.length() + 64);
		sb.append("SELECT COUNT(1) FROM (").append(meta.psql).append(") _");
		return sb.toString();
	}

	public static String modifyPsqlForPageLimit(SqlMeta meta, int start, int max, String orderBy, boolean desc) {
		StringBuilder sb = new StringBuilder(meta.psql.length() + 128);
		sb.append("SELECT * FROM (").append(meta.psql).append(") _");
		if (orderBy != null) {
			sb.append(" ORDER BY ").append(identifier(orderBy)).append(" ").append(desc ? "DESC" : "ASC");
		}
		sb.append(" LIMIT ").append(start).append(',').append(max);
		return sb.toString();
	}

	public static void fillSqlMetaLables(ResultSet rs, final SqlMeta meta) throws SQLException {
		synchronized (meta) {
			if (meta.labels == null) {
				Map<String, Integer> labels = new HashMap<String, Integer>();
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 1, n = rsmd.getColumnCount(); i <= n; i++) {
					labels.put(rsmd.getColumnLabel(i), i);
				}
				meta.labels = labels;
			}
		}
	}

	public static SqlMeta genConfigSqlMeta(String id, String sql) {
		SqlMeta meta = null;
		ParamHolder[] holders = parseParamHolderList(sql);
		if (holders.length > 0) {

			StringBuilder sb = new StringBuilder(sql);
			Map<String, List<Integer>> tmp = new HashMap<String, List<Integer>>(holders.length);
			for (int i = holders.length - 1; i >= 0; i--) {
				ParamHolder holder = holders[i];
				sb.replace(holder.start, holder.end, "?");
				List<Integer> pos = tmp.get(holder.name);
				if (pos == null) {
					pos = new LinkedList<Integer>();
					tmp.put(holder.name, pos);
				}
				pos.add(i + 1);
			}

			String psql = sb.toString();
			Map<String, int[]> params = new HashMap<String, int[]>(tmp.size());
			for (Map.Entry<String, List<Integer>> entry : tmp.entrySet()) {

				String name = entry.getKey();
				List<Integer> list = entry.getValue();
				int[] array = new int[list.size()];
				int idx = 0;
				for (Integer p : list) {
					array[idx++] = p.intValue();
				}

				params.put(name, array);
			}

			meta = new SqlMeta(psql, Collections.unmodifiableMap(params), parsePlaceHolderList(psql), parseLimitIndexIfExist(psql));
		} else {
			meta = new SqlMeta(sql, Collections.<String, int[]> emptyMap(), parseLimitIndexIfExist(sql));
		}

		return meta;
	}

	public static SqlMeta genSelectAllSqlMeta(ClassMetaInfo classMetaInfo) {

		if (classMetaInfo.tableAnnotation == null) {
			return null;
		}

		StringBuilder select = new StringBuilder(512);

		StringBuilder colsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
			}
			colsStr.append(identifier(field));
		}
		select.append("SELECT ").append(colsStr).append(" FROM ").append(identifier(classMetaInfo.tableName));

		return new SqlMeta(select.toString(), null, -1);
	}

	public static SqlMeta genSelectSqlMeta(ClassMetaInfo classMetaInfo) {

		if (classMetaInfo.tableAnnotation == null) {
			return null;
		}

		StringBuilder select = new StringBuilder(512);
		Map<String, int[]> params = new HashMap<String, int[]>();
		int pos = 0;

		StringBuilder colsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
			}
			colsStr.append(identifier(field));
		}
		select.append("SELECT ").append(colsStr).append(" FROM ").append(identifier(classMetaInfo.tableName));

		StringBuilder whereStr = new StringBuilder(128);
		for (String field : classMetaInfo.keys) {
			if (whereStr.length() > 0) {
				whereStr.append(" AND ");
			}
			whereStr.append(identifier(field)).append("=?");
			params.put(field, new int[] { ++pos });
		}
		select.append(" WHERE ").append(whereStr);

		return new SqlMeta(select.toString(), params, -1);
	}

	public static SqlMeta genInsertSqlMeta(ClassMetaInfo classMetaInfo) {

		if (classMetaInfo.tableAnnotation == null) {
			return null;
		}

		StringBuilder insert = new StringBuilder(512);
		Map<String, int[]> params = new HashMap<String, int[]>();
		int pos = 0;

		// Optimistic Lock
		String optLckCol = classMetaInfo.optimisticLockAnnotation == null ? null : classMetaInfo.optimisticLockAnnotation.column;

		StringBuilder colsStr = new StringBuilder(128);
		StringBuilder valsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
				valsStr.append(',');
			}
			colsStr.append(identifier(field));
			if (!field.equals(optLckCol)) {
				valsStr.append('?');
			} else {
				valsStr.append("(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
			}
			params.put(field, new int[] { ++pos });
		}
		insert.append("INSERT INTO ").append(identifier(classMetaInfo.tableName)).append('(').append(colsStr).append(") VALUES(").append(valsStr).append(')');
		return new SqlMeta(insert.toString(), Collections.unmodifiableMap(params), -1);
	}

	public static SqlMeta genUpdateSqlMeta(ClassMetaInfo classMetaInfo) {

		if (classMetaInfo.tableAnnotation == null) {
			return null;
		}

		StringBuilder update = new StringBuilder(512);
		Map<String, int[]> params = new HashMap<String, int[]>(classMetaInfo.fields.size());
		int pos = 0;

		// Optimistic Lock
		String optLckCol = classMetaInfo.optimisticLockAnnotation == null ? null : classMetaInfo.optimisticLockAnnotation.column;

		StringBuilder colsStr = new StringBuilder(128);
		LinkedHashSet<String> cols = new LinkedHashSet<String>();
		cols.addAll(classMetaInfo.columns);
		cols.removeAll(classMetaInfo.keys);
		for (String field : cols) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
			}
			if (!field.equals(optLckCol)) {
				colsStr.append(identifier(field)).append("=?");
			} else {
				colsStr.append(identifier(field)).append("=(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
			}
			params.put(field, new int[] { ++pos });
		}
		update.append("UPDATE ").append(identifier(classMetaInfo.tableName)).append(" SET ").append(colsStr);

		StringBuilder whereStr = new StringBuilder(128);
		for (String field : classMetaInfo.keys) {
			if (whereStr.length() > 0) {
				whereStr.append(" AND ");
			}
			whereStr.append(identifier(field)).append("=?");
			params.put(field, new int[] { ++pos });
		}

		if (optLckCol != null) {
			if (whereStr.length() > 0) {
				whereStr.append(" AND ");
			}
			whereStr.append(identifier(optLckCol)).append("=?");
			append(params, optLckCol, ++pos);
		}

		update.append(" WHERE ").append(whereStr);
		return new SqlMeta(update.toString(), params, -1);
	}

	public static SqlMeta genReplaceSqlMeta(ClassMetaInfo classMetaInfo) {

		if (classMetaInfo.tableAnnotation == null) {
			return null;
		}

		StringBuilder replace = new StringBuilder(512);
		Map<String, int[]> params = new HashMap<String, int[]>();
		int pos = 0;

		// Optimistic Lock
		String optLckCol = classMetaInfo.optimisticLockAnnotation == null ? null : classMetaInfo.optimisticLockAnnotation.column;

		StringBuilder colsStr = new StringBuilder(128);
		StringBuilder valsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
				valsStr.append(',');
			}
			colsStr.append(identifier(field));
			if (!field.equals(optLckCol)) {
				valsStr.append('?');
			} else {
				valsStr.append("(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
			}
			params.put(field, new int[] { ++pos });
		}
		replace.append("REPLACE INTO ").append(identifier(classMetaInfo.tableName)).append('(').append(colsStr).append(") VALUES(").append(valsStr).append(')');
		return new SqlMeta(replace.toString(), Collections.unmodifiableMap(params), -1);
	}

	public static SqlMeta genMergeSqlMeta(ClassMetaInfo classMetaInfo) {

		if (classMetaInfo.tableAnnotation == null) {
			return null;
		}

		StringBuilder insertOrUpdate = new StringBuilder(512);
		Map<String, int[]> params = new HashMap<String, int[]>();
		int pos = 0;

		// Optimistic Lock
		String optLckCol = classMetaInfo.optimisticLockAnnotation == null ? null : classMetaInfo.optimisticLockAnnotation.column;

		StringBuilder colsStr = new StringBuilder(128);
		StringBuilder valsStr = new StringBuilder(128);
		for (String field : classMetaInfo.columns) {
			if (colsStr.length() > 0) {
				colsStr.append(',');
				valsStr.append(',');
			}
			colsStr.append(identifier(field));
			if (!field.equals(optLckCol)) {
				valsStr.append('?');
			} else {
				valsStr.append("(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
			}
			params.put(field, new int[] { ++pos });
		}
		insertOrUpdate.append("INSERT INTO ").append(classMetaInfo.tableName).append('(').append(colsStr).append(") VALUES(").append(valsStr).append(')');

		StringBuilder updateStr = new StringBuilder(128);
		LinkedHashSet<String> cols = new LinkedHashSet<String>();
		cols.addAll(classMetaInfo.columns);
		cols.removeAll(classMetaInfo.keys);
		if (cols.size() > 0) {// FIXBUG: all is primary key
			for (String field : cols) {
				if (updateStr.length() > 0) {
					updateStr.append(',');
				}
				if (!field.equals(optLckCol)) {
					updateStr.append(identifier(field)).append("=IFNULL(?,").append(identifier(field)).append(")");
				} else {
					updateStr.append(identifier(field)).append("=(IFNULL(").append(identifier(field)).append(",IFNULL(?,0))+1)");
				}
				append(params, field, ++pos);
			}

		} else {
			for (String field : classMetaInfo.keys) {
				if (updateStr.length() > 0) {
					updateStr.append(',');
				}
				updateStr.append(identifier(field)).append("=").append(identifier(field));
			}
		}
		insertOrUpdate.append(" ON DUPLICATE KEY UPDATE ").append(updateStr);
		return new SqlMeta(insertOrUpdate.toString(), Collections.unmodifiableMap(params), -1);
	}

	public static SqlMeta genDeleteSqlMeta(ClassMetaInfo classMetaInfo) {

		if (classMetaInfo.tableAnnotation == null) {
			return null;
		}

		StringBuilder delete = new StringBuilder(512);
		Map<String, int[]> params = new HashMap<String, int[]>();
		int pos = 0;

		StringBuilder whereStr = new StringBuilder(128);
		for (String field : classMetaInfo.keys) {
			if (whereStr.length() > 0) {
				whereStr.append(" AND ");
			}
			whereStr.append(field).append("=?");
			params.put(field, new int[] { ++pos });
		}
		delete.append("DELETE FROM ").append(classMetaInfo.tableName).append(" WHERE ").append(whereStr);
		return new SqlMeta(delete.toString(), Collections.unmodifiableMap(params), -1);
	}

	static final char[] LIMIT = { 'L', 'I', 'M', 'I', 'T' };
	static final int DIFF = ('A' - 'a');

	public static int parseLimitIndexIfExist(CharSequence sql) {

		int mark, stack;
		for (int i = 0, n = sql.length(); i < n;) {
			if (Character.isJavaIdentifierPart(sql.charAt(i))) {
				mark = i++;
				while (i < n && Character.isJavaIdentifierPart(sql.charAt(i))) {
					i++;
				}
				IS_LIMIT_WORD: if (i - mark == LIMIT.length) {
					for (int j1 = 0, j2 = mark, diff; j2 < i; j1++, j2++) {
						diff = LIMIT[j1] - sql.charAt(j2);
						if (diff != 0 && diff != DIFF) {
							break IS_LIMIT_WORD;
						}
					}
					return mark;
				}
			} else {
				switch (sql.charAt(i)) {
				case '`':
					while ((++i) < n && sql.charAt(i) != '`') {
					}
					break;
				case '\'':
					while ((++i) < n) {
						if (sql.charAt(i) == '\'') {
							if (i + 1 < n && sql.charAt(i + 1) == '\'') {
								i++;
							} else {
								break;
							}
						}
					}
					break;
				case '(':
					stack = 1;
					while (stack > 0 && (++i) < n) {
						switch (sql.charAt(i)) {
						case '`':
							while ((++i) < n && sql.charAt(i) != '`') {
							}
							break;
						case '\'':
							while ((++i) < n) {
								if (sql.charAt(i) == '\'') {
									if (i + 1 < n && sql.charAt(i + 1) == '\'') {
										i++;
									} else {
										break;
									}
								}
							}
							break;
						case '(':
							stack++;
							break;
						case ')':
							stack--;
							break;
						}
					}
					break;
				}
				i++;
			}
		}
		return -1;
	}

	public static int[] parsePlaceHolderList(CharSequence sql) {

		LinkedList<Integer> vars = new LinkedList<Integer>();
		for (int i = 0, n = sql.length(); i < n;) {
			switch (sql.charAt(i)) {
			case '?':
				vars.add(i);
				i++;
				break;
			case '`':
				while ((++i) < n && sql.charAt(i) != '`') {
				}
				i++;
				break;
			case '\'':
				while ((++i) < n) {
					if (sql.charAt(i) == '\'') {
						if (i + 1 < n && sql.charAt(i + 1) == '\'') {
							i++;
						} else {
							break;
						}
					}
				}
				i++;
				break;
			default:
				i++;
			}
		}

		int[] ret = new int[vars.size() + 1];
		int idx = 1;
		for (Integer var : vars) {
			ret[idx++] = var.intValue();
		}
		return ret;
	}

	public static ParamHolder[] parseParamHolderList(String sql) {

		LinkedList<ParamHolder> vars = new LinkedList<ParamHolder>();
		int mark = 0;
		for (int i = 0, n = sql.length(); i < n;) {
			switch (sql.charAt(i)) {
			case ':':
				mark = i;
				while ((++i) < n && Character.isJavaIdentifierPart(sql.charAt(i))) {
				}
				vars.add(new ParamHolder(sql.substring(mark + 1, i), mark, i));
				i++;
				break;
			case '`':
				while ((++i) < n && sql.charAt(i) != '`') {
				}
				i++;
				break;
			case '\'':
				while ((++i) < n) {
					if (sql.charAt(i) == '\'') {
						if (i + 1 < n && sql.charAt(i + 1) == '\'') {
							i++;
						} else {
							break;
						}
					}
				}
				i++;
				break;
			default:
				i++;
			}
		}

		return vars.toArray(new ParamHolder[vars.size()]);
	}

	static class ParamHolder {
		final String name;
		final int start;
		final int end;

		public ParamHolder(String name, int start, int end) {
			this.name = name;
			this.start = start;
			this.end = end;
		}

	}

}

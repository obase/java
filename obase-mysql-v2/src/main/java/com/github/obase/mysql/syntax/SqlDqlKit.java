package com.github.obase.mysql.syntax;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.github.obase.mysql.core.Pstmt;
import com.github.obase.mysql.core.PstmtMeta;

public class SqlDqlKit extends SqlKit {

	public static final String SELECT = "SELECT";
	public static final String FROM = "FROM";
	public static final String WHERE = "WHERE";
	public static final String GROUP = "GROUP";
	public static final String HAVING = "HAVING";
	public static final String ORDER = "ORDER";
	public static final String LIMIT = "LIMIT";

	// 解析sql中的:name占位符
	public static List<Holder> parseHolder(String sql) {

		LinkedList<Holder> holders = new LinkedList<Holder>();
		int start = 0;
		int end = 0;
		int len = sql.length();
		while (end < len) {
			start = indexOf(Matcher.JavaIdentifier, sql, end, len);
			if (start == -1) {
				break;
			}
			end = indexOfNot(Matcher.JavaIdentifier, sql, start, len);
			if (end == -1) {
				end = len;
			}
			if (start > 0 && sql.charAt(start - 1) == ':') {
				holders.add(new Holder(sql.substring(start, end), start - 1, end));
			}
		}
		return holders;
	}

	// 解析sql为pstmt对象
	public static Pstmt parsePstmt(String sql) {

		List<Holder> holders = parseHolder(sql);
		Collections.reverse(holders);

		LinkedList<String> param = new LinkedList<String>();
		StringBuilder psql = new StringBuilder(sql);
		for (Holder h : holders) {
			param.addFirst(h.name);
			psql.replace(h.start, h.end, "?");
		}

		return new Pstmt(psql.toString(), param);
	}

	public static void parsePstmtLabel(ResultSet rs, final PstmtMeta meta) throws SQLException {
		synchronized (meta.psql) {
			if (meta.label == null) {
				Map<String, Integer> labels = new HashMap<String, Integer>();
				ResultSetMetaData rsmd = rs.getMetaData();
				for (int i = 1, n = rsmd.getColumnCount(); i <= n; i++) {
					labels.put(rsmd.getColumnLabel(i), i);
				}
				meta.label = labels;
			}

		}
	}

	public static void parsePstmtIndex(final PstmtMeta meta) {
		synchronized (meta.psql) {
			String sql = meta.psql;
			int start = 0;
			int end = 0;
			int len = sql.length();
			while (end < len) {
				start = indexOfIncludeParent(Matcher.JavaIdentifier, sql, end, len);
				if (start == -1) {
					break;
				}
				end = indexOfNot(Matcher.JavaIdentifier, sql, start, len);
				if (end == -1) {
					end = len;
				}
				if (sql.regionMatches(true, start, SELECT, 0, SELECT.length())) {
					meta.select = start;
				} else if (sql.regionMatches(true, start, FROM, 0, FROM.length())) {
					meta.from = start;
				} else if (sql.regionMatches(true, start, WHERE, 0, WHERE.length())) {
					meta.where = start;
				} else if (sql.regionMatches(true, start, GROUP, 0, GROUP.length())) {
					meta.group = start;
				} else if (sql.regionMatches(true, start, HAVING, 0, HAVING.length())) {
					meta.having = start;
				} else if (sql.regionMatches(true, start, ORDER, 0, ORDER.length())) {
					meta.order = start;
				} else if (sql.regionMatches(true, start, LIMIT, 0, LIMIT.length())) {
					meta.limit = start;
				}
			}
		}
	}

	public static void parsePstmtLimit(PstmtMeta meta) {
		if (meta.select == PstmtMeta.UNSET) {
			parsePstmtIndex(meta);
		}
		synchronized (meta.psql) {
			if (meta.limitPsql == null) {
				StringBuilder sb = new StringBuilder(meta.psql.length() + 128);
				if (meta.limit == PstmtMeta.UNSET) {
					sb.append(meta.psql).append(" LIMIT ?,?");
				} else {
					sb.append("SELECT * FROM (").append(meta.psql).append(") _ LIMIT ?,?");
				}
				meta.limitPsql = sb.toString();
			}
		}
	}

	// TODO: 约定SELECT子句没有参数,否则简单优化出错!
	public static void parsePstmtCount(PstmtMeta meta) {
		if (meta.select == PstmtMeta.UNSET) {
			parsePstmtIndex(meta);
		}
		synchronized (meta.psql) {
			if (meta.countPsql == null) {
				StringBuilder sb = new StringBuilder(meta.psql.length() + 256);
				if (meta.nop || meta.group != PstmtMeta.UNSET || meta.limit != PstmtMeta.UNSET) {
					sb.append("SELECT COUNT(*) FROM (");
					// 去掉order by子句, 避免浪费性能
					if (meta.order == PstmtMeta.UNSET) {
						sb.append(meta.psql);
					} else {
						sb.append(meta.psql, 0, meta.order);
						if (meta.limit != PstmtMeta.UNSET) {
							sb.append(meta.psql, meta.limit, meta.psql.length());
						}
					}
					sb.append(") _");
				} else {
					sb.append("SELECT COUNT(*) ");
					sb.append(meta.psql, meta.from, meta.order == PstmtMeta.UNSET ? meta.psql.length() : meta.order);
				}
				meta.countPsql = sb.toString();
			}
		}
	}

	public static String parsePstmtOrderLimit(PstmtMeta meta, String field, String direction) {
		if (meta.select == PstmtMeta.UNSET) {
			parsePstmtIndex(meta);
		}
		StringBuilder sb = new StringBuilder(meta.psql.length() + 128);
		sb.append(meta.psql, 0, meta.order == PstmtMeta.UNSET ? meta.psql.length() : meta.order);
		sb.append(" ORDER BY ").append(identifier(field)).append(" ").append(direction);
		sb.append(" LIMIT ?, ?");
		return sb.toString();
	}

}

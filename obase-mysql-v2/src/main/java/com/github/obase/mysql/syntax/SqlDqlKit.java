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
		if (meta.label == null) {
			Map<String, Integer> labels = new HashMap<String, Integer>();
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1, n = rsmd.getColumnCount(); i <= n; i++) {
				labels.put(rsmd.getColumnLabel(i), i);
			}
			meta.label = labels;
		}
	}

	public static void parsePstmtIndex(final PstmtMeta meta) {
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

	public static final String SELECT = "SELECT";
	public static final String FROM = "FROM";
	public static final String WHERE = "WHERE";
	public static final String GROUP = "GROUP";
	public static final String HAVING = "HAVING";
	public static final String ORDER = "ORDER";
	public static final String LIMIT = "LIMIT";

}

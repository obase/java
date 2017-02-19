package com.github.obase.mysql.jdbc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class SqlMeta {

	public final String psql;

	// Just for config query to process collection param
	public final int[] placeholderIndex;

	public final int limitIndex;

	public final Map<String, int[]> params;

	public Map<String, Integer> labels;

	public SqlMeta(String psql, Map<String, int[]> params, int limitIndex) {
		this(psql, params, null, limitIndex);
	}

	public SqlMeta(String psql, Map<String, int[]> params, int[] placeholderIndex, int limitIndex) {
		this.psql = psql;
		this.params = params == null ? Collections.<String, int[]> emptyMap() : params;
		this.placeholderIndex = placeholderIndex;
		this.limitIndex = limitIndex;
	}

	public String toString() {
		return toString(psql);
	}

	public String toString(String psql) {
		StringBuilder sb = new StringBuilder(1024);
		sb.append("{psql: ").append(psql).append(", ");
		sb.append("params: ");
		if (params != null && params.size() > 0) {
			sb.append('{');
			for (Map.Entry<String, int[]> entry : params.entrySet()) {
				sb.append(entry.getKey()).append("=").append(Arrays.toString(entry.getValue())).append(',');
			}
			sb.setCharAt(sb.length() - 1, '}');
		}
		sb.append('}');
		return sb.toString();
	}
}

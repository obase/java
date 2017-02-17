package com.github.obase.mysql.jdbc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.github.obase.mysql.jdbc.SqlMetaKit.ParamHolder;

public class SqlMeta {

	public final String psql;

	// Just for the config query, which used to process multiple values
	public final int[] configQueryParamIndex;

	public final int limitIndex;

	public final Map<String, int[]> params;

	public Map<String, Integer> labels;

	public SqlMeta(String psql, Map<String, int[]> params, int limitIndex) {
		this(psql, null, params, limitIndex);
	}

	public SqlMeta(String psql, ParamHolder[] holders, Map<String, int[]> params, int limitIndex) {
		this.psql = psql;
		if (holders != null) {
			// Just for the config query, which used to process multiple values
			this.configQueryParamIndex = new int[holders.length];
			for (int i = 0; i < holders.length; i++) {
				this.configQueryParamIndex[i] = holders[i].start;
			}
		} else {
			this.configQueryParamIndex = null;
		}
		this.params = params == null ? Collections.<String, int[]> emptyMap() : params;
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

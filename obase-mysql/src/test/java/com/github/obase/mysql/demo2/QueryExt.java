package com.github.obase.mysql.demo2;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.obase.MessageException;
import com.github.obase.kit.MapKit;
import com.github.obase.mysql.MysqlErrno;
import com.github.obase.mysql.jdbc.SqlMeta;

public class QueryExt {

	public static void main(String[] args) {

	}

	boolean showSql;
	static final Log logger = LogFactory.getLog(QueryExt.class);
	final Map<String, SqlMeta> configSqlMetaCache = new HashMap<String, SqlMeta>();

	private static String paramNewName(Object name, int idx) {
		return new StringBuilder(64).append(name).append("_$").append(idx).toString();
	}

	/** scan to recognize collection params and convert it. return collection params */
	private static Map<String, Integer> extcSqlMetaValues(Map<String, Object> values, Map<String, Object> valuesNew) {

		Map<String, Integer> collects = null;
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();
			if (val instanceof Collection) {
				Collection<?> c = (Collection<?>) val;
				int size = c.size();
				if (size == 1) {
					valuesNew.put(key, c.iterator().next());
				} else if (size == 0) {
					valuesNew.put(key, null);
				} else {
					int idx = 0;
					for (Iterator<?> iter = c.iterator(); iter.hasNext(); idx++) {
						String nameNew = paramNewName(iter.next(), idx);
						valuesNew.put(nameNew, iter.next());
					}
					if (collects == null) {
						collects = new HashMap<String, Integer>();
					}
					collects.put(key, size);
				}
			} else {
				valuesNew.put(key, val);
			}
		}
		return collects;
	}

	/**
	 * Index start from 1, the 0 element should be null;
	 */
	private static String[] rollSqlMetaParams(SqlMeta meta) {

		if (MapKit.isEmpty(meta.params)) {
			return null;
		}

		int size = 1; // SQL param index start from 1;
		for (int[] vals : meta.params.values()) {
			size += vals.length;
		}

		String[] params = new String[size];
		for (Map.Entry<String, int[]> entry : meta.params.entrySet()) {
			String key = entry.getKey();
			for (int i : entry.getValue()) {
				params[i] = key;
			}
		}

		return params;
	}

	/** reutrn last fragment of (...?...) from mark */
	private static int lastParamIndexOf(String psql, int mark) {
		while (mark >= 0) {
			switch (psql.charAt(mark)) {
			case '\'':
				while (--mark >= 0 && psql.charAt(mark) != '\'')
					;
				break;
			case '`':
				while (--mark >= 0 && psql.charAt(mark) != '`')
					;
				break;
			case '?':
				return mark;
			}
			mark--;
		}
		return mark;
	}

	public <T> void queryExtc(Connection conn, String queryId, Class<T> elemType, Map<String, Object> values) {

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		} else if (showSql) {
			logger.info("Sql for query: " + meta);
		}

		Map<String, Object> valuesNew = new HashMap<String, Object>();
		Map<String, Integer> collects = null;
		String[] params = null;
		if ((collects = extcSqlMetaValues(values, valuesNew)) != null && (params = rollSqlMetaParams(meta)) != null) {
			// execute with new meta and convert values
			LinkedList<String> paramsNew = new LinkedList<String>();
			StringBuilder psqlNew = new StringBuilder(meta.psql);
			String param;
			Integer size;
			int mark = meta.psql.length(); // start scan position
			// the param position start from 1, but not 0;
			for (int n = params.length - 1; n > 0; n--) {

//				mark =

				param = params[n];
				size = collects.get(param);
				if (size != null) {
					// process the params
					for (int idx = size - 1; idx >= 0; idx--) {
						paramsNew.addFirst(paramNewName(param, idx));
					}
					// process the psql: found the n param ? index;
				} else {
					paramsNew.addFirst(param);
				}
			}
		} else {
			// execute with old meta and convert values
		}

	}
}

package com.github.obase.mysql.demo2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.obase.MessageException;
import com.github.obase.kit.MapKit;
import com.github.obase.mysql.MysqlErrno;
import com.github.obase.mysql.jdbc.SqlMeta;
import com.github.obase.mysql.jdbc.SqlMetaKit;

public class QueryExt {

	public static void main(String[] args) {

	}

	boolean showSql;
	static final Log logger = LogFactory.getLog(QueryExt.class);
	final Map<String, SqlMeta> configSqlMetaCache = new HashMap<String, SqlMeta>();

	private static String newParamName(Object name, int idx) {
		return new StringBuilder(64).append(name).append("_$").append(idx).toString();
	}

	/** scan to recognize collection params and convert it. return collection params */
	private static Map<String, Object> extcCollectValues(Map<String, Object> values, Map<String, Integer> collects) {

		Map<String, Object> valuesNew = new HashMap<String, Object>();
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
						String nameNew = newParamName(iter.next(), idx);
						valuesNew.put(nameNew, iter.next());
					}
					collects.put(key, size);
				}
			} else {
				valuesNew.put(key, val);
			}
		}
		return valuesNew;
	}

	/** Position params, start from 1 */
	private static String[] origSqlMetaParams(SqlMeta meta) {
		if (MapKit.isEmpty(meta.params)) {
			return null;
		}
		int size = 1;
		for (int[] arr : meta.params.values()) {
			size += arr.length;
		}
		String[] params = new String[size];
		for (Map.Entry<String, int[]> entry : meta.params.entrySet()) {
			String key = entry.getKey();
			for (int pos : entry.getValue()) {
				params[pos] = key;
			}
		}
		return params;
	}

	/**
	 * syntax:((...?..))
	 * 
	 * @throws SQLException
	 */
	private static void extcCollectPsql(StringBuilder sqlb, int size, int idx, int lbound, int rbound) throws SQLException {
		int pidx = sqlb.lastIndexOf("((", idx);
		if (pidx < 0 || pidx <= lbound) {
			throw new SQLException("Syntax error for param near index " + idx + ": " + sqlb);
		}
		int sidx = sqlb.indexOf("))", idx);
		if (sidx < 0 || sidx >= rbound) {
			throw new SQLException("Syntax error for param near index " + idx + ": " + sqlb);
		}
		pidx += 1;
		sidx += 1;
		String str = " OR " + sqlb.substring(pidx, sidx);
		for (int i = 1; i < size; i++) {
			sqlb.insert(sidx, str);
		}
	}

	private static int[] toIntArray(List<Integer> list) {
		int[] ret = new int[list.size()];
		int idx = 0;
		for (Integer p : list) {
			ret[idx++] = p.intValue();
		}
		return ret;
	}

	private static Map<String, int[]> extcCollectParams(List<String> params) {

		Map<String, List<Integer>> temp = new HashMap<String, List<Integer>>();
		int pos = 0;
		for (String param : params) {
			List<Integer> poses = temp.get(param);
			if (poses == null) {
				poses = new LinkedList<Integer>();
			}
			poses.add(++pos);
		}

		Map<String, int[]> extcParams = new HashMap<String, int[]>(temp.size());
		for (Map.Entry<String, List<Integer>> entry : temp.entrySet()) {
			extcParams.put(entry.getKey(), toIntArray(entry.getValue()));
		}
		return extcParams;
	}

	private static SqlMeta extcCollectSqlMeta(SqlMeta meta, Map<String, Integer> collects) throws SQLException {

		String[] origParams = origSqlMetaParams(meta); // position start from 1

		LinkedList<String> tempParams = new LinkedList<String>();
		StringBuilder tempPsql = new StringBuilder(meta.psql);
		String param;
		Integer size;
		for (int limit = meta.psql.length(), last = origParams.length - 1, pos = last; pos > 0; pos--) {
			param = origParams[pos];
			size = collects.get(param);
			if (size != null) {
				for (int idx = size - 1; idx >= 0; idx--) {
					tempParams.addFirst(newParamName(param, idx));
				}
				extcCollectPsql(tempPsql, size, meta.psqlParamIndex[pos], pos > 1 ? meta.psqlParamIndex[pos - 1] : -1, pos < last ? meta.psqlParamIndex[pos + 1] : limit);
			} else {
				tempParams.addFirst(param);
			}
		}
		// optional: limitIndex
		return new SqlMeta(tempPsql.toString(), extcCollectParams(tempParams), SqlMetaKit.parsePlaceHolderList(tempPsql), SqlMetaKit.parseLimitIndexIfExist(tempPsql));
	}

	public <T> void queryExtc(Connection conn, String queryId, Class<T> elemType, Map<String, Object> values) throws SQLException {

		SqlMeta meta = configSqlMetaCache.get(queryId);
		if (meta == null) {
			throw new MessageException(MysqlErrno.SOURCE, MysqlErrno.SQL_CONFIG_NOT_FOUND, "Not found sql config: " + queryId);
		} else if (showSql) {
			logger.info("Sql for query: " + meta);
		}

		Map<String, Integer> collects = new HashMap<String, Integer>();
		Map<String, Object> extcValues = extcCollectValues(values, collects);
		if (MapKit.isNotEmpty(collects)) {
			meta = extcCollectSqlMeta(meta, collects);
		}

		// do as before ...
		// if ((collects = extcSqlMetaValues(values, valuesNew)) != null && (params = rollSqlMetaParams(meta)) != null) {
		// // execute with new meta and convert values
		// LinkedList<String> paramsNew = new LinkedList<String>();
		// StringBuilder psqlNew = new StringBuilder(meta.psql);
		// String param;
		// Integer size;
		// int mark = meta.psql.length(); // start scan position
		// // the param position start from 1, but not 0;
		// for (int n = params.length - 1; n > 0; n--) {
		//
		// // mark =
		//
		// param = params[n];
		// size = collects.get(param);
		// if (size != null) {
		// // process the params
		// for (int idx = size - 1; idx >= 0; idx--) {
		// paramsNew.addFirst(paramNewName(param, idx));
		// }
		// // process the psql: found the n param ? index;
		// } else {
		// paramsNew.addFirst(param);
		// }
		// }
		// } else {
		// // execute with old meta and convert values
		// }

	}

}

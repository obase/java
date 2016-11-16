package com.github.obase.mysql.jdbc;

import java.util.Arrays;
import java.util.Map;

class SqlKit {

	// 先去除`,再添加`
	public static String identifier(String val) {
		StringBuilder sb = new StringBuilder(val);
		for (int i = sb.length() - 1; i >= 0; i--) {
			if (sb.charAt(i) == '`') {
				sb.deleteCharAt(i);
			}
		}
		sb.insert(0, '`').append('`');
		return sb.toString();
	}

	static void append(Map<String, int[]> params, String field, int p) {
		int[] pos = params.get(field);
		if (pos == null) {
			pos = new int[1];
		} else {
			pos = Arrays.copyOf(pos, pos.length + 1);
		}
		pos[pos.length - 1] = p;
		params.put(field, pos);
	}

}

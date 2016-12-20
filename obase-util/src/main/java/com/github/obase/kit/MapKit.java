package com.github.obase.kit;

import java.util.HashMap;
import java.util.Map;

public class MapKit {

	public static boolean isEmpty(Map<?, ?> colls) {
		return colls == null || colls.size() == 0;
	}

	public static boolean isNotEmpty(Map<?, ?> colls) {
		return colls != null && colls.size() != 0;
	}

	public static Map<String, Object> as(Object... pairs) {
		if (pairs == null) {
			return null;
		}
		Map<String, Object> ret = new HashMap<String, Object>((pairs.length + 1) / 2);
		int k = 0, v = 1;
		while (v < pairs.length) {
			ret.put((String) pairs[k], pairs[v]);
			k += 2;
			v += 2;
		}
		if (k < pairs.length && v == pairs.length) { // last key
			ret.put((String) pairs[k], null);
		}
		return ret;
	}

	public static Map<String, String> as2(String... pairs) {
		if (pairs == null) {
			return null;
		}
		Map<String, String> ret = new HashMap<String, String>((pairs.length + 1) / 2);
		int k = 0, v = 1;
		while (v < pairs.length) {
			ret.put((String) pairs[k], pairs[v]);
			k += 2;
			v += 2;
		}
		if (k < pairs.length && v == pairs.length) { // last key
			ret.put((String) pairs[k], null);
		}
		return ret;
	}

}

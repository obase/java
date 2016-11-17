package com.github.obase.kit;

import java.util.Map;

public class MapKit {

	public static boolean isEmpty(Map<?, ?> colls) {
		return colls == null || colls.size() == 0;
	}

	public static boolean isNotEmpty(Map<?, ?> colls) {
		return colls != null && colls.size() != 0;
	}

}

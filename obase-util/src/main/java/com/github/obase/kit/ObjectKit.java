package com.github.obase.kit;

public class ObjectKit {

	public static boolean equals(Object obj1, Object obj2) {
		return (obj1 == obj2) || (obj1 != null && obj1.equals(obj2));
	}

	public static boolean notEquals(Object obj1, Object obj2) {
		return (obj1 != obj2) && (obj1 == null || !obj1.equals(obj2));
	}

	public static <T> T ifnull(T obj, T def) {
		return obj == null ? def : obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T coalesce(T... objs) {
		for (T obj : objs) {
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

}

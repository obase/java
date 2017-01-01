package com.github.obase;

import java.util.Collection;
import java.util.Map;

import com.github.obase.kit.CollectKit;
import com.github.obase.kit.MapKit;
import com.github.obase.kit.ObjectKit;
import com.github.obase.kit.StringKit;

public class Assert {

	public static void isTrue(boolean obj, int errno, String errmsg) {
		if (!obj) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void notTrue(boolean obj, int errno, String errmsg) {
		if (obj) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void isTrue(Boolean obj, int errno, String errmsg) {

		if (obj == null || !obj) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void notTrue(Boolean obj, int errno, String errmsg) {
		if (obj != null && obj) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void isNull(Object obj, int errno, String errmsg) {
		if (obj != null) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void notNull(Object obj, int errno, String errmsg) {
		if (obj == null) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void isEmpty(String obj, int errno, String errmsg) {
		if (StringKit.isNotEmpty(obj)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void notEmpty(String obj, int errno, String errmsg) {
		if (StringKit.isEmpty(obj)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void isBlank(String obj, int errno, String errmsg) {
		if (StringKit.isNotBlank(obj)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void notBlank(String obj, int errno, String errmsg) {
		if (StringKit.isBlank(obj)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static <T> void isEmpty(Collection<T> obj, int errno, String errmsg) {
		if (CollectKit.isNotEmpty(obj)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static <T> void notEmpty(Collection<T> obj, int errno, String errmsg) {
		if (CollectKit.isEmpty(obj)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static <K, T> void isEmpty(Map<K, T> obj, int errno, String errmsg) {
		if (MapKit.isNotEmpty(obj)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static <K, T> void notEmpty(Map<K, T> obj, int errno, String errmsg) {
		if (MapKit.isEmpty(obj)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void equals(Object obj1, Object obj2, int errno, String errmsg) {
		if (ObjectKit.notEquals(obj1, obj2)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void notEquals(Object obj1, Object obj2, int errno, String errmsg) {
		if (ObjectKit.equals(obj1, obj2)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void minLength(String val, int min, int errno, String errmsg) {
		if (val != null && val.length() < min) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void maxLength(String val, int max, int errno, String errmsg) {
		if (val != null && val.length() > max) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void alphanumeric(String val, int errno, String errmsg) {
		if (val != null) {
			char ch;
			for (int i = 0, n = val.length(); i < n; i++) {
				ch = val.charAt(i);
				if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_') {
					continue;
				}
				throw new MessageException(errno, errmsg);
			}
		}
	}

	public static <T> void isContains(Collection<T> c, Object v, int errno, String errmsg) {
		if (c == null || !c.contains(v)) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static <T> void notContains(Collection<T> c, Object v, int errno, String errmsg) {
		if (c != null && c.contains(v)) {
			throw new MessageException(errno, errmsg);
		}
	}
}

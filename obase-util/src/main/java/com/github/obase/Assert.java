package com.github.obase;

import java.util.Collection;
import java.util.Map;

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
		if (obj == null || obj.size() == 0) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static <T> void notEmpty(Collection<T> obj, int errno, String errmsg) {
		if (obj != null && obj.size() != 0) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static <K, T> void isEmpty(Map<K, T> obj, int errno, String errmsg) {
		if (obj == null || obj.size() == 0) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static <K, T> void notEmpty(Map<K, T> obj, int errno, String errmsg) {
		if (obj != null && obj.size() != 0) {
			throw new MessageException(errno, errmsg);
		}
	}
}

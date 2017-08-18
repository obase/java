package com.github.obase;

import java.util.Collection;
import java.util.Map;

public class Assert {

	public static void isTrue(boolean val, int errno, String errmsg) throws MessageException {
		if (!val) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void notTrue(boolean val, int errno, String errmsg) throws MessageException {
		if (val) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void isNull(Object val, int errno, String errmsg) throws MessageException {
		if (val == null) {
			return;
		}
		throw new MessageException(errno, errmsg);
	}

	public static void notNull(Object val, int errno, String errmsg) throws MessageException {
		if (val == null) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void isEmpty(String val, int errno, String errmsg) throws MessageException {
		if (val != null && val.length() != 0) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void notEmpty(String val, int errno, String errmsg) throws MessageException {
		if (val == null || val.length() == 0) {
			throw new MessageException(errno, errmsg);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void isEmpty(Collection val, int errno, String errmsg) throws MessageException {
		if (val != null && val.size() != 0) {
			throw new MessageException(errno, errmsg);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void notEmpty(Collection val, int errno, String errmsg) throws MessageException {
		if (val == null || val.size() == 0) {
			throw new MessageException(errno, errmsg);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void isEmpty(Map val, int errno, String errmsg) throws MessageException {
		if (val != null && val.size() != 0) {
			throw new MessageException(errno, errmsg);
		}
	}

	@SuppressWarnings("rawtypes")
	public static void notEmpty(Map val, int errno, String errmsg) throws MessageException {
		if (val == null || val.size() == 0) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void isEquals(Object val1, Object val2, int errno, String errmsg) throws MessageException {
		if (val1 != val2 && (val1 == null || !val1.equals(val2))) {
			throw new MessageException(errno, errmsg);
		}
	}

	public static void notEquals(Object val1, Object val2, int errno, String errmsg) throws MessageException {
		if (val1 == val2 || (val1 != null && val1.equals(val2))) {
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

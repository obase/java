package com.github.obase.kit;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StringKit {

	private StringKit() {
	}

	public static boolean isEmpty(String val) {
		return val == null || val.length() == 0;
	}

	public static boolean isNotEmpty(String val) {
		return val != null && val.length() != 0;
	}

	public static boolean isBlank(String val) {
		if (val == null) {
			return true;
		}
		int len = val.length();
		if (len == 0) {
			return true;
		}

		int st = 0;
		while ((st < len) && (val.charAt(st) <= ' ')) {
			st++;
		}
		while ((st < len) && (val.charAt(st) <= ' ')) {
			len--;
		}
		return st >= len;
	}

	public static boolean isNotBlank(String val) {
		if (val == null) {
			return false;
		}
		int len = val.length();
		if (len == 0) {
			return false;
		}

		int st = 0;
		while ((st < len) && (val.charAt(st) <= ' ')) {
			st++;
		}
		while ((st < len) && (val.charAt(st) <= ' ')) {
			len--;
		}
		return st < len;
	}

	static final String[] EMPTY_ARRAY = new String[0];
	static final List<String> EMPTY_LIST = Collections.emptyList();

	public static String[] split(String val, char sep, boolean ignoreEmptyTokens) {
		if (val.length() > 0) {
			List<String> list = split2List(val, sep, ignoreEmptyTokens);
			return list.toArray(new String[list.size()]);
		}
		return EMPTY_ARRAY;
	}

	public static List<String> split2List(String val, char sep, boolean ignoreEmptyTokens) {
		if (val.length() > 0) {
			LinkedList<String> list = new LinkedList<String>();
			int vlen = val.length(), mark = 0, pos = 0;
			while (pos < vlen && (pos = val.indexOf(sep, mark)) != -1) {
				if (mark < pos || !ignoreEmptyTokens) {
					list.add(val.substring(mark, pos));
				}
				pos = mark = pos + 1;
			}
			if (mark < vlen) {
				list.add(val.substring(mark));
			}
			return list;
		}
		return EMPTY_LIST;
	}

	public static String[] split(String val, String sep, boolean ignoreEmptyTokens) {
		if (val.length() > 0) {
			List<String> list = split2List(val, sep, ignoreEmptyTokens);
			return list.toArray(new String[list.size()]);
		}
		return EMPTY_ARRAY;
	}

	public static List<String> split2List(String val, String sep, boolean ignoreEmptyTokens) {
		if (val.length() > 0) {
			LinkedList<String> list = new LinkedList<String>();
			int vlen = val.length(), slen = sep.length(), mark = 0, pos = 0;
			while (pos < vlen && (pos = val.indexOf(sep, mark)) != -1) {
				if (mark < pos || !ignoreEmptyTokens) {
					list.add(val.substring(mark, pos));
				}
				pos = mark = pos + slen;
			}
			if (mark < vlen) {
				list.add(val.substring(mark));
			}
			return list;
		}
		return EMPTY_LIST;
	}

	public static String join(String[] val, char sep, boolean ignoreEmptyTokens) {
		StringBuilder sb = new StringBuilder(512);
		for (String v : val) {
			if (isEmpty(v) && ignoreEmptyTokens) {
				continue;
			}
			sb.append(v).append(sep);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String join(List<String> val, char sep, boolean ignoreEmptyTokens) {
		StringBuilder sb = new StringBuilder(512);
		for (String v : val) {
			if (isEmpty(v) && ignoreEmptyTokens) {
				continue;
			}
			sb.append(v).append(sep);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String join(String[] val, String sep, boolean ignoreEmptyTokens) {
		StringBuilder sb = new StringBuilder(512);
		for (String v : val) {
			if (isEmpty(v) && ignoreEmptyTokens) {
				continue;
			}
			sb.append(v).append(sep);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - sep.length());
		}
		return sb.toString();
	}

	public static String join(List<String> val, String sep, boolean ignoreEmptyTokens) {
		StringBuilder sb = new StringBuilder(512);
		for (String v : val) {
			if (isEmpty(v) && ignoreEmptyTokens) {
				continue;
			}
			sb.append(v).append(sep);
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - sep.length());
		}
		return sb.toString();
	}

	public static boolean equals(String val1, String val2) {
		return (val1 == val2) || (val1 != null && val1.equals(val2));
	}

	public static boolean equalsIgnoreCase(String val1, String val2) {
		return (val1 == val2) || (val1 != null && val1.equalsIgnoreCase(val2));
	}

}

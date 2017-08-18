package com.github.obase.kit;

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

	public static String[] split(String val, char sep, boolean ignoreEmptyTokens) {
		List<String> list = split2List(val, sep, ignoreEmptyTokens);
		return list == null ? null : list.toArray(new String[list.size()]);
	}

	public static List<String> split2List(String val, char sep, boolean ignoreEmptyTokens) {

		LinkedList<String> list = new LinkedList<String>();
		for (int vlen = val.length(), mark = 0, next = 0; true;) {
			next = val.indexOf(sep, mark);
			if (next != -1) {
				if (mark < next || !ignoreEmptyTokens) {
					list.add(val.substring(mark, next));
				}
				mark = next + 1;
			} else {
				if (mark < vlen || !ignoreEmptyTokens) {
					list.add(val.substring(mark));
				}
				break;
			}
		}

		return list;
	}

	public static String[] split(String val, String sep, boolean ignoreEmptyTokens) {
		List<String> list = split2List(val, sep, ignoreEmptyTokens);
		return list == null ? null : list.toArray(new String[list.size()]);
	}

	public static List<String> split2List(String val, String sep, boolean ignoreEmptyTokens) {

		LinkedList<String> list = new LinkedList<String>();
		for (int vlen = val.length(), slen = sep.length(), mark = 0, next = 0; true;) {
			next = val.indexOf(sep, mark);
			if (next == -1) {
				if (mark < vlen || !ignoreEmptyTokens) {
					list.add(val.substring(mark));
				}
				break;
			} else {
				if (mark < vlen || !ignoreEmptyTokens) {
					list.add(val.substring(mark, next));
				}
				mark = next + slen;
			}
		}

		return list;
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

	public static final char POLICY_DEFAULT_SEPACHAR = ':';
	public static final char POLICY_DEFAULT_WILDCHAR = '*';

	public static boolean policyMatches(String c, String a, String r) {

		int mark = r.indexOf(POLICY_DEFAULT_SEPACHAR);
		if (mark > 0) {
			if (c == null || c.length() != mark || !r.regionMatches(0, c, 0, mark)) {
				return false;
			}
		}
		mark++;

		int len = r.length();
		if (r.charAt(mark) == POLICY_DEFAULT_WILDCHAR) {
			// 前置
			mark++;
			len -= mark;
			return a != null && len <= a.length() && r.regionMatches(mark, a, a.length() - len, len);
		} else if (r.charAt(len - 1) == POLICY_DEFAULT_WILDCHAR) {
			// 后置
			len--;
			len -= mark;
			return a != null && len <= a.length() && r.regionMatches(mark, a, 0, len);
		} else {
			// 完全
			len -= mark;
			return a != null && len == a.length() && r.regionMatches(mark, a, 0, len);
		}

	}

}

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

	public static final char POLICY_DEFAULT_SEPACHAR = ':';
	public static final char POLICY_DEFAULT_WILDCHAR = '*';

	public static boolean policyMatches(String c, String a, String r) {

		int mark = r.indexOf(POLICY_DEFAULT_SEPACHAR);
		if (mark > 0) {
			int gpre = r.indexOf(POLICY_DEFAULT_WILDCHAR);
			if (gpre > mark) {
				gpre = -1;
			}
			int gsuf = r.lastIndexOf(POLICY_DEFAULT_WILDCHAR, mark);
			if (gsuf <= gpre) {
				gsuf = mark;
			}
			if (!r.regionMatches(gpre + 1, c, 0, gsuf - gpre - 1)) {
				return false;
			}
		}
		int apre = r.indexOf(POLICY_DEFAULT_WILDCHAR, mark + 1);
		if (apre < mark) {
			apre = mark;
		}
		int asuf = r.lastIndexOf(POLICY_DEFAULT_WILDCHAR);
		if (asuf <= apre) {
			asuf = r.length();
		}
		return r.regionMatches(apre + 1, a, 0, asuf - apre - 1);
	}

	public static class Join {

		final char sep;
		final String nil;
		final LinkedList<String> list = new LinkedList<String>();

		Join(char sep, String nil) {
			this.sep = sep;
			this.nil = nil;
		}

		public static Join one(char sep, String nil) {
			return new Join(sep, nil);
		}

		public Join join(String val) {
			list.add(val);
			return this;
		}

		public String toString() {
			if (list.isEmpty()) {
				return "";
			}
			int len = 0;
			for (String itm : list) {
				len += (itm == null) ? nil.length() : itm.length();
			}
			StringBuilder sb = new StringBuilder(len + list.size() + 16);
			for (String itm : list) {
				sb.append(itm == null ? nil : itm).append(sep);
			}
			sb.setLength(sb.length() - 1);
			return sb.toString();
		}
	}

	public static class Split {

		final char sep;
		final String nil;
		final String text;
		int mark;

		Split(char sep, String nil, String text) {
			this.sep = sep;
			this.nil = nil;
			this.text = text;
		}

		public static Split one(char sep, String nil, String text) {
			return new Split(sep, nil, text);
		}

		public String next() {
			if (mark < text.length()) {
				String val;
				int pos = text.indexOf(sep, mark);
				if (pos == -1) {
					val = text.substring(mark);
					mark = text.length();
				} else {
					val = text.substring(mark, pos);
					mark = pos + 1;
				}
				return StringKit.equals(val, nil) ? null : val;
			}
			return null;
		}
	}

}

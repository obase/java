package com.github.obase.mysql.syntax;

/**
 * 针对标准SQL语法处理相关逻辑.另外拓展Mysql的双引号与反引号.
 */
public class SqlKit {

	public static final char SPACE = '\u0020';

	// 去除SQL中的无用空白字符,智能处理单引,双引,反引
	public static String filterWhiteSpaces(String psql) {
		StringBuilder sb = new StringBuilder(psql.length());
		int start = 0;
		int end = 0;
		int len = psql.length();
		while (end < len) {
			if (sb.length() > 0) {
				sb.append(SPACE);
			}
			start = indexOfNot(Matcher.Whitespace, psql, end, len);
			if (start == -1) {
				break;
			}
			end = indexOf(Matcher.Whitespace, psql, start, end);
			if (end == -1) {
				end = len;
			}
			sb.append(psql, start, end); // 不要反复创建substring
		}
		return sb.toString();
	}

	// 从start开始查找下一个非空白字符
	public static int indexOfNot(Matcher m, String psql, int start, int len) {
		while (start < len) {
			if (!m.match(psql.charAt(start))) {
				return start;
			}
			start++;
		}
		return -1;
	}

	// 从start开始查找下一个空白字符,中间智能跳过单引,双引,反引等特殊SQL字符
	public static int indexOf(Matcher m, String psql, int start, int len) {
		while (start < len) {
			char ch = psql.charAt(start);
			if (m.match(ch)) {
				return start;
			}
			switch (ch) {
			case '\'':
				while (++start < len) {
					ch = psql.charAt(start);
					if (ch == '\'') {
						// 前向检查,还是单引,表示该字符是转义
						int nxt = start + 1;
						if (nxt < len && psql.charAt(nxt) == '\'') {
							start++;
						} else {
							break;
						}
					} else if (ch == '\\') {
						start++;
					}
				}
				break;
			case '\"':
				while (++start < len) {
					ch = psql.charAt(start);
					if (ch == '"') {
						break;
					} else if (ch == '\\') {
						start++;
					}
				}
				break;
			case '`':
				while (++start < len) {
					ch = psql.charAt(start);
					if (ch == '`') {
						// 前向检查,还是反引,表示该字符是转义
						int nxt = start + 1;
						if (nxt < len && psql.charAt(nxt) == '`') {
							start++;
						} else {
							break;
						}
					} else if (ch == '\\') {
						start++;
					}
				}
				break;
			}
			start++;
		}
		return -1;
	}

	// 从start开始查找下一个空白字符,中间智能跳过单引,双引,反引等特殊SQL字符
	public static int indexOfIncludeParent(Matcher m, String psql, int start, int len) {
		int left = 0;
		while (start < len) {
			char ch = psql.charAt(start);
			if (left == 0 && m.match(ch)) {
				return start;
			}
			switch (ch) {
			case '\'':
				while (++start < len) {
					ch = psql.charAt(start);
					if (ch == '\'') {
						// 前向检查,还是单引,表示该字符是转义
						int nxt = start + 1;
						if (nxt < len && psql.charAt(nxt) == '\'') {
							start++;
						} else {
							break;
						}
					} else if (ch == '\\') {
						start++;
					}
				}
				break;
			case '\"':
				while (++start < len) {
					ch = psql.charAt(start);
					if (ch == '"') {
						break;
					} else if (ch == '\\') {
						start++;
					}
				}
				break;
			case '`':
				while (++start < len) {
					ch = psql.charAt(start);
					if (ch == '`') {
						// 前向检查,还是反引,表示该字符是转义
						int nxt = start + 1;
						if (nxt < len && psql.charAt(nxt) == '`') {
							start++;
						} else {
							break;
						}
					} else if (ch == '\\') {
						start++;
					}
				}
				break;
			case '(':
				left++;
				break;
			case ')':
				left--;
				break;
			}
			start++;
		}
		return -1;
	}

}

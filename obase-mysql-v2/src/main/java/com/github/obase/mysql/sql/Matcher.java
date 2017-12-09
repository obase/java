package com.github.obase.mysql.sql;

public interface Matcher {

	boolean match(char ch);

	Matcher Whitespace = new Matcher() {
		public boolean match(char ch) {
			return Character.isWhitespace(ch);
		}
	};

	Matcher JavaIdentifier = new Matcher() {
		public boolean match(char ch) {
			return Character.isJavaIdentifierPart(ch);
		}
	};

}

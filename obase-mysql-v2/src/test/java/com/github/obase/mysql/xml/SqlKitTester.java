package com.github.obase.mysql.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.github.obase.mysql.syntax.SqlKit;

public class SqlKitTester {

	public static String readFile(String classpath) throws IOException {
		Reader reader = null;
		try {
			reader = new InputStreamReader(SqlKitTester.class.getResourceAsStream(classpath));
			StringBuilder sb = new StringBuilder();
			char[] cbuf = new char[1024];
			for (int len = 0; (len = reader.read(cbuf)) > 0;) {
				sb.append(cbuf, 0, len);
			}
			return sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public static void main(String[] args) throws IOException {
		String sql = readFile("/test.sql");
		// System.out.println(sql);
		sql = SqlKit.trimLine(sql);
		System.out.println(sql);
	}

}

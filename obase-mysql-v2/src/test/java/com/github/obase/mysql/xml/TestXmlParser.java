package com.github.obase.mysql.xml;

import java.io.InputStream;

public class TestXmlParser {

	public static void main(String[] args) throws Exception {

		ObaseMysqlParser parser = new ObaseMysqlParser();
		InputStream is = null;
		try {
			is = TestXmlParser.class.getResourceAsStream("/HostsRepos.xml");
			ObaseMysqlObject doc = parser.parse(is);
			System.out.println(doc);
		} finally {
			if (is != null) {
				is.close();
			}
		}

	}

}

package com.github.obase.mysql.xml;

import java.io.IOException;

import com.github.obase.mysql.PstmtMeta;
import com.github.obase.mysql.SPstmtMeta;
import com.github.obase.mysql.sql.Sql;
import com.github.obase.mysql.sql.SqlDqlKit;

public class SqlDqlKitTester extends SqlKitTester {

	public static void main(String[] args) throws IOException {
		String sql = readFile("/test.sql");
		Sql pstmt = SqlDqlKit.parseSql(sql);
		PstmtMeta meta = SPstmtMeta.getInstance(pstmt.content, pstmt.params);
		long start = System.currentTimeMillis();
		for (int i = 0; i < 10000 * 100; i++) {
			SqlDqlKit.parsePstmtIndex(meta);
		}
		long end = System.currentTimeMillis();
		System.out.println("used time:" + (end - start));

		System.out.println(meta);
		if (meta.select >= 0)
			System.out.println(meta.select + "==>" + meta.psql.substring(meta.select, meta.select + SqlDqlKit.SELECT.length()));
		if (meta.from >= 0)
			System.out.println(meta.from + "==>" + meta.psql.substring(meta.from, meta.from + SqlDqlKit.FROM.length()));
		if (meta.where >= 0)
			System.out.println(meta.where + "==>" + meta.psql.substring(meta.where, meta.where + SqlDqlKit.WHERE.length()));
		if (meta.group >= 0)
			System.out.println(meta.group + "==>" + meta.psql.substring(meta.group, meta.group + SqlDqlKit.GROUP.length()));
		if (meta.having >= 0)
			System.out.println(meta.having + "==>" + meta.psql.substring(meta.having, meta.having + SqlDqlKit.HAVING.length()));
		if (meta.order >= 0)
			System.out.println(meta.order + "==>" + meta.psql.substring(meta.order, meta.order + SqlDqlKit.ORDER.length()));
		if (meta.limit >= 0)
			System.out.println(meta.limit + "==>" + meta.psql.substring(meta.limit, meta.limit + SqlDqlKit.LIMIT.length()));
	}

}

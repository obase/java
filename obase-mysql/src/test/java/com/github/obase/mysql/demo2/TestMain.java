package com.github.obase.mysql.demo2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.obase.mysql.jdbc.SqlMeta;
import com.github.obase.mysql.jdbc.SqlMetaKit;

public class TestMain {

	public static void main(String[] args) {
		Map<String, int[]> params = new HashMap<String, int[]>();
		params.put("a", new int[] { 1, 2 });
		String psql = "select * from (select * from abc where :bbc=123 limit 100) where a=`:abc` and b=:bbc :bbc limit :abc";
		SqlMeta meta = SqlMetaKit.genConfigSqlMeta("test", psql);
		System.out.println(meta.psql);
		for (Map.Entry<String, int[]> entry : meta.params.entrySet()) {
			System.out.print(entry.getKey());
			System.out.print(": ");
			System.out.print(Arrays.toString(entry.getValue()));
			System.out.println();
		}
		System.out.println(Arrays.toString(meta.placeholderIndex));
		System.out.println(meta.limitIndex);
	}
}

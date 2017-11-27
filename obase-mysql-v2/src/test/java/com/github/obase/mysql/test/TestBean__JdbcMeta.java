package com.github.obase.mysql.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.github.obase.mysql.core.JdbcMeta;

public class TestBean__JdbcMeta extends JdbcMeta {

	public TestBean__JdbcMeta() {
		names.add("name");
		names.add("age");
		names.add("score");
		names.add("birth");
	}

	@Override
	public Object getValue(String name, Object obj) {
		TestBean b = (TestBean) obj;
		if ("name".equals(name)) {
			return b.getName();
		} else if ("age".equals(name)) {
			return b.getAge();
		} else if ("score".equals(name)) {
			return b.getScore();
		} else if ("birth".equals(name)) {
			return b.getBirth();
		}
		return null;
	}

	@Override
	public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
		TestBean b = (TestBean) obj;
		int[] p;
		if ((p = params.get("name")) != null) {

		} else if ((p = params.get("age")) != null) {

		} else if ((p = params.get("score")) != null) {

		} else if ((p = params.get("birth")) != null) {

		}
	}

	@Override
	public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
		// TODO Auto-generated method stub

	}

}

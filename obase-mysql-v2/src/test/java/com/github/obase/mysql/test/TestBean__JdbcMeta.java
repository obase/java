package com.github.obase.mysql.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.github.obase.mysql.core.JdbcMeta;

public class TestBean__JdbcMeta extends JdbcMeta {

	@Override
	public Object getValue(Object obj, String name) {
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
	public void setParam(PreparedStatement pstmt, int p, Object obj, String name) throws SQLException {
		TestBean b = (TestBean) obj;
		if ("name".equals(name)) {
			set_String(pstmt, p, b.getName());
			return;
		} else if ("age".equals(name)) {
			set_int(pstmt, p, b.getAge());
			return;
		} else if ("score".equals(name)) {
			set_double(pstmt, p, b.getScore());
			return;
		} else if ("birth".equals(name)) {
			set_JavaUtilDate(pstmt, p, b.getBirth());
			return;
		}
	}

	@Override
	public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
		TestBean t = new TestBean();
		Integer p = null;
		if ((p = types.get("name")) != null) {
			t.setName(get_String(rs, p));
		}
		if ((p = types.get("age")) != null) {
			t.setAge(get_int(rs, p));
		}
		if ((p = types.get("score")) != null) {
			t.setScore(get_double(rs, p));
		}
		if ((p = types.get("birth")) != null) {
			t.setBirth(get_JavaUtilDate(rs, p));
		}
		return t;
	}

	@Override
	public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
		TestBean t = (TestBean) obj;
		Integer p = null;
		if ((p = types.get("name")) != null) {
			t.setName(get_String(rs, p));
		}
		if ((p = types.get("age")) != null) {
			t.setAge(get_int(rs, p));
		}
		if ((p = types.get("score")) != null) {
			t.setScore(get_double(rs, p));
		}
		if ((p = types.get("birth")) != null) {
			t.setBirth(get_JavaUtilDate(rs, p));
		}
	}

}

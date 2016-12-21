package com.github.obase.mysql.demo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import com.github.obase.mysql.jdbc.JdbcAction;

public class Session_JdbcAction extends JdbcAction {

	@Override
	public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
		Session that = (Session) obj;
		int[] pos;

		if ((pos = params.get("user")) != null) {
			// 根据name的类型选择
			set_String(pstmt, pos, that.getUser());
		}

		if ((pos = params.get("roles")) != null) {
			// 根据category的类型选择
			set_Object(pstmt, pos, that.getRoles());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
		Session that = new Session();
		Integer pos;

		if ((pos = types.get("user")) != null) {
			// 根据name的类型选择
			that.setUser(get_String(rs, pos));
		}
		if ((pos = types.get("roles")) != null) {
			// 根据category的类型选择
			that.setRoles(get_Object(rs, pos, Set.class));
		}
		return that;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
		Session that = (Session) obj;
		Integer pos;

		if ((pos = types.get("user")) != null) {
			// 根据name的类型选择
			that.setUser(get_String(rs, pos));
		}
		if ((pos = types.get("roles")) != null) {
			// 根据category的类型选择
			that.setRoles(get_Object(rs, pos, Set.class));
		}
	}

}

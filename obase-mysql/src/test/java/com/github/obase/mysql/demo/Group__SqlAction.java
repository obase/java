package com.github.obase.mysql.demo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.github.obase.mysql.jdbc.JdbcAction;

public class Group__SqlAction extends JdbcAction {

	@Override
	public void setParam(PreparedStatement pstmt, Map<String, int[]> params, Object obj) throws SQLException {
		Group that = (Group) obj;
		int[] pos;

		if ((pos = params.get("id")) != null) {
			// 根据id的类型选择
			set_long(pstmt, pos, that.getId());
		}

		if ((pos = params.get("name")) != null) {
			// 根据name的类型选择
			set_String(pstmt, pos, that.getName3());
		}

		if ((pos = params.get("category")) != null) {
			// 根据category的类型选择
			set_Integer(pstmt, pos, that.getCategory());
		}
	}

	@Override
	public Object getResult(ResultSet rs, Map<String, Integer> types) throws SQLException {
		Group that = new Group();
		Integer pos;

		if ((pos = types.get("id")) != null) {
			// 根据id的类型选择
			that.setId(get_long(rs, pos));
		}
		if ((pos = types.get("name")) != null) {
			// 根据name的类型选择
			that.setName3(get_String(rs, pos));
		}
		if ((pos = types.get("category")) != null) {
			// 根据category的类型选择
			that.setCategory(get_int(rs, pos));
		}

		if ((pos = types.get("remark")) != null) {
			// 根据category的类型选择
			that.setRemark(get_String(rs, pos));
		}
		return that;
	}

	@Override
	public void getResult2(ResultSet rs, Map<String, Integer> types, Object obj) throws SQLException {
		Group that = (Group) obj;
		Integer pos;

		if ((pos = types.get("id")) != null) {
			// 根据id的类型选择
			that.setId(get_long(rs, pos));
		}
		if ((pos = types.get("name")) != null) {
			// 根据name的类型选择
			that.setName3(get_String(rs, pos));
		}
		if ((pos = types.get("category")) != null) {
			// 根据category的类型选择
			that.setCategory(get_int(rs, pos));
		}

		if ((pos = types.get("remark")) != null) {
			// 根据category的类型选择
			that.setRemark(get_String(rs, pos));
		}
	}

}

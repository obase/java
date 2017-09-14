package com.huya.dbms.service.sys;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.github.obase.Page;
import com.github.obase.kit.StringKit;
import com.huya.dbms.entity.Admin;
import com.huya.dbms.service.BaseService;

@Service
public class AdminService extends BaseService {

	public void page(Page<Admin> page, Map<String, Object> params) throws SQLException {
		mysqlClient.queryPage("Admin.fuzzySelect", Admin.class, page, params);
	}

	private void checkPermission(String user, Admin newOne, Admin oldOne) throws SQLException {
		if (StringKit.equals(user, newOne.getId())) {
			if (equals(newOne == null ? null : newOne.getLevel(), oldOne == null ? null : oldOne.getLevel())) {
				return;
			}
		}
		Admin self = mysqlClient.selectByKey(Admin.class, user);
		Assert.isTrue(self != null && self.getLevel() == 7, "权限不够!");
	}

	public void add(String user, Admin admin) throws SQLException {

		Assert.notNull(admin, "参数不能为空");
		Assert.hasText(admin.getId(), "主键不能为空!");
		Admin entity = mysqlClient.selectByKey(Admin.class, admin.getId());
		Assert.isNull(entity, "用户已存在!");
		checkPermission(user, admin, null);
		mysqlClient.insert(cinfo(admin, user));
	}

	public void update(String user, Admin admin) throws SQLException {
		Assert.notNull(admin, "参数不能为空");
		Assert.hasText(admin.getId(), "主键不能为空!");
		Admin entity = mysqlClient.selectByKey(Admin.class, admin.getId());
		Assert.notNull(entity, "用户不存在!");
		checkPermission(user, admin, entity);
		int sc = mysqlClient.update(minfo(admin, user));
		Assert.isTrue(sc > 0, "数据版本已过期!");
	}

	public void delete(String user, Map<String, Object> params) throws SQLException {
		String id = (String) params.get("id");
		Assert.hasText(id, "主键不能为空!");
		Admin entity = mysqlClient.selectByKey(Admin.class, id);
		Assert.notNull(entity, "用户不存在!");
		checkPermission(user, null, entity);
		mysqlClient.deleteByKey(Admin.class, id);
	}

}

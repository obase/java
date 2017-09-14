package com.huya.dbms.service.dsn;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.github.obase.Page;
import com.huya.dbms.entity.Instance;
import com.huya.dbms.entity.Proinstan;
import com.huya.dbms.entity.Project;
import com.huya.dbms.model.Database;
import com.huya.dbms.service.BaseService;

@Service
public class DatabaseService extends BaseService {

	public void page(Page<Database> page, Map<String, Object> params) throws SQLException {
		mysqlClient.queryPage("Database.fuzzySelect", Database.class, page, params);
	}

	public void add(String user, Database database) throws SQLException {
		Assert.notNull(database, "参数不能为空");
		Assert.notNull(database.getProjectid(), "项目不能为空!");
		Assert.notNull(database.getInstanceid(), "实例不能为空!");
		Assert.hasText(database.getDbname(), "数据库不能为空!");

		Project project = mysqlClient.selectByKey(Project.class, database.getProjectid());
		Assert.notNull(project, "项目不存在!");

		Instance instance = mysqlClient.selectByKey(Instance.class, database.getInstanceid());
		Assert.notNull(instance, "实例不存在!");

		Database entity = mysqlClient.queryFirst("Database.select", Database.class, database);
		Assert.isNull(entity, "数据库已存在!");

		database.setId(mysqlClient.insert(Proinstan.class, cinfo(database, user), Integer.class));
	}

	public void update(String user, Database database) throws SQLException {
		Assert.notNull(database, "参数不能为空");
		Assert.notNull(database.getProjectid(), "项目不能为空!");
		Assert.notNull(database.getInstanceid(), "实例不能为空!");
		Assert.hasText(database.getDbname(), "数据库不能为空!");

		Project project = mysqlClient.selectByKey(Project.class, database.getProjectid());
		Assert.notNull(project, "项目不存在!");

		Instance instance = mysqlClient.selectByKey(Instance.class, database.getInstanceid());
		Assert.notNull(instance, "实例不存在!");

		Proinstan entity = mysqlClient.selectByKey(Proinstan.class, database.getId());
		Assert.notNull(entity, "数据库不存在!");
		if (!equals(database.getProjectid(), entity.getProjectid()) || !equals(database.getInstanceid(), entity.getInstanceid()) || !equals(database.getDbname(), entity.getDbname())) {
			entity = mysqlClient.queryFirst("Database.select", Proinstan.class, database);
			Assert.isNull(entity, "数据库已存在!");
		}
		int sc = mysqlClient.update(Proinstan.class, minfo(database, user));
		Assert.isTrue(sc > 0, "数据版本已过期!");
	}

	public void delete(String user, Map<String, Object> params) throws SQLException {
		String id = (String) params.get("id");
		Assert.hasText(id, "主键不能为空!");
		Database entity = mysqlClient.selectByKey(Database.class, id);
		Assert.notNull(entity, "数据库不存在!");
		mysqlClient.deleteByKey(Database.class, id);
	}
}

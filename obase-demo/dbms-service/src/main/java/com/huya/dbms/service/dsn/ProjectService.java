package com.huya.dbms.service.dsn;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.github.obase.Page;
import com.github.obase.kit.StringKit;
import com.huya.dbms.entity.Project;
import com.huya.dbms.service.BaseService;

@Service
public class ProjectService extends BaseService {

	public void page(Page<Project> page, Map<String, Object> params) throws SQLException {
		mysqlClient.queryPage("Project.fuzzySelect", Project.class, page, params);
	}

	public void add(String user, Project project) throws SQLException {
		Assert.notNull(project, "参数不能为空");
		Assert.hasText(project.getProjecttype(), "项目类型不能为空!");
		Assert.hasText(project.getProjectcode(), "项目代号不能为空!");
		Assert.hasText(project.getName(), "项目名称不能为空!");
		Project entity = mysqlClient.queryFirst("Project.select", Project.class, project.getProjectcode());
		Assert.isNull(entity, "项目已存在!");
		Integer id = mysqlClient.insert(cinfo(project, user), Integer.class);
		project.setId(id);
	}

	public void update(String user, Project project) throws SQLException {
		Assert.notNull(project, "参数不能为空");
		Assert.notNull(project.getId(), "主键不能为空!");
		Assert.hasText(project.getProjecttype(), "项目类型不能为空!");
		Assert.hasText(project.getProjectcode(), "项目代号不能为空!");
		Assert.hasText(project.getName(), "项目名称不能为空!");
		Project entity = mysqlClient.selectByKey(Project.class, project.getId());
		Assert.notNull(entity, "项目不存在!");
		if (!StringKit.equals(project.getProjectcode(), entity.getProjectcode())) {
			entity = mysqlClient.queryFirst("Project.select", Project.class, project.getProjectcode());
			Assert.isNull(entity, "项目代号重复!");
		}
		int sc = mysqlClient.update(minfo(project, user));
		Assert.isTrue(sc > 0, "数据版本已过期!");
	}

	public void delete(String user, Map<String, Object> params) throws SQLException {
		String id = (String) params.get("id");
		Assert.hasText(id, "主键不能为空!");
		Project entity = mysqlClient.selectByKey(Project.class, id);
		Assert.notNull(entity, "项目不存在!");
		mysqlClient.deleteByKey(Project.class, id);
	}

}

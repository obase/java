package com.huya.dbms.service.dsn;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.github.obase.Page;
import com.github.obase.kit.StringKit;
import com.huya.dbms.entity.Instance;
import com.huya.dbms.entity.Instanext;
import com.huya.dbms.model.InstanceExt;
import com.huya.dbms.service.BaseService;

@Service
public class InstanceService extends BaseService {

	@SuppressWarnings("rawtypes")
	public void page(Page<Map> page, Map<String, Object> params) throws SQLException {
		mysqlClient.queryPage("Instance.fuzzySelect", Map.class, page, params);
	}

	public void add(String user, InstanceExt instance) throws SQLException {
		Assert.notNull(instance, "参数不能为空");
		Assert.hasText(instance.getName(), "实例名称不能为空!");
		Assert.hasText(instance.getType(), "实例类型不能为空!");
		Assert.hasText(instance.getIp(), "实例地址不能为空!");
		Assert.notNull(instance.getPort(), "实例端口不能为空!");
		Assert.notNull(instance.getIstest(), "请选择是否用于测试!");
		Assert.notNull(instance.getIsbackup(), "请选择是否用于备份!");
		Assert.notNull(instance.getRole(), "请选择主或从!");
		Assert.notNull(instance.getMdba(), "主DBA不能为空!");
		Assert.notNull(instance.getSdba(), "从DBA不能为空!");
		Assert.notNull(instance.getIdc(), "机房不能为空!");
		Assert.notNull(instance.getDep(), "部门不能为空!");
		InstanceExt entity = mysqlClient.queryFirst("Instance.select", InstanceExt.class, instance.getName());
		Assert.isNull(entity, "实例已存在!");

		instance = cinfo(instance, user);
		instance.setId(mysqlClient.insert(Instance.class, instance, Integer.class));
		mysqlClient.insert(Instanext.class, instance);
	}

	public void update(String user, InstanceExt instance) throws SQLException {
		Assert.notNull(instance, "参数不能为空");
		Assert.hasText(instance.getName(), "实例名称不能为空!");
		Assert.hasText(instance.getType(), "实例类型不能为空!");
		Assert.hasText(instance.getIp(), "实例地址不能为空!");
		Assert.notNull(instance.getPort(), "实例端口不能为空!");
		Assert.notNull(instance.getIstest(), "请选择是否用于测试!");
		Assert.notNull(instance.getIsbackup(), "请选择是否用于备份!");
		Assert.notNull(instance.getRole(), "请选择主或从!");
		Assert.notNull(instance.getMdba(), "主DBA不能为空!");
		Assert.notNull(instance.getSdba(), "从DBA不能为空!");
		Assert.notNull(instance.getIdc(), "机房不能为空!");
		Assert.notNull(instance.getDep(), "部门不能为空!");
		Instance entity = mysqlClient.selectByKey(Instance.class, instance.getId());
		Assert.notNull(entity, "项目不存在!");
		if (!StringKit.equals(instance.getName(), entity.getName())) {
			entity = mysqlClient.queryFirst("Instance.select", Instance.class, instance.getName());
			Assert.isNull(entity, "实例名称重复!");
		}

		minfo(instance, user);
		int sc = mysqlClient.update(Instance.class, instance);
		Assert.isTrue(sc > 0, "数据版本已过期!");
		sc = mysqlClient.update(Instanext.class, instance);
		Assert.isTrue(sc > 0, "数据版本已过期!");
	}

	public void delete(String user, Map<String, Object> params) throws SQLException {
		String id = (String) params.get("id");
		Assert.hasText(id, "主键不能为空!");
		Instance entity = mysqlClient.selectByKey(Instance.class, id);
		Assert.notNull(entity, "实例不存在!");
		mysqlClient.deleteByKey(Instance.class, id);
		mysqlClient.deleteByKey(Instanext.class, id);
	}

}

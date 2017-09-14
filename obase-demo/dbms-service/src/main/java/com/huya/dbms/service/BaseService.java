package com.huya.dbms.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.github.obase.jedis.JedisClient;
import com.github.obase.mysql.MysqlClient;
import com.huya.dbms.entity.Entity;

@Transactional(rollbackFor = Exception.class)
public abstract class BaseService {

	@Autowired
	protected MysqlClient mysqlClient;

	@Autowired
	protected JedisClient jedisClient;

	protected static <T extends Entity> T cinfo(T entity, String user) {
		return cinfo(entity, user, new Date());
	}

	protected static <T extends Entity> T cinfo(T entity, String user, Date now) {
		entity.setCreateBy(user);
		entity.setModifyBy(user);
		entity.setCreateTime(now);
		entity.setModifyTime(now);
		return entity;
	}

	protected static <T extends Entity> T minfo(T entity, String user) {
		return minfo(entity, user, new Date());
	}

	protected static <T extends Entity> T minfo(T entity, String user, Date now) {
		entity.setModifyBy(user);
		entity.setModifyTime(now);
		return entity;
	}

	public static boolean equals(Object obj1, Object obj2) {
		return obj1 == obj2 || (obj1 != null && obj1.equals(obj2));
	}

}

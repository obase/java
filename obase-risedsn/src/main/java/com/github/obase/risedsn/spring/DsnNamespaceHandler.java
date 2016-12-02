package com.github.obase.risedsn.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class DsnNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("comboPooledDataSource", new ComboPooledDataSourceBeanDefinitionParser());
		registerBeanDefinitionParser("basicDataSource", new BasicDataSourceBeanDefinitionParser());
		registerBeanDefinitionParser("tomcatDataSource", new TomcatDataSourceBeanDefinitionParser());
		registerBeanDefinitionParser("jedisPool", new JedisPoolBeanDefinitionParser());
		registerBeanDefinitionParser("shardedJedisPool", new ShardedJedisPoolBeanDefinitionParser());
		registerBeanDefinitionParser("redisClient", new RedisClientBeanDefinitionParser());
	}

}

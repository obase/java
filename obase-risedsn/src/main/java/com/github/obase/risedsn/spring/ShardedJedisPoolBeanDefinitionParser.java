package com.github.obase.risedsn.spring;

import static com.github.obase.risedsn.spring.DsnKit.getDwenvElement;
import static com.github.obase.risedsn.spring.DsnKit.getElementProperty;
import static com.github.obase.risedsn.spring.DsnKit.getRedisProperty;
import static com.github.obase.risedsn.spring.DsnKit.isEmpty;

import java.util.LinkedList;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

public class ShardedJedisPoolBeanDefinitionParser extends DsnBaseBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return ShardedJedisPool.class;
	}

	@Override
	protected void doParse(Element element, BeanDefinitionParserDelegate delegate, BeanDefinitionBuilder builder) {

		// redis数据源无主从,slave属性忽略
		Element dwenvElement = getDwenvElement(element, true);
		AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();

		Object poolConfig = null;
		LinkedList<JedisShardInfo> shards = null;
		Object algo = Hashing.MURMUR_HASH;
		Object keyTagPattern = null;

		NodeList nodeList = dwenvElement.getElementsByTagNameNS("*", "property");
		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element propElem = (Element) nodeList.item(i);
			String name = propElem.getAttribute("name");
			if ("poolConfig".equals(name)) {
				poolConfig = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else if ("shards".equals(name)) {
				shards = new LinkedList<JedisShardInfo>();
				NodeList jedisShardInfoNodeList = propElem.getElementsByTagNameNS("*", "jedisShardInfo");
				int size = jedisShardInfoNodeList.getLength();
				if (size > 0) {
					for (int j = 0; j < size; j++) {

						Element infoElem = (Element) jedisShardInfoNodeList.item(j);

						String dsn = getElementProperty(infoElem, "dsn", false);
						if (isEmpty(dsn)) {
							dsn = getElementProperty(dwenvElement, "dsn", false);
						}
						String key = getElementProperty(infoElem, "key", false);
						if (isEmpty(key)) {
							key = getElementProperty(dwenvElement, "key", false);
						}
						String[] props = getRedisProperty(dsn, key);

						String infoName = getElementProperty(infoElem, "name", false);
						String infoWeightStr = getElementProperty(infoElem, "weight", false);
						String infoConnectionTimeoutStr = getElementProperty(infoElem, "connectionTimeout", false);
						String infoSoTimeoutStr = getElementProperty(infoElem, "soTimeout", false);
						String infoDbStr = getElementProperty(infoElem, "db", false);

						int infoWeight = isEmpty(infoWeightStr) ? Sharded.DEFAULT_WEIGHT : Integer.parseInt(infoWeightStr);
						int infoConnectionTimeout = isEmpty(infoConnectionTimeoutStr) ? Protocol.DEFAULT_TIMEOUT : Integer.parseInt(infoConnectionTimeoutStr);
						int infoSoTimeout = isEmpty(infoSoTimeoutStr) ? Protocol.DEFAULT_TIMEOUT : Integer.parseInt(infoSoTimeoutStr);
						int infoDb = isEmpty(infoDbStr) ? Protocol.DEFAULT_DATABASE : Integer.parseInt(infoDbStr);

						shards.add(new com.github.obase.risedsn.extend.JedisShardInfo(infoName, infoWeight, props[0], Integer.parseInt(props[1]), props[2], infoConnectionTimeout, infoSoTimeout, infoDb));
					}
				}
			} else if ("algo".equals(name)) {
				algo = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else if ("keyTagPattern".equals(name)) {
				keyTagPattern = delegate.parsePropertyValue(propElem, beanDefinition, name);
			}
		}

		/*************************************************
		 * 基于构造函数: public ShardedJedisPool(final GenericObjectPoolConfig poolConfig, List<JedisShardInfo> shards, Hashing algo, Pattern keyTagPattern)
		 *************************************************/

		if (poolConfig == null) {
			poolConfig = new GenericObjectPoolConfig();
		}
		if (algo == null) {
			algo = Hashing.MURMUR_HASH;
		}
		builder.addConstructorArgValue(poolConfig);
		builder.addConstructorArgValue(shards);
		builder.addConstructorArgValue(algo);
		builder.addConstructorArgValue(keyTagPattern);

		builder.setDestroyMethodName("close");
	}
}

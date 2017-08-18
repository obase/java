package com.github.obase.risedsn.spring;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

public final class JedisPoolBeanDefinitionParser extends RedisBaseBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return JedisPool.class;
	}

	@Override
	protected void doParseInner(Element dwenvElement, BeanDefinitionParserDelegate delegate, BeanDefinitionBuilder builder, String host, String port, String pass) {

		AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();

		Object poolConfig = null;
		Object connectionTimeout = Protocol.DEFAULT_TIMEOUT;
		Object soTimeout = Protocol.DEFAULT_TIMEOUT;
		Object database = Protocol.DEFAULT_DATABASE;
		Object clientName = null;

		NodeList nodeList = dwenvElement.getElementsByTagNameNS("*", "property");
		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element propElem = (Element) nodeList.item(i);
			String name = propElem.getAttribute("name");

			if ("poolConfig".equals(name)) {
				poolConfig = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else if ("connectionTimeout".equals(name)) {
				connectionTimeout = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else if ("soTimeout".equals(name)) {
				soTimeout = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else if ("database".equals(name)) {
				database = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else if ("clientName".equals(name)) {
				clientName = delegate.parsePropertyValue(propElem, beanDefinition, name);
			}
		}

		/*************************************************
		 * 基于构造函数: JedisPool(final GenericObjectPoolConfig poolConfig, final String host, int port, final int connectionTimeout, final int soTimeout, final String password, final int database, final String clientName, final boolean ssl, final SSLSocketFactory sslSocketFactory, final SSLParameters sslParameters, final HostnameVerifier hostnameVerifier)
		 *************************************************/
		// ref, not value
		builder.addConstructorArgValue(poolConfig == null ? new GenericObjectPoolConfig() : poolConfig);
		builder.addConstructorArgValue(host);
		builder.addConstructorArgValue(port);
		builder.addConstructorArgValue(connectionTimeout);
		builder.addConstructorArgValue(soTimeout);
		builder.addConstructorArgValue(pass);
		builder.addConstructorArgValue(database);
		builder.addConstructorArgValue(clientName);
		builder.addConstructorArgValue(false);
		builder.addConstructorArgValue(null);
		builder.addConstructorArgValue(null);
		builder.addConstructorArgValue(null);

		builder.setDestroyMethodName("close");
	}

}

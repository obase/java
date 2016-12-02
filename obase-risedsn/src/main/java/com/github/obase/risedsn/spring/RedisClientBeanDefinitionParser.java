package com.github.obase.risedsn.spring;

import static com.github.obase.risedsn.spring.DsnKit.getValue;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;

public final class RedisClientBeanDefinitionParser extends RedisBaseBeanDefinitionParser {

	@Override
	protected Class<?> getBeanClass(Element element) {
		return RedisClient.class;
	}

	@Override
	protected void doParseInner(Element dwenvElement, BeanDefinitionParserDelegate delegate, BeanDefinitionBuilder builder, String host, String port, String pass) {

		AbstractBeanDefinition beanDefinition = builder.getRawBeanDefinition();

		StringBuilder uri = new StringBuilder(128);
		uri.append("redis://");
		if (pass != null) {
			uri.append(pass).append('@');
		}
		uri.append(host).append(':').append(port);

		Object clientResources = null;
		Object timeout = null;
		Object database = null;

		NodeList nodeList = dwenvElement.getElementsByTagNameNS("*", "property");
		for (int i = 0, n = nodeList.getLength(); i < n; i++) {
			Element propElem = (Element) nodeList.item(i);

			String name = propElem.getAttribute("name");

			if ("clientResources".equals(name)) {
				clientResources = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else if ("timeout".equals(name)) {
				timeout = delegate.parsePropertyValue(propElem, beanDefinition, name);
			} else if ("database".equals(name)) {
				database = delegate.parsePropertyValue(propElem, beanDefinition, name);
			}
		}

		if (timeout != null || database != null) {
			uri.append('?');
			if (timeout != null) {
				uri.append("timeout=").append(getValue(timeout)).append('&');
			}
			if (database != null) {
				uri.append("database=").append(getValue(database)).append('&');
			}
			uri.setLength(uri.length() - 1);
		}

		/*************************************************
		 * 基于构造函数: RedisClient create(ClientResources clientResources, RedisURI redisURI)
		 *************************************************/
		// ref, not value
		builder.setFactoryMethod("create");
		if (clientResources != null) {
			builder.addConstructorArgValue(clientResources);
		}
		builder.addConstructorArgValue(RedisURI.create(uri.toString()));
		builder.setDestroyMethodName("shutdown");
	}

}

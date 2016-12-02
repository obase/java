package com.github.obase.risedsn.spring;

import static com.github.obase.risedsn.spring.DsnKit.getDwenvElement;
import static com.github.obase.risedsn.spring.DsnKit.getElementProperty;
import static com.github.obase.risedsn.spring.DsnKit.getRedisProperty;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.w3c.dom.Element;

public abstract class RedisBaseBeanDefinitionParser extends DsnBaseBeanDefinitionParser {

	@Override
	protected final void doParse(Element element, BeanDefinitionParserDelegate delegate, BeanDefinitionBuilder builder) {

		/******************************** 读取参数 ***************************************/
		// redis数据源无主从,slave属性忽略
		Element dwenvElement = getDwenvElement(element, true);
		String dsn = getElementProperty(dwenvElement, "dsn", true);

		String key = getElementProperty(dwenvElement, "key", false);
		String[] props = getRedisProperty(dsn, key);
		String host = props[0];
		String port = props[1];
		String pass = props[2];

		/******************************** 注入属性 ***************************************/
		doParseInner(dwenvElement, delegate, builder, host, port, pass);
	}

	protected abstract void doParseInner(Element dwenvElement, BeanDefinitionParserDelegate delegate, BeanDefinitionBuilder builder, String host, String port, String pass);

}

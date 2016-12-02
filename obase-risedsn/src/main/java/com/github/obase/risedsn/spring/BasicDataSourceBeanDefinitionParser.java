package com.github.obase.risedsn.spring;

import org.apache.commons.dbcp2.BasicDataSource;
import org.w3c.dom.Element;

public class BasicDataSourceBeanDefinitionParser extends MysqlBaseBeanDefinitionParser {

	protected BasicDataSourceBeanDefinitionParser() {
		super("driverClassName", "url", "username", "password");
	}

	@Override
	protected Class<?> getBeanClass(Element element) {
		return BasicDataSource.class;
	}

}

package com.github.obase.risedsn.spring;

import org.w3c.dom.Element;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ComboPooledDataSourceBeanDefinitionParser extends MysqlBaseBeanDefinitionParser {

	protected ComboPooledDataSourceBeanDefinitionParser() {
		super("driverClass", "jdbcUrl", "user", "password");
	}

	@Override
	protected Class<?> getBeanClass(Element element) {
		return ComboPooledDataSource.class;
	}

}

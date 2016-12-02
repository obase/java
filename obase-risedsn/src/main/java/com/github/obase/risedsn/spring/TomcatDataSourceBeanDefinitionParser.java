package com.github.obase.risedsn.spring;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.w3c.dom.Element;

public class TomcatDataSourceBeanDefinitionParser extends MysqlBaseBeanDefinitionParser {

	protected TomcatDataSourceBeanDefinitionParser() {
		super("driverClassName", "url", "username", "password");
	}

	@Override
	protected Class<?> getBeanClass(Element element) {
		return DataSource.class;
	}

}

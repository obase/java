package com.github.obase.config.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class DsnNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("application-properties", new ApplicationPropertiesBeanDefinitionParser());
	}

}

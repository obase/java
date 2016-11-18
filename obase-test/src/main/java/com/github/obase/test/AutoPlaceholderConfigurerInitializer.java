package com.github.obase.test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

public class AutoPlaceholderConfigurerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		GenericBeanDefinition df = new GenericBeanDefinition();
		df.setBeanClass(PropertySourcesPlaceholderConfigurer.class);
		((DefaultListableBeanFactory) applicationContext.getBeanFactory())
				.registerBeanDefinition(PropertySourcesPlaceholderConfigurer.class.getCanonicalName(), df);
	}
}

package com.github.obase.config.spring;

import java.util.UUID;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.github.obase.config.ApplicationProperties;

public class ApplicationPropertiesBeanDefinitionParser extends AbstractBeanDefinitionParser {

	@Override
	protected final AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
		BeanDefinitionParserDelegate delegate = parserContext.getDelegate();
		String beanName = element.getAttribute(BeanDefinitionParserDelegate.ID_ATTRIBUTE);
		if (!StringUtils.hasText(beanName)) {
			beanName = UUID.randomUUID().toString();
			element.setAttribute(BeanDefinitionParserDelegate.ID_ATTRIBUTE, beanName);
		}

		String parentName = getParentName(element);
		if (parentName != null) {
			builder.getRawBeanDefinition().setParentName(parentName);
		}
		Class<?> beanClass = getBeanClass(element);
		builder.getRawBeanDefinition().setBeanClass(beanClass);
		builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
		if (parserContext.isNested()) {
			builder.setScope(parserContext.getContainingBeanDefinition().getScope());
		}
		if (parserContext.isDefaultLazyInit()) {
			builder.setLazyInit(true);
		}

		doParse(element, delegate, builder);
		return delegate.parseBeanDefinitionAttributes(element, beanName, null, builder.getBeanDefinition());
	}

	protected void doParse(Element element, BeanDefinitionParserDelegate delegate, BeanDefinitionBuilder builder) {

		String locations = element.getAttribute("locations");
		String ignoreSystemEnvironment = element.getAttribute("ignoreSystemEnvironment");
		String ignoreSystemProperties = element.getAttribute("ignoreSystemProperties");
		String ignorePropertyPlaceholder = element.getAttribute("ignorePropertyPlaceholder");
		String dataSourceRef = element.getAttribute("dataSourceRef");
		String query = element.getAttribute("query");
		String jedisPoolRef = element.getAttribute("jedisPoolRef");
		String hash = element.getAttribute("hash");
		String rules = element.getAttribute("rules");
		String timer = element.getAttribute("timer");
		String fatalIfError = element.getAttribute("fatalIfError");

		if (!StringUtils.isEmpty(locations)) {
			builder.addPropertyValue("locations", locations);
		}
		if (!StringUtils.isEmpty(ignoreSystemEnvironment)) {
			builder.addPropertyValue("ignoreSystemEnvironment", Boolean.parseBoolean(ignoreSystemEnvironment));
		}
		if (!StringUtils.isEmpty(ignoreSystemProperties)) {
			builder.addPropertyValue("ignoreSystemProperties", Boolean.parseBoolean(ignoreSystemProperties));
		}
		if (!StringUtils.isEmpty(ignorePropertyPlaceholder)) {
			builder.addPropertyValue("ignorePropertyPlaceholder", Boolean.parseBoolean(ignorePropertyPlaceholder));
		}
		if (!StringUtils.isEmpty(dataSourceRef)) {
			builder.addPropertyValue("dataSourceRef", dataSourceRef);
		}
		if (!StringUtils.isEmpty(query)) {
			builder.addPropertyValue("query", query);
		}
		if (!StringUtils.isEmpty(jedisPoolRef)) {
			builder.addPropertyValue("jedisPoolRef", jedisPoolRef);
		}
		if (!StringUtils.isEmpty(hash)) {
			builder.addPropertyValue("hash", hash);
		}
		if (!StringUtils.isEmpty(rules)) {
			builder.addPropertyValue("rules", rules);
		}
		if (!StringUtils.isEmpty(timer)) {
			builder.addPropertyValue("timer", Integer.parseInt(timer));
		}
		if (!StringUtils.isEmpty(fatalIfError)) {
			builder.addPropertyValue("fatalIfError", Boolean.parseBoolean(fatalIfError));
		}

		builder.setDestroyMethodName("destroy");
	}

	protected Class<?> getBeanClass(Element element) {
		return ApplicationProperties.class;
	}

	protected String getParentName(Element element) {
		return null;
	}

}

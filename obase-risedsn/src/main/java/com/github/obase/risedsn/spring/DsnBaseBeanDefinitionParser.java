package com.github.obase.risedsn.spring;

import java.util.UUID;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

public abstract class DsnBaseBeanDefinitionParser extends AbstractBeanDefinitionParser {

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

	protected abstract void doParse(Element element, BeanDefinitionParserDelegate delegate, BeanDefinitionBuilder builder);

	protected abstract Class<?> getBeanClass(Element element);

	protected String getParentName(Element element) {
		return null;
	}

}

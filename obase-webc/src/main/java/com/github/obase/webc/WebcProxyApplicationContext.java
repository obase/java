package com.github.obase.webc;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.ResolvableType;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Override the method relative to annotation
 *
 */
class WebcProxyApplicationContext extends XmlWebApplicationContext {

	@Override
	protected final String[] getDefaultConfigLocations() {
		return null; // ignore empty contextConfigLocation
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return new WebcDefaultListableBeanFactory(getInternalParentBeanFactory());
	}

	static class WebcDefaultListableBeanFactory extends DefaultListableBeanFactory {

		WebcDefaultListableBeanFactory(BeanFactory parentBeanFactory) {
			super(parentBeanFactory);
		}

		@Override
		public String[] getBeanNamesForType(ResolvableType type) {
			BeanFactory parent = super.getParentBeanFactory();
			if (parent instanceof ListableBeanFactory) {
				LinkedHashSet<String> ret = new LinkedHashSet<String>();
				Collections.addAll(ret, ((ListableBeanFactory) parent).getBeanNamesForType(type));
				Collections.addAll(ret, super.getBeanNamesForType(type));
				return ret.toArray(new String[ret.size()]);
			} else {
				return super.getBeanNamesForType(type);
			}
		}

		@Override
		public String[] getBeanNamesForType(Class<?> type) {
			BeanFactory parent = super.getParentBeanFactory();
			if (parent instanceof ListableBeanFactory) {
				LinkedHashSet<String> ret = new LinkedHashSet<String>();
				Collections.addAll(ret, ((ListableBeanFactory) parent).getBeanNamesForType(type));
				Collections.addAll(ret, super.getBeanNamesForType(type));
				return ret.toArray(new String[ret.size()]);
			} else {
				return super.getBeanNamesForType(type);
			}
		}

		@Override
		public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
			BeanFactory parent = super.getParentBeanFactory();
			if (parent instanceof ListableBeanFactory) {
				LinkedHashSet<String> ret = new LinkedHashSet<String>();
				Collections.addAll(ret, ((ListableBeanFactory) parent).getBeanNamesForType(type, includeNonSingletons, allowEagerInit));
				Collections.addAll(ret, super.getBeanNamesForType(type, includeNonSingletons, allowEagerInit));
				return ret.toArray(new String[ret.size()]);
			} else {
				return super.getBeanNamesForType(type);
			}
		}

		@Override
		public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
			BeanFactory parent = super.getParentBeanFactory();
			if (parent instanceof ListableBeanFactory) {
				LinkedHashMap<String, T> ret = new LinkedHashMap<String, T>();
				ret.putAll(((ListableBeanFactory) parent).getBeansOfType(type));
				ret.putAll(super.getBeansOfType(type));
				return ret;
			} else {
				return super.getBeansOfType(type);
			}

		}

		@Override
		public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
			BeanFactory parent = super.getParentBeanFactory();
			if (parent instanceof ListableBeanFactory) {
				LinkedHashMap<String, T> ret = new LinkedHashMap<String, T>();
				ret.putAll(((ListableBeanFactory) parent).getBeansOfType(type, includeNonSingletons, allowEagerInit));
				ret.putAll(super.getBeansOfType(type, includeNonSingletons, allowEagerInit));
				return ret;
			} else {
				return super.getBeansOfType(type);
			}
		}

		@Override
		public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {

			BeanFactory parent = super.getParentBeanFactory();
			if (parent instanceof ListableBeanFactory) {
				LinkedHashSet<String> ret = new LinkedHashSet<String>();
				Collections.addAll(ret, ((ListableBeanFactory) parent).getBeanNamesForAnnotation(annotationType));
				Collections.addAll(ret, super.getBeanNamesForAnnotation(annotationType));
				return ret.toArray(new String[ret.size()]);
			} else {
				return super.getBeanNamesForAnnotation(annotationType);
			}
		}

		@Override
		public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
			BeanFactory parent = super.getParentBeanFactory();
			if (parent instanceof ListableBeanFactory) {
				LinkedHashMap<String, Object> ret = new LinkedHashMap<String, Object>();
				ret.putAll(((ListableBeanFactory) parent).getBeansWithAnnotation(annotationType));
				ret.putAll(super.getBeansWithAnnotation(annotationType));
				return ret;
			} else {
				return super.getBeansWithAnnotation(annotationType);
			}
		}

	}
}

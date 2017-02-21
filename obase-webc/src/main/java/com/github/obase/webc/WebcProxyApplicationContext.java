package com.github.obase.webc;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.AbstractApplicationContext;
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
	public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {

		ApplicationContext parent = super.getParent();

		if (parent instanceof AbstractApplicationContext) {
			LinkedList<BeanFactoryPostProcessor> ret = new LinkedList<BeanFactoryPostProcessor>(super.getBeanFactoryPostProcessors());
			for (BeanFactoryPostProcessor item : ((AbstractApplicationContext) parent).getBeanFactoryPostProcessors()) {
				if (!ret.contains(item)) {
					ret.add(item);
				}
			}
			return ret;
		} else {
			return super.getBeanFactoryPostProcessors();
		}
	}

	@Override
	public Collection<ApplicationListener<?>> getApplicationListeners() {

		ApplicationContext parent = super.getParent();

		if (parent instanceof AbstractApplicationContext) {
			LinkedHashSet<ApplicationListener<?>> ret = new LinkedHashSet<ApplicationListener<?>>(super.getApplicationListeners());
			for (ApplicationListener<?> item : ((AbstractApplicationContext) parent).getApplicationListeners()) {
				if (!ret.contains(item)) {
					ret.add(item);
				}
			}
			return ret;
		} else {
			return super.getApplicationListeners();
		}
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type) {

		ApplicationContext parent = super.getParent();

		if (parent instanceof AbstractApplicationContext) {
			LinkedList<String> ret = new LinkedList<String>();
			Collections.addAll(ret, super.getBeanNamesForType(type));
			for (String item : ((AbstractApplicationContext) parent).getBeanNamesForType(type)) {
				if (!ret.contains(item)) {
					ret.add(item);
				}
			}
			return ret.toArray(new String[ret.size()]);
		} else {
			return super.getBeanNamesForType(type);
		}
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {

		ApplicationContext parent = super.getParent();

		if (parent instanceof AbstractApplicationContext) {
			LinkedList<String> ret = new LinkedList<String>();
			Collections.addAll(ret, super.getBeanNamesForType(type));
			for (String item : ((AbstractApplicationContext) parent).getBeanNamesForType(type)) {
				if (!ret.contains(item)) {
					ret.add(item);
				}
			}
			return ret.toArray(new String[ret.size()]);
		} else {
			return super.getBeanNamesForType(type);
		}
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {

		ApplicationContext parent = super.getParent();

		if (parent instanceof AbstractApplicationContext) {
			LinkedList<String> ret = new LinkedList<String>();
			Collections.addAll(ret, super.getBeanNamesForType(type));
			for (String item : ((AbstractApplicationContext) parent).getBeanNamesForType(type, includeNonSingletons, allowEagerInit)) {
				if (!ret.contains(item)) {
					ret.add(item);
				}
			}
			return ret.toArray(new String[ret.size()]);
		} else {
			return super.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
		}
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {

		ApplicationContext parent = super.getParent();

		if (parent instanceof AbstractApplicationContext) {
			LinkedHashMap<String, T> ret = new LinkedHashMap<String, T>(super.getBeansOfType(type));
			for (Map.Entry<String, T> entry : ((AbstractApplicationContext) parent).getBeansOfType(type).entrySet()) {
				if (!ret.containsKey(entry.getKey())) {
					ret.put(entry.getKey(), entry.getValue());
				}
			}
			return ret;
		} else {
			return super.getBeansOfType(type);
		}
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {

		ApplicationContext parent = super.getParent();

		if (parent instanceof AbstractApplicationContext) {
			LinkedHashMap<String, T> ret = new LinkedHashMap<String, T>(super.getBeansOfType(type, includeNonSingletons, allowEagerInit));
			for (Map.Entry<String, T> entry : ((AbstractApplicationContext) parent).getBeansOfType(type, includeNonSingletons, allowEagerInit).entrySet()) {
				if (!ret.containsKey(entry.getKey())) {
					ret.put(entry.getKey(), entry.getValue());
				}
			}
			return ret;
		} else {
			return super.getBeansOfType(type);
		}
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {

		ApplicationContext parent = super.getParent();

		if (parent instanceof AbstractApplicationContext) {
			LinkedList<String> ret = new LinkedList<String>();
			Collections.addAll(ret, getBeanNamesForAnnotation(annotationType));
			for (String item : ((AbstractApplicationContext) parent).getBeanNamesForAnnotation(annotationType)) {
				if (!ret.contains(item)) {
					ret.add(item);
				}
			}
			return ret.toArray(new String[ret.size()]);
		} else {
			return super.getBeanNamesForAnnotation(annotationType);
		}
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {

		ApplicationContext parent = super.getParent();

		if (parent instanceof AbstractApplicationContext) {
			LinkedHashMap<String, Object> ret = new LinkedHashMap<String, Object>(super.getBeansWithAnnotation(annotationType));
			for (Map.Entry<String, Object> entry : ((AbstractApplicationContext) parent).getBeansWithAnnotation(annotationType).entrySet()) {
				if (!ret.containsKey(entry.getKey())) {
					ret.put(entry.getKey(), entry.getValue());
				}
			}
			return ret;
		} else {
			return super.getBeansWithAnnotation(annotationType);
		}
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
		A ret = super.findAnnotationOnBean(beanName, annotationType);
		if (ret == null) {
			ApplicationContext parent = super.getParent();
			if (parent instanceof AbstractApplicationContext) {
				ret = ((AbstractApplicationContext) parent).findAnnotationOnBean(beanName, annotationType);
			}
		}
		return ret;
	}

}

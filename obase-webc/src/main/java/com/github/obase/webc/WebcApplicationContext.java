package com.github.obase.webc;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
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
class WebcApplicationContext extends XmlWebApplicationContext {

	@Override
	protected final String[] getDefaultConfigLocations() {
		return null; // ignore empty contextConfigLocation
	}

	@Override
	public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {

		if (super.getParent() == null) {
			return super.getBeanFactoryPostProcessors();
		}

		LinkedList<ApplicationContext> stack = new LinkedList<ApplicationContext>();
		ApplicationContext appctx;
		for (appctx = this; appctx != null; appctx = appctx.getParent()) {
			stack.addFirst(appctx);
		}

		LinkedHashSet<BeanFactoryPostProcessor> set = new LinkedHashSet<BeanFactoryPostProcessor>();
		while ((appctx = stack.removeFirst()) != null) {
			if (appctx instanceof AbstractApplicationContext) {
				set.addAll(((AbstractApplicationContext) appctx).getBeanFactoryPostProcessors());
			}
		}

		ArrayList<BeanFactoryPostProcessor> ret = new ArrayList<BeanFactoryPostProcessor>();
		ret.addAll(set);

		return ret;
	}

	@Override
	public Collection<ApplicationListener<?>> getApplicationListeners() {

		if (super.getParent() == null) {
			return super.getApplicationListeners();
		}

		LinkedList<ApplicationContext> stack = new LinkedList<ApplicationContext>();
		ApplicationContext appctx;
		for (appctx = this; appctx != null; appctx = appctx.getParent()) {
			stack.addFirst(appctx);
		}

		LinkedHashSet<ApplicationListener<?>> set = new LinkedHashSet<ApplicationListener<?>>();
		while ((appctx = stack.removeFirst()) != null) {
			if (appctx instanceof AbstractApplicationContext) {
				set.addAll(((AbstractApplicationContext) appctx).getApplicationListeners());
			}
		}

		return set;
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type) {

		if (super.getParent() == null) {
			return super.getBeanNamesForType(type);
		}

		LinkedList<ApplicationContext> stack = new LinkedList<ApplicationContext>();
		ApplicationContext appctx;
		for (appctx = this; appctx != null; appctx = appctx.getParent()) {
			stack.addFirst(appctx);
		}

		LinkedHashSet<String> set = new LinkedHashSet<String>();
		while ((appctx = stack.removeFirst()) != null) {
			if (appctx instanceof AbstractApplicationContext) {
				Collections.addAll(set, ((AbstractApplicationContext) appctx).getBeanNamesForType(type));
			}
		}

		return set.toArray(new String[set.size()]);
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {
		if (super.getParent() == null) {
			return super.getBeanNamesForType(type);
		}

		LinkedList<ApplicationContext> stack = new LinkedList<ApplicationContext>();
		ApplicationContext appctx;
		for (appctx = this; appctx != null; appctx = appctx.getParent()) {
			stack.addFirst(appctx);
		}

		LinkedHashSet<String> set = new LinkedHashSet<String>();
		while ((appctx = stack.removeFirst()) != null) {
			if (appctx instanceof AbstractApplicationContext) {
				Collections.addAll(set, ((AbstractApplicationContext) appctx).getBeanNamesForType(type));
			}
		}

		return set.toArray(new String[set.size()]);
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		if (super.getParent() == null) {
			return super.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
		}

		LinkedList<ApplicationContext> stack = new LinkedList<ApplicationContext>();
		ApplicationContext appctx;
		for (appctx = this; appctx != null; appctx = appctx.getParent()) {
			stack.addFirst(appctx);
		}

		LinkedHashSet<String> set = new LinkedHashSet<String>();
		while ((appctx = stack.removeFirst()) != null) {
			if (appctx instanceof AbstractApplicationContext) {
				Collections.addAll(set, ((AbstractApplicationContext) appctx).getBeanNamesForType(type, includeNonSingletons, allowEagerInit));
			}
		}

		return set.toArray(new String[set.size()]);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		if (super.getParent() == null) {
			return super.getBeansOfType(type);
		}

		LinkedList<ApplicationContext> stack = new LinkedList<ApplicationContext>();
		ApplicationContext appctx;
		for (appctx = this; appctx != null; appctx = appctx.getParent()) {
			stack.addFirst(appctx);
		}

		LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
		while ((appctx = stack.removeFirst()) != null) {
			if (appctx instanceof AbstractApplicationContext) {
				map.putAll(((AbstractApplicationContext) appctx).getBeansOfType(type));
			}
		}

		return map;
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
		if (super.getParent() == null) {
			return super.getBeansOfType(type, includeNonSingletons, allowEagerInit);
		}

		LinkedList<ApplicationContext> stack = new LinkedList<ApplicationContext>();
		ApplicationContext appctx;
		for (appctx = this; appctx != null; appctx = appctx.getParent()) {
			stack.addFirst(appctx);
		}

		LinkedHashMap<String, T> map = new LinkedHashMap<String, T>();
		while ((appctx = stack.removeFirst()) != null) {
			if (appctx instanceof AbstractApplicationContext) {
				map.putAll(((AbstractApplicationContext) appctx).getBeansOfType(type, includeNonSingletons, allowEagerInit));
			}
		}

		return map;
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		if (super.getParent() == null) {
			return super.getBeanNamesForAnnotation(annotationType);
		}

		LinkedList<ApplicationContext> stack = new LinkedList<ApplicationContext>();
		ApplicationContext appctx;
		for (appctx = this; appctx != null; appctx = appctx.getParent()) {
			stack.addFirst(appctx);
		}

		LinkedHashSet<String> set = new LinkedHashSet<String>();
		while ((appctx = stack.removeFirst()) != null) {
			if (appctx instanceof AbstractApplicationContext) {
				Collections.addAll(set, ((AbstractApplicationContext) appctx).getBeanNamesForAnnotation(annotationType));
			}
		}

		return set.toArray(new String[set.size()]);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
		if (super.getParent() == null) {
			return super.getBeansWithAnnotation(annotationType);
		}

		LinkedList<ApplicationContext> stack = new LinkedList<ApplicationContext>();
		ApplicationContext appctx;
		for (appctx = this; appctx != null; appctx = appctx.getParent()) {
			stack.addFirst(appctx);
		}

		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		while ((appctx = stack.removeFirst()) != null) {
			if (appctx instanceof AbstractApplicationContext) {
				map.putAll(((AbstractApplicationContext) appctx).getBeansWithAnnotation(annotationType));
			}
		}

		return map;
	}

}

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

		LinkedList<BeanFactoryPostProcessor> ret = new LinkedList<BeanFactoryPostProcessor>(super.getBeanFactoryPostProcessors());
		for (ApplicationContext ctx = this.getParent(); ctx != null; ctx = ctx.getParent()) {
			if (ctx instanceof AbstractApplicationContext) {
				for (BeanFactoryPostProcessor item : ((AbstractApplicationContext) ctx).getBeanFactoryPostProcessors()) {
					if (!ret.contains(item)) {
						ret.add(item);
					}
				}
			}
		}

		return ret;
	}

	@Override
	public Collection<ApplicationListener<?>> getApplicationListeners() {

		if (super.getParent() == null) {
			return super.getApplicationListeners();
		}

		LinkedHashSet<ApplicationListener<?>> ret = new LinkedHashSet<ApplicationListener<?>>(super.getApplicationListeners());
		for (ApplicationContext ctx = this.getParent(); ctx != null; ctx = ctx.getParent()) {
			if (ctx instanceof AbstractApplicationContext) {
				for (ApplicationListener<?> item : ((AbstractApplicationContext) ctx).getApplicationListeners()) {
					if (!ret.contains(item)) {
						ret.add(item);
					}
				}
			}
		}

		return ret;
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type) {

		if (super.getParent() == null) {
			return super.getBeanNamesForType(type);
		}

		LinkedHashSet<String> ret = new LinkedHashSet<String>();
		Collections.addAll(ret, super.getBeanNamesForType(type));
		for (ApplicationContext ctx = this.getParent(); ctx != null; ctx = ctx.getParent()) {
			if (ctx instanceof AbstractApplicationContext) {
				for (String item : ((AbstractApplicationContext) ctx).getBeanNamesForType(type)) {
					if (!ret.contains(item)) {
						ret.add(item);
					}
				}
			}
		}

		return ret.toArray(new String[ret.size()]);
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {
		if (super.getParent() == null) {
			return super.getBeanNamesForType(type);
		}

		LinkedHashSet<String> ret = new LinkedHashSet<String>();
		Collections.addAll(ret, super.getBeanNamesForType(type));
		for (ApplicationContext ctx = this.getParent(); ctx != null; ctx = ctx.getParent()) {
			if (ctx instanceof AbstractApplicationContext) {
				for (String item : ((AbstractApplicationContext) ctx).getBeanNamesForType(type)) {
					if (!ret.contains(item)) {
						ret.add(item);
					}
				}
			}
		}

		return ret.toArray(new String[ret.size()]);
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		if (super.getParent() == null) {
			return super.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
		}

		LinkedHashSet<String> ret = new LinkedHashSet<String>();
		Collections.addAll(ret, super.getBeanNamesForType(type, includeNonSingletons, allowEagerInit));
		for (ApplicationContext ctx = this.getParent(); ctx != null; ctx = ctx.getParent()) {
			if (ctx instanceof AbstractApplicationContext) {
				for (String item : ((AbstractApplicationContext) ctx).getBeanNamesForType(type, includeNonSingletons, allowEagerInit)) {
					if (!ret.contains(item)) {
						ret.add(item);
					}
				}
			}
		}

		return ret.toArray(new String[ret.size()]);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		if (super.getParent() == null) {
			return super.getBeansOfType(type);
		}

		LinkedHashMap<String, T> ret = new LinkedHashMap<String, T>(super.getBeansOfType(type));
		for (ApplicationContext ctx = this.getParent(); ctx != null; ctx = ctx.getParent()) {
			if (ctx instanceof AbstractApplicationContext) {
				for (Map.Entry<String, T> entry : ((AbstractApplicationContext) ctx).getBeansOfType(type).entrySet()) {
					if (!ret.containsKey(entry.getKey())) {
						ret.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		return ret;
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
		if (super.getParent() == null) {
			return super.getBeansOfType(type, includeNonSingletons, allowEagerInit);
		}

		LinkedHashMap<String, T> ret = new LinkedHashMap<String, T>(super.getBeansOfType(type, includeNonSingletons, allowEagerInit));
		for (ApplicationContext ctx = this.getParent(); ctx != null; ctx = ctx.getParent()) {
			if (ctx instanceof AbstractApplicationContext) {
				for (Map.Entry<String, T> entry : ((AbstractApplicationContext) ctx).getBeansOfType(type, includeNonSingletons, allowEagerInit).entrySet()) {
					if (!ret.containsKey(entry.getKey())) {
						ret.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		return ret;
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		if (super.getParent() == null) {
			return super.getBeanNamesForAnnotation(annotationType);
		}

		LinkedHashSet<String> ret = new LinkedHashSet<String>();
		Collections.addAll(ret, super.getBeanNamesForAnnotation(annotationType));
		for (ApplicationContext ctx = this.getParent(); ctx != null; ctx = ctx.getParent()) {
			if (ctx instanceof AbstractApplicationContext) {
				for (String item : ((AbstractApplicationContext) ctx).getBeanNamesForAnnotation(annotationType)) {
					if (!ret.contains(item)) {
						ret.add(item);
					}
				}
			}
		}

		return ret.toArray(new String[ret.size()]);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
		if (super.getParent() == null) {
			return super.getBeansWithAnnotation(annotationType);
		}

		LinkedHashMap<String, Object> ret = new LinkedHashMap<String, Object>(super.getBeansWithAnnotation(annotationType));
		for (ApplicationContext ctx = this.getParent(); ctx != null; ctx = ctx.getParent()) {
			if (ctx instanceof AbstractApplicationContext) {
				for (Map.Entry<String, Object> entry : ((AbstractApplicationContext) ctx).getBeansWithAnnotation(annotationType).entrySet()) {
					if (!ret.containsKey(entry.getKey())) {
						ret.put(entry.getKey(), entry.getValue());
					}
				}
			}
		}

		return ret;
	}

}

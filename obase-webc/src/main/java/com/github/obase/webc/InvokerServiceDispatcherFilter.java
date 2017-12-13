package com.github.obase.webc;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.github.obase.kit.ArrayKit;
import com.github.obase.webc.Webc.Util;
import com.github.obase.webc.annotation.InvokerService;
import com.github.obase.webc.support.BaseInvokerServiceProcessor;

public class InvokerServiceDispatcherFilter extends WebcFrameworkFilter {

	InvokerServiceProcessor processor;
	AsyncListener listener;
	long timeout;
	Map<String, InvokerServiceObject> rules;

	protected final void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
		wac.addBeanFactoryPostProcessor(new InvokerServiceExporterBeanFactoryPostProcessor());
	}

	@Override
	protected void initFrameworkFilter() throws ServletException {

		processor = Util.findWebcBean(applicationContext, InvokerServiceProcessor.class, params.controlProcessor);
		if (processor == null) {
			processor = new BaseInvokerServiceProcessor();
		}
		listener = Util.findWebcBean(applicationContext, AsyncListener.class, params.asyncListener);
		timeout = params.asyncTimeout;

		Set<InvokerServiceObject> objects = new HashSet<InvokerServiceObject>();
		Map<String, InvokerServiceObject> beans = applicationContext.getBeansOfType(InvokerServiceObject.class);
		if (beans.size() > 0) {
			for (Map.Entry<String, InvokerServiceObject> entry : beans.entrySet()) {
				if (entry.getKey().startsWith(Webc.INVOKER_SERVICE_PREFIX)) {
					InvokerServiceObject exporter = entry.getValue();
					if (!objects.add(exporter)) {
						throw new IllegalStateException("Duplicate invoker service : " + exporter.getServiceInterface());
					}
				}
			}
		}

		processor.setup(objects);

		// For performance: change lookupPath to servletPath and set to servletMethodHandlerMap
		rules = new HashMap<String, InvokerServiceObject>(objects.size());
		for (InvokerServiceObject object : objects) {
			rules.put(Kits.getServletPath(params.namespace, '/' + object.getServiceInterface().getCanonicalName(), null), object);
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		final InvokerServiceObject object = rules.get(request.getServletPath());

		if (object != null) {

			if (req.isAsyncSupported()) {

				final AsyncContext actx = request.startAsync(request, resp);
				if (listener != null) {
					actx.addListener(listener);
				}
				actx.setTimeout(timeout); // @Since 1.2.0: never timeout any more
				actx.start(new Runnable() {
					@Override
					public void run() {
						final HttpServletRequest request = (HttpServletRequest) actx.getRequest();
						final HttpServletResponse response = (HttpServletResponse) actx.getResponse();

						HttpServletRequest prerequest = null;
						try {
							prerequest = processor.process(request, response, object);
							if (prerequest != null) {
								object.handleRequest(prerequest, response);
							}
						} catch (Throwable t) {
							processor.error(request, response, t);
						} finally {
							if (request.isAsyncStarted()) {
								actx.complete();
							}
						}
					}
				});
			} else {

				final HttpServletResponse response = (HttpServletResponse) resp;
				HttpServletRequest prerequest = null;
				try {
					prerequest = processor.process(request, response, object);
					if (prerequest != null) {
						object.handleRequest(prerequest, response);
					}
				} catch (Throwable t) {
					processor.error(request, response, t);
				}
			}
			return;
		}
		chain.doFilter(req, resp);
	}

	static class InvokerServiceExporterBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
			System.out.println(beanFactory.getBeansOfType(Object.class).keySet());
			String[] beanNames = beanFactory.getBeanNamesForAnnotation(InvokerService.class);
			if (ArrayKit.isNotEmpty(beanNames)) {
				DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) beanFactory;
				for (String beanName : beanNames) {
					InvokerService invokerServiceAnnotation = beanFactory.findAnnotationOnBean(beanName, InvokerService.class);
					if (invokerServiceAnnotation != null) {
						Class<?> serviceInterface = invokerServiceAnnotation.service();
						GenericBeanDefinition bf = new GenericBeanDefinition();
						bf.setBeanClass(InvokerServiceObject.class);
						MutablePropertyValues props = bf.getPropertyValues();
						props.add("service", new RuntimeBeanReference(beanName));
						props.add("serviceInterface", serviceInterface);
						props.add("remark", invokerServiceAnnotation.remark());
						props.add("category", invokerServiceAnnotation.category());
						dbf.registerBeanDefinition(Webc.INVOKER_SERVICE_PREFIX + serviceInterface.getCanonicalName(), bf);
					}
				}
			}

		}

	}

}

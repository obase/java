package com.github.obase.webc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
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

import com.github.obase.webc.annotation.InvokerService;
import com.github.obase.webc.support.BaseInvokerServiceProcessor;
import com.github.obase.kit.ArrayKit;

@WebFilter(asyncSupported = true)
public class InvokerServiceDispatcherFilter extends WebcFrameworkFilter {

	InvokerServiceProcessor processor;
	Map<String, InvokerServiceObject> invokerServiceExporterMap;

	AsyncListener listener;
	long timeout;

	protected final void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
		wac.addBeanFactoryPostProcessor(new InvokerServiceExporterBeanFactoryPostProcessor());
	}

	@Override
	protected void initFrameworkFilter() throws ServletException {
		super.initFrameworkFilter();

		processor = Webc.Util.findBean(webApplicationContext, InvokerServiceProcessor.class, null);
		if (processor == null) {
			processor = new BaseInvokerServiceProcessor();
		}
		listener = processor.getAsyncListener();
		timeout = processor.getSessionTimeout();

		Map<String, InvokerServiceObject> rules = new HashMap<String, InvokerServiceObject>();
		Map<String, InvokerServiceObject> beans = webApplicationContext.getBeansOfType(InvokerServiceObject.class);
		if (beans.size() > 0) {
			for (Map.Entry<String, InvokerServiceObject> entry : beans.entrySet()) {
				if (entry.getKey().startsWith(Webc.INVOKER_SERVICE_PREFIX)) {
					InvokerServiceObject exporter = entry.getValue();
					String lookupPath = '/' + exporter.getServiceInterface().getCanonicalName();
					if (rules.put(lookupPath, exporter) != null) {
						throw new IllegalStateException("Duplicate invoker service lookup path : " + lookupPath);
					}
				}
			}
		}

		processor.initMappingRules(filterConfig, rules);

		// For performance: change lookupPath to servletPath and set to servletMethodHandlerMap
		invokerServiceExporterMap = new HashMap<String, InvokerServiceObject>(rules.size());
		for (Map.Entry<String, InvokerServiceObject> entry : rules.entrySet()) {
			invokerServiceExporterMap.put(Kits.getServletPath(config.namespace, entry.getKey(), null), entry.getValue());
		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		final HttpServletRequest request = (HttpServletRequest) req;
		final InvokerServiceObject object = invokerServiceExporterMap.get(request.getServletPath());

		if (object != null) {

			final AsyncContext actx = request.startAsync(request, resp);
			if (listener != null) {
				actx.addListener(listener);
			}
			actx.setTimeout(timeout);
			actx.start(new Runnable() {
				@Override
				public void run() {
					HttpServletRequest request = (HttpServletRequest) actx.getRequest();
					HttpServletResponse response = (HttpServletResponse) actx.getResponse();

					HttpServletRequest processedRequest = request;
					try {
						request = processor.preprocess(processedRequest, response, object);
						if (request != null) {
							object.handleRequest(request, response);
							processor.postprocess(request, response, null);
						}
					} catch (Throwable t) {
						processor.postprocess(request != null ? request : processedRequest, response, t);
					} finally {
						if (processedRequest.isAsyncStarted()) {
							actx.complete();
						}
					}
				}
			});
			return;
		}
		chain.doFilter(req, resp);
	}

	static class InvokerServiceExporterBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

			String[] beanNames = beanFactory.getBeanNamesForAnnotation(InvokerService.class);
			if (ArrayKit.isNotEmpty(beanNames)) {
				DefaultListableBeanFactory dbf = (DefaultListableBeanFactory) beanFactory;
				for (String beanName : beanNames) {
					InvokerService invokerServiceAnnotation = beanFactory.findAnnotationOnBean(beanName, InvokerService.class);
					if (invokerServiceAnnotation != null) {
						Class<?> serviceInterface = invokerServiceAnnotation.value();
						GenericBeanDefinition bf = new GenericBeanDefinition();
						bf.setBeanClass(InvokerServiceObject.class);
						MutablePropertyValues props = bf.getPropertyValues();
						props.add("service", new RuntimeBeanReference(beanName));
						props.add("serviceInterface", serviceInterface);
						props.add("annotation", invokerServiceAnnotation);
						dbf.registerBeanDefinition(Webc.INVOKER_SERVICE_PREFIX + serviceInterface.getCanonicalName(), bf);
					}
				}
			}

		}

	}

}

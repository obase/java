package com.github.obase.webc;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.util.WebUtils;

import com.github.obase.webc.Webc.DelegatedClassLoader;
import com.github.obase.webc.Webc.HttpMethod;
import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.webc.support.BaseServletMethodProcessor;

@SuppressWarnings("rawtypes")
public class ServletMethodDispatcherFilter extends WebcFrameworkFilter {

	CommonsMultipartResolver multipartResolver; // filter not support StandardServletMultipartResolver
	ServletMethodProcessor processor;
	Map<String, ServletMethodObject[]> servletMethodHandlerMap;
	DelegatedClassLoader delegateClassLoader;

	AsyncListener listener;
	long timeout;

	@Override
	protected final void initFrameworkFilter() throws ServletException {

		super.initFrameworkFilter();

		delegateClassLoader = new DelegatedClassLoader(webApplicationContext.getClassLoader());

		multipartResolver = Webc.Util.findBean(webApplicationContext, CommonsMultipartResolver.class, null);
		processor = Webc.Util.findBean(webApplicationContext, ServletMethodProcessor.class, null);
		if (processor == null) {
			processor = new BaseServletMethodProcessor();
		}
		listener = processor.getAsyncListener();
		timeout = processor.getSessionTimeout();

		// key is lookupPath
		Map<String, ServletMethodObject[]> rules = new HashMap<String, ServletMethodObject[]>();
		Map<String, Object> beans = webApplicationContext.getBeansWithAnnotation(Controller.class);
		if (beans.size() > 0) {
			Collection<ServletMethodFilter> servletFilters = webApplicationContext.getBeansOfType(ServletMethodFilter.class).values();
			for (Map.Entry<String, Object> beanEntry : beans.entrySet()) {

				Object bean = beanEntry.getValue();

				Class userClass = ClassUtils.getUserClass(bean);
				Controller controller = webApplicationContext.findAnnotationOnBean(beanEntry.getKey(), Controller.class);

				Map<Method, ServletMethod> annotations = new HashMap<Method, ServletMethod>();
				for (Method method : userClass.getMethods()) {
					ServletMethod annot = method.getAnnotation(ServletMethod.class);
					if (annot == null) {
						continue;
					}
					int mod = method.getModifiers();
					if (Modifier.isAbstract(mod)) {
						continue;
					}
					Class[] ptypes = method.getParameterTypes();
					if (ptypes.length != 2 || ptypes[0] != HttpServletRequest.class || ptypes[1] != HttpServletResponse.class) {
						continue;
					}
					annotations.put(method, annot);
				}

				if (annotations.size() > 0) {
					for (Map.Entry<Method, ServletMethod> entry : annotations.entrySet()) {

						Method method = entry.getKey();
						ServletMethod annotation = entry.getValue();

						String lookupPath = processor.parseLookupPath(userClass, controller, method, annotation);
						ServletMethodObject obj = newServletMethodObject(bean, method, annotation, findServletFilter(servletFilters, lookupPath, userClass, method, annotation));

						ServletMethodObject[] objs = rules.get(lookupPath);
						if (objs == null) {
							objs = new ServletMethodObject[HttpMethod.values().length];
							rules.put(lookupPath, objs);
						}

						HttpMethod[] methods = annotation.method();
						if (annotation.method().length == 0) {
							methods = HttpMethod.values();
						}
						for (HttpMethod m : methods) {
							if (objs[m.index] == null) {
								objs[m.index] = obj;
							} else {
								throw new IllegalStateException("Duplicate servlet method lookup path : " + m.name() + " " + lookupPath);
							}
						}
					}
				}
			}
		}
		processor.initMappingRules(filterConfig, rules);

		// For performance: change lookupPath to servletPath and set to servletMethodHandlerMap
		servletMethodHandlerMap = new HashMap<String, ServletMethodObject[]>(rules.size());
		for (Map.Entry<String, ServletMethodObject[]> entry : rules.entrySet()) {
			servletMethodHandlerMap.put(Kits.getServletPath(config.namespace, entry.getKey(), null), entry.getValue());
		}
	}

	@Override
	public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		req.setCharacterEncoding(Webc.CHARSET_NAME);
		resp.setCharacterEncoding(Webc.CHARSET_NAME);

		final HttpServletRequest request = (HttpServletRequest) req;

		final ServletMethodObject[] objects = servletMethodHandlerMap.get(request.getServletPath());
		if (objects != null) {
			final HttpMethod hmethod = HttpMethod.valueOf(request.getMethod());
			final ServletMethodObject object = objects[hmethod.index];
			if (object != null) {
				
				req.setAttribute(Webc.ATTR_NAMESPACE, namespace);
				
				final AsyncContext asyncContext = request.isAsyncStarted() ? request.getAsyncContext() : request.startAsync();
				if (listener != null) {
					asyncContext.addListener(listener);
				}
				asyncContext.setTimeout(timeout);
				asyncContext.start(new Runnable() {

					@Override
					public void run() {
						HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
						HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

						HttpServletRequest processedRequest = null;
						boolean multipartRequestParsed = false;
						try {
							processedRequest = checkMultipart(request);
							multipartRequestParsed = processedRequest != request;
							request = processor.preprocess(processedRequest, response, object);
							if (request != null) {
								object.service(request, response);
								processor.postprocess(request, response, null);
							}
						} catch (Throwable t) {
							processor.postprocess(request != null ? request : processedRequest, response, t);
						} finally {
							if (multipartRequestParsed) {
								cleanupMultipart(processedRequest);
							}
							if (asyncContext.getRequest().isAsyncStarted()) {
								asyncContext.complete();
							}
						}
					}
				});
				return;
			}
		}
		chain.doFilter(req, resp);
	}

	private ServletMethodFilter[] findServletFilter(Collection<ServletMethodFilter> servletFilters, String lookupPath, Class userClass, Method method, ServletMethod annotation) {
		if (servletFilters == null || servletFilters.size() == 0) {
			return ServletMethodFilter.EMPTY_ARRAY;
		}
		LinkedList<ServletMethodFilter> ret = new LinkedList<ServletMethodFilter>();
		for (ServletMethodFilter filter : servletFilters) {
			if (filter.matches(lookupPath, userClass, method, annotation)) {
				ret.add(filter);
			}
		}
		if (ret.isEmpty()) {
			return ServletMethodFilter.EMPTY_ARRAY;
		}
		Collections.sort(ret, ServletMethodFilter.OrderBy);
		return ret.toArray(new ServletMethodFilter[ret.size()]);
	}

	public ServletMethodObject newServletMethodObject(Object bean, Method method, ServletMethod annotation, ServletMethodFilter... filters) {

		String className = bean.getClass().getCanonicalName() + "__" + method.getName();
		Class<?> c;
		try {
			c = delegateClassLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			byte[] data = Webc.Util.dumpServletMethodObject(className, ClassUtils.getUserClass(bean), method);
			c = delegateClassLoader.defineClass(className, data);
		}
		try {
			return ((ServletMethodObject) c.newInstance()).init(bean, method.getName(), annotation, filters);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
		if (this.multipartResolver != null && this.multipartResolver.isMultipart(request)) {
			if (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
				logger.debug("Request is already a MultipartHttpServletRequest - if not in a forward, " + "this typically results from an additional MultipartFilter in web.xml");
			} else if (request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) instanceof MultipartException) {
				logger.debug("Multipart resolution failed for current request before - " + "skipping re-resolution for undisturbed error rendering");
			} else {
				return this.multipartResolver.resolveMultipart(request);
			}
		}
		// If not returned before: return original request.
		return request;
	}

	private void cleanupMultipart(HttpServletRequest request) {
		MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
		if (multipartRequest != null) {
			this.multipartResolver.cleanupMultipart(multipartRequest);
		}
	}
}

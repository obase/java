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

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;

import com.github.obase.WrappedException;
import com.github.obase.kit.ClassKit.DelegateClassLoader;
import com.github.obase.kit.StringKit;
import com.github.obase.webc.Webc.Util;
import com.github.obase.webc.annotation.ServletMethod;

@SuppressWarnings("rawtypes")
public class ServletMethodDispatcherFilter extends WebcFrameworkFilter {

	ServletMethodProcessor processor;
	AsyncListener listener;
	long timeout;
	Map<String, ServletMethodObject> rulesMap; // key is servletPath
	DelegateClassLoader delegateClassLoader; // using spring clsssLoader

	@Override
	protected final void initFrameworkFilter() throws ServletException {

		delegateClassLoader = new DelegateClassLoader(applicationContext.getClassLoader());
		processor = Util.findWebcBean(applicationContext, ServletMethodProcessor.class, params.controlProcessor);
		listener = Util.findWebcBean(applicationContext, AsyncListener.class, params.asyncListener);
		timeout = params.timeoutSecond * 1000;

		// key is lookupPath
		Map<String, ServletMethodObject> map = new HashMap<String, ServletMethodObject>();
		Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Controller.class);
		if (beans.size() > 0) {
			Collection<ServletMethodFilter> servletFilters = applicationContext.getBeansOfType(ServletMethodFilter.class).values();
			for (Map.Entry<String, Object> beanEntry : beans.entrySet()) {

				Object bean = beanEntry.getValue();

				Class userClass = ClassUtils.getUserClass(bean);
				Controller controller = applicationContext.findAnnotationOnBean(beanEntry.getKey(), Controller.class);

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
					// must match the signature
					if (ptypes.length != 2 || ptypes[0] != HttpServletRequest.class || ptypes[1] != HttpServletResponse.class) {
						continue;
					}
					annotations.put(method, annot);
				}

				if (annotations.size() > 0) {
					for (Map.Entry<Method, ServletMethod> entry : annotations.entrySet()) {

						Method method = entry.getKey();
						ServletMethod annotation = entry.getValue();

						String methodName = method.getName();

						String lookupPath = processor.lookup(controller, userClass, annotation, methodName);

						ServletMethodHandler obj = newServletMethodHandler(method, bean, findServletFilter(servletFilters, lookupPath, userClass, methodName, annotation));

						ServletMethodObject rules = map.get(lookupPath);
						if (rules == null) {
							rules = new ServletMethodObject(annotation, lookupPath);
							map.put(lookupPath, rules);
						}

						HttpMethod[] methods = annotation.method();
						if (annotation.method().length == 0) {
							methods = HttpMethod.values(); // default all
						}
						for (HttpMethod m : methods) {
							if (rules.handlers[m.ordinal()] == null) {
								rules.handlers[m.ordinal()] = obj;
							} else {
								throw new IllegalStateException("Duplicate lookupPath : " + m + " " + lookupPath);
							}
						}
					}
				}
			}
		}
		processor.setup(params, map);
		rulesMap = new HashMap<String, ServletMethodObject>(map.size());
		// For performance: change lookupPath to servletPath and set to servletMethodHandlerMap
		for (Map.Entry<String, ServletMethodObject> entry : map.entrySet()) {
			rulesMap.put(Kits.getServletPath(params.namespace, entry.getKey(), null), entry.getValue());
			if (StringKit.isEmpty(entry.getKey())) {
				rulesMap.put(Kits.getServletPath(params.namespace, "/", null), entry.getValue()); // FIXBUG: special for home page
			}
		}
	}

	@Override
	public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		req.setCharacterEncoding(Webc.CHARSET_NAME);
		resp.setCharacterEncoding(Webc.CHARSET_NAME);

		final HttpServletRequest request = (HttpServletRequest) req;

		final ServletMethodObject object = rulesMap.get(request.getServletPath());
		if (object != null) {
			final HttpMethod method = HttpMethod.valueOf(request.getMethod());
			final ServletMethodHandler handler = object.handlers[method.ordinal()];
			if (handler != null) {
				String lookupPath = object.lookupPath;

				req.setAttribute(Webc.ATTR_HTTP_METHOD, method);
				req.setAttribute(Webc.ATTR_LOOKUP_PATH, lookupPath);
				req.setAttribute(Webc.ATTR_NAMESPACE, params.namespace);

				// FIXBUG: if aysnc not support
				if (req.isAsyncSupported()) {
					final AsyncContext asyncContext = request.isAsyncStarted() ? request.getAsyncContext() : request.startAsync();
					if (listener != null) {
						asyncContext.addListener(listener);
					}
					asyncContext.setTimeout(timeout);
					asyncContext.start(new Runnable() {
						@Override
						public void run() {

							final HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
							final HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

							HttpServletRequest prerequest = null;
							try {
								prerequest = processor.process(request, response, object);
								if (prerequest != null) {
									handler.service(prerequest, response);
								}
							} catch (Throwable t) {
								processor.error(request, response, t);
							} finally {
								if (request.isAsyncStarted()) {
									asyncContext.complete();
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
							handler.service(prerequest, response);
						}
					} catch (Throwable t) {
						processor.error(request, response, t);
					}
				}
				return;
			}
		}

		chain.doFilter(req, resp);

	}

	private ServletMethodFilter[] findServletFilter(Collection<ServletMethodFilter> servletFilters, String lookupPath, Class userClass, String methodName, ServletMethod annotation) {
		if (servletFilters == null || servletFilters.size() == 0) {
			return ServletMethodFilter.EMPTY_ARRAY;
		}
		LinkedList<ServletMethodFilter> ret = new LinkedList<ServletMethodFilter>();
		for (ServletMethodFilter filter : servletFilters) {
			if (filter.matches(lookupPath, userClass, methodName, annotation)) {
				ret.add(filter);
			}
		}
		if (ret.isEmpty()) {
			return ServletMethodFilter.EMPTY_ARRAY;
		}
		Collections.sort(ret, ServletMethodFilter.OrderBy);
		return ret.toArray(new ServletMethodFilter[ret.size()]);
	}

	public ServletMethodHandler newServletMethodHandler(Method method, Object bean, ServletMethodFilter... filters) {

		String className = bean.getClass().getCanonicalName() + "__" + method.getName();
		Class<?> c;
		try {
			c = delegateClassLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			byte[] data = Webc.Util.dumpServletMethodObject(className, ClassUtils.getUserClass(bean), method);
			c = delegateClassLoader.defineClass(className, data);
		}
		try {
			return ((ServletMethodHandler) c.newInstance()).bind(bean, filters);
		} catch (Exception e) {
			throw new WrappedException(e);
		}
	}

}

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
import com.github.obase.kit.StringKit;
import com.github.obase.loader.DelegatedClassLoader;
import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.webc.support.BaseServletMethodProcessor;

@SuppressWarnings("rawtypes")
public class ServletMethodDispatcherFilter extends WebcFrameworkFilter {

	ServletMethodProcessor processor;
	AsyncListener listener;
	long timeout;
<<<<<<< HEAD
	Map<String, ServletMethodObject> rulesMap; // key is servletPath
=======
	Map<String, ServletMethodRules> rulesMap; // key is servletPath
>>>>>>> branch 'master' of git@github.com:obase/java.git
	DelegatedClassLoader delegateClassLoader; // using spring clsssLoader

	@Override
	protected final void initFrameworkFilter() throws ServletException {

		delegateClassLoader = new DelegatedClassLoader(applicationContext.getClassLoader());

		if (params.controlProcessor != null) {
			processor = (ServletMethodProcessor) applicationContext.getBean(params.controlProcessor);
		} else {// using default if not set
			processor = new BaseServletMethodProcessor();
		}

		if (params.asyncListener != null) {
			listener = (AsyncListener) applicationContext.getBean(params.asyncListener);
		}

		timeout = params.timeoutSecond * 1000;

		// key is lookupPath
<<<<<<< HEAD
		Map<String, ServletMethodObject> map = new HashMap<String, ServletMethodObject>();
=======
		Map<String, ServletMethodRules> map = new HashMap<String, ServletMethodRules>();
>>>>>>> branch 'master' of git@github.com:obase/java.git
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
<<<<<<< HEAD
						ServletMethodHandler obj = newServletMethodHandler(method, bean, findServletFilter(servletFilters, lookupPath, userClass, methodName, annotation));

						ServletMethodObject rules = map.get(lookupPath);
						if (rules == null) {
							rules = new ServletMethodObject(annotation, lookupPath);
=======
						ServletMethodObject obj = newServletMethodObject(method, bean, findServletFilter(servletFilters, lookupPath, userClass, methodName, annotation));

						ServletMethodRules rules = map.get(lookupPath);
						if (rules == null) {
							rules = new ServletMethodRules(lookupPath);
>>>>>>> branch 'master' of git@github.com:obase/java.git
							map.put(lookupPath, rules);
						}

						HttpMethod[] methods = annotation.method();
						if (annotation.method().length == 0) {
							methods = HttpMethod.values(); // default all
						}
						for (HttpMethod m : methods) {
<<<<<<< HEAD
							if (rules.handlers[m.ordinal()] == null) {
								rules.handlers[m.ordinal()] = obj;
=======
							if (rules.annotations[m.ordinal()] == null) {
								rules.annotations[m.ordinal()] = annotation;
								rules.objects[m.ordinal()] = obj;
>>>>>>> branch 'master' of git@github.com:obase/java.git
							} else {
								throw new IllegalStateException("Duplicate lookupPath : " + m + " " + lookupPath);
							}
						}
					}
				}
			}
		}
		processor.setup(params, map);
<<<<<<< HEAD
		rulesMap = new HashMap<String, ServletMethodObject>(map.size());
=======
		rulesMap = new HashMap<String, ServletMethodRules>(map.size());
>>>>>>> branch 'master' of git@github.com:obase/java.git
		// For performance: change lookupPath to servletPath and set to servletMethodHandlerMap
<<<<<<< HEAD
		for (Map.Entry<String, ServletMethodObject> entry : map.entrySet()) {
=======
		for (Map.Entry<String, ServletMethodRules> entry : map.entrySet()) {
>>>>>>> branch 'master' of git@github.com:obase/java.git
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

<<<<<<< HEAD
		final ServletMethodObject object = rulesMap.get(request.getServletPath());
		if (object != null) {
			final HttpMethod method = HttpMethod.valueOf(request.getMethod());
			final ServletMethodHandler handler = object.handlers[method.ordinal()];
			if (handler != null) {
=======
		ServletMethodRules rules = rulesMap.get(request.getServletPath());
		if (rules != null) {
			final HttpMethod method = HttpMethod.valueOf(request.getMethod());
			final ServletMethodObject object = rules.objects[method.ordinal()];
			if (object != null) {
>>>>>>> branch 'master' of git@github.com:obase/java.git

<<<<<<< HEAD
				String lookupPath = object.lookupPath;
=======
				String lookupPath = rules.lookupPath;
>>>>>>> branch 'master' of git@github.com:obase/java.git

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
<<<<<<< HEAD
								prerequest = processor.process(request, response, object);
								if (prerequest != null) {
									handler.service(prerequest, response);
=======
								prerequest = processor.process(request, response);
								if (prerequest != null) {
									object.service(prerequest, response);
>>>>>>> branch 'master' of git@github.com:obase/java.git
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
					HttpServletRequest prerequest = null, processedRequest = null;
					try {
<<<<<<< HEAD
						prerequest = processor.process(request, response, object);
						if (prerequest != null) {
							handler.service(processedRequest, response);
=======
						prerequest = processor.process(request, response);
						if (prerequest != null) {
							object.service(processedRequest, response);
>>>>>>> branch 'master' of git@github.com:obase/java.git
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

<<<<<<< HEAD
	public ServletMethodHandler newServletMethodHandler(Method method, Object bean, ServletMethodFilter... filters) {
=======
	public ServletMethodObject newServletMethodObject(Method method, Object bean, ServletMethodFilter... filters) {
>>>>>>> branch 'master' of git@github.com:obase/java.git

		String className = bean.getClass().getCanonicalName() + "__" + method.getName();
		Class<?> c;
		try {
			c = delegateClassLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			byte[] data = Webc.Util.dumpServletMethodObject(className, ClassUtils.getUserClass(bean), method);
			c = delegateClassLoader.defineClass(className, data);
		}
		try {
<<<<<<< HEAD
			return ((ServletMethodHandler) c.newInstance()).bind(bean, filters);
=======
			return ((ServletMethodObject) c.newInstance()).bind(bean, filters);
>>>>>>> branch 'master' of git@github.com:obase/java.git
		} catch (Exception e) {
			throw new WrappedException(e);
		}
	}

}

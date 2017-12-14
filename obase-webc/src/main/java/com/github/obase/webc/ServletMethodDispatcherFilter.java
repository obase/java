package com.github.obase.webc;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;

import com.github.obase.WrappedException;
import com.github.obase.kit.ClassKit;
import com.github.obase.kit.StringKit;
import com.github.obase.webc.Webc.Util;
import com.github.obase.webc.annotation.ServletController;
import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.webc.support.BaseServletMethodProcessor;

@SuppressWarnings("rawtypes")
public class ServletMethodDispatcherFilter extends WebcFrameworkFilter {

	ServletMethodProcessor processor;
	AsyncListener listener;
	long timeout;
	// key: method + lookupPath, val: ServletMethodHandler[method]
	Map<String, ServletMethodObject> rules;

	@Override
	protected final void initFrameworkFilter() throws ServletException {

		// Init the properties
		processor = Util.findWebcBean(applicationContext, ServletMethodProcessor.class, params.controlProcessor);
		if (processor == null) {
			processor = new BaseServletMethodProcessor();
		}
		processor.init(params); // @Since 1.2.0

		listener = Util.findWebcBean(applicationContext, AsyncListener.class, params.asyncListener);
		timeout = params.asyncTimeout;

		// @Controller + @ServletController
		Set<String> beanNameSet = new HashSet<String>();
		Collections.addAll(beanNameSet, applicationContext.getBeanNamesForAnnotation(Controller.class));
		Collections.addAll(beanNameSet, applicationContext.getBeanNamesForAnnotation(ServletController.class));

		Set<ServletMethodObject> objects = new HashSet<ServletMethodObject>();
		if (beanNameSet.size() > 0) {

			// get all filters
			Collection<ServletMethodFilter> servletFilters = applicationContext.getBeansOfType(ServletMethodFilter.class).values();

			for (String beanName : beanNameSet) {
				Controller controller = applicationContext.findAnnotationOnBean(beanName, Controller.class);
				ServletController servletController = applicationContext.findAnnotationOnBean(beanName, ServletController.class);

				Object bean = applicationContext.getBean(beanName);
				Class userClass = ClassUtils.getUserClass(bean);
				for (Method method : userClass.getMethods()) {
					// Signature: @ServletMethod public void xxx(HttpServletRequest, HttpServletResponse)...
					ServletMethod servletMethod = method.getAnnotation(ServletMethod.class);
					if (servletMethod == null) {
						continue;
					}
					int modifiers = method.getModifiers();
					if (!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers)) {
						continue;
					}
					Class[] ptypes = method.getParameterTypes();
					if (ptypes.length != 2 || ptypes[0] != HttpServletRequest.class || ptypes[1] != HttpServletResponse.class) {
						continue;
					}

					String methodName = method.getName();
					String lookupPath = processor.lookup(servletController, controller, userClass, servletMethod, methodName);
					ServletMethodHandler handler = newServletMethodHandler(methodName, bean, findServletFilter(servletFilters, lookupPath, userClass, method.getName(), servletMethod));

					HttpMethod[] methods = servletMethod.method();
					if (methods.length == 0) {
						methods = HttpMethod.values(); // default all
					}
					for (HttpMethod m : methods) {
						ServletMethodObject object = new ServletMethodObject(m, lookupPath, servletMethod, params.defaultAuthType, params.csrfOff, handler);
						if (!objects.add(object)) {
							throw new IllegalStateException("Duplicate lookupPath : " + m + " " + lookupPath + " ==> " + method);
						}
					}
				}
			}
		}

		// setup by processor
		processor.setup(objects);

		// optimize rules
		rules = new HashMap<String, ServletMethodObject>(objects.size());
		for (ServletMethodObject object : objects) {
			rules.put(rkey(object.method.name(), Kits.getServletPath(params.namespace, object.lookupPath, null)), object);
			if (StringKit.isEmpty(object.lookupPath)) {
				rules.put(rkey(object.method.name(), Kits.getServletPath(params.namespace, "/", null)), object); // FIXBUG: special for home page
			}
		}
	}

	private static final String rkey(String method, String servletPath) {
		return method + servletPath;
	}

	@Override
	public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

		req.setCharacterEncoding(Webc.CHARSET_NAME);
		resp.setCharacterEncoding(Webc.CHARSET_NAME);

		final HttpServletRequest request = (HttpServletRequest) req;
		ServletMethodObject object = rules.get(rkey(request.getMethod(), request.getServletPath()));

		if (object != null) {

			req.setAttribute(Webc.ATTR_LOOKUP_PATH, object.lookupPath);
			req.setAttribute(Webc.ATTR_NAMESPACE, params.namespace);

			// FIXBUG: if aysnc not support
			if (req.isAsyncSupported()) {

				// Must require java 8
				AsyncContext asyncContext;
				if (req.isAsyncStarted()) {
					asyncContext = req.getAsyncContext();
				} else {
					asyncContext = req.startAsync();
					if (listener != null) {
						asyncContext.addListener(listener);
					}
					asyncContext.setTimeout(timeout); // @Since 1.2.0: never timeout
				}

				asyncContext.start(new Runnable() {
					@Override
					public void run() {

						final HttpServletRequest request = (HttpServletRequest) asyncContext.getRequest();
						final HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();

						HttpServletRequest prerequest = null;
						try {
							prerequest = processor.process(request, response, object);
							if (prerequest != null) {
								object.handler.service(prerequest, response);
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
						object.handler.service(prerequest, response);
					}
				} catch (Throwable t) {
					processor.error(request, response, t);
				}
			}

		} else {
			chain.doFilter(req, resp);
		}
	}

	private ServletMethodFilter[] findServletFilter(Collection<ServletMethodFilter> servletFilters, String lookupPath, Class userClass, String methodName, ServletMethod annotation) {
		if (servletFilters == null || servletFilters.size() == 0) {
			return ServletMethodFilter.EMPTY_ARRAY;
		}
		LinkedList<ServletMethodFilter> ret = new LinkedList<ServletMethodFilter>();
		for (ServletMethodFilter filter : servletFilters) {
			if (filter.matches(userClass, methodName, annotation, lookupPath)) {
				ret.add(filter);
			}
		}
		if (ret.isEmpty()) {
			return ServletMethodFilter.EMPTY_ARRAY;
		}
		Collections.sort(ret, ServletMethodFilter.OrderBy);
		return ret.toArray(new ServletMethodFilter[ret.size()]);
	}

	public ServletMethodHandler newServletMethodHandler(String methodName, Object bean, ServletMethodFilter... filters) {
		String className = bean.getClass().getCanonicalName() + "__" + methodName;
		Class<?> c;
		try {
			c = ClassKit.loadClass(className);
		} catch (ClassNotFoundException e) {
			byte[] data = Webc.Util.dumpServletMethodObject(className, ClassUtils.getUserClass(bean), methodName);
			c = ClassKit.defineClass(className, data);
		}

		SoftReference<ServletMethodHandler> ref = ServletMethodHandlerRefs.get(c);
		if (ref == null || ref.get() == null) {
			try {
				ref = new SoftReference<ServletMethodHandler>(((ServletMethodHandler) c.newInstance()).bind(bean, filters));
			} catch (Exception e) {
				throw new WrappedException(e);
			}
			ServletMethodHandlerRefs.put(c, ref);
		}
		return ref.get();
	}

	/* 全局缓存池. 处理API与WEB同时需要的情况. 注意:为避免不同ClassLoader,不能使用class name作为主键 */
	private static final Map<Class<?>, SoftReference<ServletMethodHandler>> ServletMethodHandlerRefs = new HashMap<Class<?>, SoftReference<ServletMethodHandler>>();
}

package com.github.obase.webc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.github.obase.webc.config.WebcConfig;

/**
 * The dispatcher filter should be the last order, and do following jobs
 */
public abstract class WebcFrameworkFilter implements Filter {

	static final String DEFAULT_NAMESPACE_SUFFIX = "-filter";
	static final String SERVLET_CONTEXT_PREFIX = WebcFrameworkFilter.class.getName() + ".CONTEXT.";
	static final String INIT_PARAM_DELIMITERS = ",; \t\n";

	protected final Log logger = LogFactory.getLog(getClass());
	protected final ConfigurableWebEnvironment environment = new StandardServletEnvironment();

	protected FilterConfig filterConfig;
	protected ServletContext servletContext;
	protected WebcProxyApplicationContext applicationContext;

	protected com.github.obase.webc.config.WebcConfig.FilterInitParam params;

	@Override
	public final void init(final FilterConfig filterConfig) throws ServletException {

		this.filterConfig = filterConfig;
		this.servletContext = filterConfig.getServletContext();

		// init params
		this.params = WebcConfig.decodeFilterInitParam(filterConfig);

		// init application context
		String ctxAttrName = getFilterContextAttributeName();
		this.applicationContext = (WebcProxyApplicationContext) WebApplicationContextUtils.getWebApplicationContext(this.servletContext, ctxAttrName);
		if (this.applicationContext == null) {
			this.applicationContext = createAndRefreshWebApplicationContext(WebApplicationContextUtils.getWebApplicationContext(this.servletContext));
			this.servletContext.setAttribute(ctxAttrName, this.applicationContext);
		}

		// call subclass
		initFrameworkFilter();
	}

	protected final WebcProxyApplicationContext createAndRefreshWebApplicationContext(WebApplicationContext rootContext) {

		WebcProxyApplicationContext wac = new WebcProxyApplicationContext(); // use custom application context

		wac.setParent(rootContext);
		wac.setConfigLocation(this.filterConfig.getInitParameter(ContextLoader.CONFIG_LOCATION_PARAM)); // contextConfigLocation
		wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + ObjectUtils.getDisplayString(this.servletContext.getContextPath()) + "/" + this.filterConfig.getFilterName());

		wac.setServletContext(this.servletContext);
		wac.setServletConfig(new ServletConfig() {

			@Override
			public String getServletName() {
				return filterConfig.getFilterName();
			}

			@Override
			public ServletContext getServletContext() {
				return filterConfig.getServletContext();
			}

			@Override
			public String getInitParameter(String name) {
				return filterConfig.getInitParameter(name);
			}

			@Override
			public Enumeration<String> getInitParameterNames() {
				return filterConfig.getInitParameterNames();
			}

		});
		wac.setNamespace(filterConfig.getFilterName());
		wac.setEnvironment(this.environment);

		// The wac environment's #initPropertySources will be called in any case when the context
		// is refreshed; do it eagerly here to ensure servlet property sources are in place for
		// use in any post-processing or initialization that occurs below prior to #refresh
		this.environment.initPropertySources(wac.getServletContext(), wac.getServletConfig());

		postProcessWebApplicationContext(wac); // hook for subclass
		applyInitializers(wac);
		wac.refresh();

		return wac;
	}

	protected void postProcessWebApplicationContext(ConfigurableWebApplicationContext wac) {
	}

	// compatible to globalInitializerClasses
	protected final void applyInitializers(ConfigurableApplicationContext wac) {

		List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers = new ArrayList<ApplicationContextInitializer<ConfigurableApplicationContext>>();

		String globalClassNames = this.servletContext.getInitParameter(ContextLoader.GLOBAL_INITIALIZER_CLASSES_PARAM);
		if (globalClassNames != null) {
			for (String className : StringUtils.tokenizeToStringArray(globalClassNames, INIT_PARAM_DELIMITERS)) {
				contextInitializers.add(loadInitializer(className, wac));
			}
		}

		AnnotationAwareOrderComparator.sort(contextInitializers);
		for (ApplicationContextInitializer<ConfigurableApplicationContext> initializer : contextInitializers) {
			initializer.initialize(wac);
		}
	}

	@SuppressWarnings("unchecked")
	private ApplicationContextInitializer<ConfigurableApplicationContext> loadInitializer(String className, ConfigurableApplicationContext wac) {
		try {
			Class<?> initializerClass = ClassUtils.forName(className, wac.getClassLoader());
			Class<?> initializerContextClass = GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
			if (initializerContextClass != null) {
				Assert.isAssignable(initializerContextClass, wac.getClass(),
						String.format("Could not add context initializer [%s] since its generic parameter [%s] " + "is not assignable from the type of application context used by this "
								+ "framework servlet [%s]: ", initializerClass.getName(), initializerContextClass.getName(), wac.getClass().getName()));
			}
			return BeanUtils.instantiateClass(initializerClass, ApplicationContextInitializer.class);
		} catch (Exception ex) {
			throw new IllegalArgumentException(String.format("Could not instantiate class [%s] specified " + "via 'contextInitializerClasses' init-param", className), ex);
		}
	}

	@Override
	public final void destroy() {
		this.servletContext.log("Destroying Spring FrameworkServlet '" + this.filterConfig.getFilterName() + "'");

		destroyFrameworkFilter();
		if (this.applicationContext instanceof ConfigurableApplicationContext) {
			((ConfigurableApplicationContext) this.applicationContext).close();
		}
	}

	protected final String getFilterContextAttributeName() {
		return SERVLET_CONTEXT_PREFIX + this.filterConfig.getFilterName();
	}

	protected abstract void initFrameworkFilter() throws ServletException;

	protected void destroyFrameworkFilter() {
		// override by subclass
	}

}

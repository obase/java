package com.github.obase.webc;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.util.NestedServletException;

import com.github.obase.webc.config.WebcConfig;

/**
 * The dispatcher filter should be the last order, and do following jobs
 */
public abstract class WebcFrameworkFilter implements Filter, EnvironmentAware {

	static final String DEFAULT_NAMESPACE_SUFFIX = "-filter";
	static final String SERVLET_CONTEXT_PREFIX = WebcFrameworkFilter.class.getName() + ".CONTEXT.";
	static String INIT_PARAM_DELIMITERS = ",; \t\n";

	static class FilterConfigPropertyValues extends MutablePropertyValues {

		private static final long serialVersionUID = 1L;

		public FilterConfigPropertyValues(FilterConfig config, Set<String> requiredProperties) throws ServletException {

			Set<String> missingProps = (requiredProperties != null && !requiredProperties.isEmpty()) ? new HashSet<String>(requiredProperties) : null;

			Enumeration<?> en = config.getInitParameterNames();
			while (en.hasMoreElements()) {
				String property = (String) en.nextElement();
				Object value = config.getInitParameter(property);
				addPropertyValue(new PropertyValue(property, value));
				if (missingProps != null) {
					missingProps.remove(property);
				}
			}

			// Fail if we are still missing properties.
			if (missingProps != null && missingProps.size() > 0) {
				throw new ServletException("Initialization from FilterConfig for filter '" + config.getFilterName() + "' failed; the following required properties were missing: " + StringUtils.collectionToDelimitedString(missingProps, ", "));
			}
		}
	}

	protected final Log logger = LogFactory.getLog(getClass());

	protected final Set<String> requiredProperties = new HashSet<String>();
	protected ConfigurableEnvironment environment = new StandardServletEnvironment();

	protected FilterConfig filterConfig;
	protected ServletContext servletContext;
	protected WebApplicationContext webApplicationContext;

	protected final void addRequiredProperty(String property) {
		this.requiredProperties.add(property);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = (ConfigurableEnvironment) environment;
	}

	@Override
	public final void init(final FilterConfig filterConfig) throws ServletException {

		try {
			PropertyValues pvs = new FilterConfigPropertyValues(filterConfig, this.requiredProperties);
			BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
			ResourceLoader resourceLoader = new ServletContextResourceLoader(filterConfig.getServletContext());
			bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, this.environment));
			bw.setPropertyValues(pvs, true);
		} catch (BeansException ex) {
			String msg = "Failed to set bean properties on filter '" + filterConfig.getFilterName() + "': " + ex.getMessage();
			logger.error(msg, ex);
			throw new NestedServletException(msg, ex);
		}

		// initial,convert filterConfig to adapter servletConfig
		this.filterConfig = filterConfig;
		this.servletContext = filterConfig.getServletContext();
		this.webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(this.servletContext, getFilterContextAttributeName());
		if (this.webApplicationContext == null) {
			this.webApplicationContext = createAndRefreshWebApplicationContext(WebApplicationContextUtils.getWebApplicationContext(this.servletContext));
			this.servletContext.setAttribute(getFilterContextAttributeName(), this.webApplicationContext);
		}

		// call subclass
		initFrameworkFilter();
	}

	protected final WebApplicationContext createAndRefreshWebApplicationContext(WebApplicationContext rootContext) {

		XmlWebApplicationContext wac = new XmlWebApplicationContext() {
			@Override
			protected final String[] getDefaultConfigLocations() {
				return null;
			}
		};
		wac.setEnvironment(this.environment);
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

		// The wac environment's #initPropertySources will be called in any case when the context
		// is refreshed; do it eagerly here to ensure servlet property sources are in place for
		// use in any post-processing or initialization that occurs below prior to #refresh
		ConfigurableEnvironment env = wac.getEnvironment();
		if (env instanceof ConfigurableWebEnvironment) {
			((ConfigurableWebEnvironment) env).initPropertySources(wac.getServletContext(), wac.getServletConfig());
		}

		postProcessWebApplicationContext(wac);
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
				Assert.isAssignable(initializerContextClass, wac.getClass(), String.format("Could not add context initializer [%s] since its generic parameter [%s] " + "is not assignable from the type of application context used by this " + "framework servlet [%s]: ", initializerClass.getName(),
						initializerContextClass.getName(), wac.getClass().getName()));
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
		if (this.webApplicationContext instanceof ConfigurableApplicationContext) {
			((ConfigurableApplicationContext) this.webApplicationContext).close();
		}
	}

	protected final String getFilterContextAttributeName() {
		return SERVLET_CONTEXT_PREFIX + this.filterConfig.getFilterName();
	}

	protected com.github.obase.webc.config.WebcConfig.FilterConfig config;
	protected String namespace;

	protected void initFrameworkFilter() throws ServletException {
		config = WebcConfig.decodeFilterInitParam(filterConfig);
		namespace = config.namespace;
	}

	protected void destroyFrameworkFilter() {
	}

}

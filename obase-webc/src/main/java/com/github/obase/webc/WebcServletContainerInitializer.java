package com.github.obase.webc;

import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.core.io.Resource;

import com.github.obase.kit.StringKit;
import com.github.obase.webc.config.WebcConfig;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;
import com.github.obase.webc.config.WebcConfigParser;

public class WebcServletContainerInitializer implements ServletContainerInitializer {

	/**
	 * It is a convenient method to support spring-boot. e.g
	 * 
	 * <pre>
	 * &#64;SpringBootApplication
	 * public class AppMain extends WebcServletContainerInitializer implements ServletContextInitializer {
	 * 
	 * 	public static void main(String[] args) {
	 * 		SpringApplication.run(AppMain.class, args);
	 * 	}
	 * 
	 * }
	 * </pre>
	 */
	public void onStartup(ServletContext servletContext) throws ServletException {
		onStartup(null, servletContext);
	}

	@Override
	public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) throws ServletException {

		Resource configResource = Webc.Util.getDefaultConfigResource(servletContext, Webc.DEFAULT_CONFIG_LOCATION, this.getClass(), Webc.DEFAULT_CONFIG_LOCATION2);
		if (configResource != null) {

			WebcConfig config = WebcConfigParser.parse(configResource);
			if (!config.withoutApplicationContext) {
				config = mergeInitParamForContext(servletContext, config);
				if (StringKit.isNotEmpty(config.contextConfigLocation)) {
					initWebkitFrameworkListener(servletContext, config);
				}
			}
			if (!config.withoutServletContext) {
				if (config.servlets.size() > 0) {
					for (FilterInitParam fc : config.servlets) {
						initWebkitFrameworkFilter(ServletMethodDispatcherFilter.class, servletContext, mergeInitParamForServlet(servletContext, fc));
					}
				} else {
					String contextConfigLocation = Webc.Util.getDefaultConfigLocation(servletContext, Webc.DEFAULT_SERVLET_CONTEXT_CONFIG_LOCATION, this.getClass(), Webc.DEFAULT_SERVLET_CONTEXT_CONFIG_LOCATION2);
					if (StringKit.isNotEmpty(contextConfigLocation)) {
						FilterInitParam params = new FilterInitParam();
						params.contextConfigLocation = contextConfigLocation;
						FilterInitParam fc = mergeInitParamForServlet(servletContext, params);
						initWebkitFrameworkFilter(ServletMethodDispatcherFilter.class, servletContext, fc);
					}
				}
			}
			if (!config.withoutServiceContext) {
				if (config.services.size() > 0) {
					for (FilterInitParam fc : config.services) {
						initWebkitFrameworkFilter(InvokerServiceDispatcherFilter.class, servletContext, mergeInitParamForService(servletContext, fc));
					}
				} else {
					String contextConfigLocation = Webc.Util.getDefaultConfigLocation(servletContext, Webc.DEFAULT_SERVICE_CONTEXT_CONFIG_LOCATION, this.getClass(), Webc.DEFAULT_SERVICE_CONTEXT_CONFIG_LOCATION2);
					if (StringKit.isNotEmpty(contextConfigLocation)) {
						FilterInitParam params = new FilterInitParam();
						params.contextConfigLocation = contextConfigLocation;
						FilterInitParam fc = mergeInitParamForService(servletContext, params);
						initWebkitFrameworkFilter(InvokerServiceDispatcherFilter.class, servletContext, fc);
					}
				}
			}

		}

		servletContext.log("WebcServletContainerInitializer onStartup process completely");

	}

	private void initWebkitFrameworkListener(ServletContext servletContext, WebcConfig config) {
		WebcConfig.encodeContextInitParam(servletContext, config);
		servletContext.addListener(ApplicationContextLoaderListener.class);
	}

	private void initWebkitFrameworkFilter(Class<? extends WebcFrameworkFilter> filterClass, ServletContext servletContext, FilterInitParam config) {
		String filterName = filterClass.getSimpleName() + "$" + (config.namespace == null ? "" : replaceInvalidIdentifier(config.namespace)) + "$" + (config.hashCode() & 0xFFFFFFFFL);
		FilterRegistration.Dynamic dynamic = servletContext.addFilter(filterName, filterClass);
		dynamic.setAsyncSupported(!config.asyncOff);
		dynamic.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.FORWARD), false, Webc.Util.getUrlPatternForNamespace(config.namespace));
		WebcConfig.encodeFilterInitParam(dynamic, config);
	}

	private static String replaceInvalidIdentifier(String chars) {
		StringBuilder sb = new StringBuilder(chars);
		for (int i = 0, n = sb.length(); i < n; i++) {
			if (Character.isJavaIdentifierPart(sb.charAt(i))) {
				continue;
			}
			sb.setCharAt(i, '_');
		}
		return sb.toString();
	}

	private WebcConfig mergeInitParamForContext(ServletContext servletContext, WebcConfig config) {

		if (StringKit.isEmpty(config.contextConfigLocation)) {
			config.contextConfigLocation = Webc.Util.getDefaultConfigLocation(servletContext, Webc.DEFAULT_CONTEXT_CONFIG_LOCATION, this.getClass(), Webc.DEFAULT_CONTEXT_CONFIG_LOCATION2);
		}
		return config;
	}

	private FilterInitParam mergeInitParamForServlet(ServletContext servletContext, FilterInitParam params) {
		if (StringKit.isEmpty(params.namespace)) {
			params.namespace = Webc.DEFAULT_NAMESPACE_FOR_SERVLET;
		}
		// @Since 1.2.0: Not using default servletContext.xml any more
		// if (StringKit.isEmpty(params.contextConfigLocation)) {
		// params.contextConfigLocation = Webc.Util.getDefaultConfigLocation(servletContext, Webc.DEFAULT_SERVLET_CONTEXT_CONFIG_LOCATION, this.getClass(), Webc.DEFAULT_SERVLET_CONTEXT_CONFIG_LOCATION2);
		// }
		return params;
	}

	private FilterInitParam mergeInitParamForService(ServletContext servletContext, FilterInitParam params) {
		if (StringKit.isEmpty(params.namespace)) {
			params.namespace = Webc.DEFAULT_NAMESPACE_FOR_SERVICE;
		}
		// @Since 1.2.0: Not using default servletContext.xml any more
		// if (StringKit.isEmpty(params.contextConfigLocation)) {
		// params.contextConfigLocation = Webc.Util.getDefaultConfigLocation(servletContext, Webc.DEFAULT_SERVICE_CONTEXT_CONFIG_LOCATION, this.getClass(), Webc.DEFAULT_SERVICE_CONTEXT_CONFIG_LOCATION2);
		// }
		return params;
	}

}

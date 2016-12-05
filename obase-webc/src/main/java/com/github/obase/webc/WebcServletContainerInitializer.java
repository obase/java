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
import com.github.obase.webc.config.WebcConfig.FilterConfig;
import com.github.obase.webc.config.WebcConfigParser;

public final class WebcServletContainerInitializer implements ServletContainerInitializer {

	@Override
	public void onStartup(Set<Class<?>> webAppInitializerClasses, ServletContext servletContext) throws ServletException {

		Resource configResource = Webc.Util.getDefaultConfigResource(servletContext, Webc.DEFAULT_CONFIG_LOCATION, this.getClass(), Webc.DEFAULT_CONFIG_LOCATION2);
		if (configResource != null) {

			WebcConfig config = WebcConfigParser.parse(configResource);
			config = WebcConfig.mergeDefaultForApplication(config, servletContext, this.getClass());
			if (!config.withoutApplicationContext) {
				if (StringKit.isNotEmpty(config.contextConfigLocation)) {
					initWebkitFrameworkListener(servletContext, config);
				}
			}
			if (!config.withoutServletContext) {
				if (config.servlets.size() > 0) {
					for (FilterConfig fc : config.servlets) {
						initWebkitFrameworkFilter(ServletMethodDispatcherFilter.class, servletContext, WebcConfig.mergeDefaultForServlet(fc, servletContext, this.getClass()));
					}
				} else {
					FilterConfig fc = WebcConfig.mergeDefaultForServlet(new FilterConfig(), servletContext, this.getClass());
					if (StringKit.isNotEmpty(fc.contextConfigLocation)) {
						initWebkitFrameworkFilter(ServletMethodDispatcherFilter.class, servletContext, fc);
					}
				}
			}
			if (!config.withoutServiceContext) {
				if (config.services.size() > 0) {
					for (FilterConfig fc : config.services) {
						initWebkitFrameworkFilter(InvokerServiceDispatcherFilter.class, servletContext, WebcConfig.mergeDefaultForService(fc, servletContext, this.getClass()));
					}
				} else {
					FilterConfig fc = WebcConfig.mergeDefaultForService(new FilterConfig(), servletContext, this.getClass());
					if (StringKit.isNotEmpty(fc.contextConfigLocation)) {
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

	private void initWebkitFrameworkFilter(Class<? extends WebcFrameworkFilter> filterClass, ServletContext servletContext, FilterConfig config) {
		String filterName = filterClass.getSimpleName() + "$" + (config.namespace == null ? "" : replaceInvalidIdentifier(config.namespace)) + "$" + (config.hashCode() & 0xFFFFFFFFL);
		FilterRegistration.Dynamic dynamic = servletContext.addFilter(filterName, filterClass);
		dynamic.setAsyncSupported(true);
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

}

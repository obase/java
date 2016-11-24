package com.github.obase.webc.config;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoader;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.github.obase.webc.Webc;
import com.github.obase.kit.StringKit;

@JacksonXmlRootElement(localName = WebcConfig.ROOT_ELEMENT)
public class WebcConfig {

	public static final String CONTEXT_CONFIG_LOCATION = ContextLoader.CONFIG_LOCATION_PARAM;
	public static final String NAMESPACE = "namespace";
	public static final String ROOT_ELEMENT = "webc";

	public boolean withoutApplicationContext;
	public boolean withoutServletContext;
	public boolean withoutServiceContext;
	public String contextConfigLocation;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonProperty(value = "servlet")
	public final List<FilterConfig> servlets = new LinkedList<FilterConfig>();

	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonProperty(value = "service")
	public final List<FilterConfig> services = new LinkedList<FilterConfig>();

	public static class FilterConfig {
		public String namespace;
		public String contextConfigLocation;
	}

	public static FilterConfig mergeDefaultForServlet(FilterConfig config, ServletContext servletContext, Class<?> clazz) {
		if (StringKit.isEmpty(config.namespace)) {
			config.namespace = Webc.DEFAULT_NAMESPACE_FOR_SERVLET;
		}
		if (StringKit.isEmpty(config.contextConfigLocation)) {
			config.contextConfigLocation = Webc.Util.getDefaultConfigLocation(servletContext, Webc.DEFAULT_SERVLET_CONTEXT_CONFIG_LOCATION, clazz, Webc.DEFAULT_SERVLET_CONTEXT_CONFIG_LOCATION2);
		}
		return config;
	}

	public static FilterConfig mergeDefaultForService(FilterConfig config, ServletContext servletContext, Class<?> clazz) {
		if (StringKit.isEmpty(config.namespace)) {
			config.namespace = Webc.DEFAULT_NAMESPACE_FOR_SERVICE;
		}
		if (StringKit.isEmpty(config.contextConfigLocation)) {
			config.contextConfigLocation = Webc.Util.getDefaultConfigLocation(servletContext, Webc.DEFAULT_SERVICE_CONTEXT_CONFIG_LOCATION, clazz, Webc.DEFAULT_SERVICE_CONTEXT_CONFIG_LOCATION2);
		}
		return config;
	}

	public static WebcConfig mergeDefaultForApplication(WebcConfig config, ServletContext servletContext, Class<?> clazz) {
		if (StringKit.isEmpty(config.contextConfigLocation)) {
			config.contextConfigLocation = Webc.Util.getDefaultConfigLocation(servletContext, Webc.DEFAULT_CONTEXT_CONFIG_LOCATION, clazz, Webc.DEFAULT_CONTEXT_CONFIG_LOCATION2);
		}
		return config;
	}

	public static void encodeContextInitParam(ServletContext servletContext, WebcConfig config) {
		if (StringKit.isNotEmpty(config.contextConfigLocation)) {
			servletContext.setInitParameter(CONTEXT_CONFIG_LOCATION, config.contextConfigLocation);
		}
	}

	public static FilterConfig decodeFilterInitParam(javax.servlet.FilterConfig filterConfig) {

		FilterConfig ret = new FilterConfig();
		ret.namespace = getStringParam(filterConfig, NAMESPACE, null);
		ret.contextConfigLocation = getStringParam(filterConfig, CONTEXT_CONFIG_LOCATION, null);

		return ret;
	}

	public static void encodeFilterInitParam(FilterRegistration.Dynamic dynamic, FilterConfig config) {

		if (StringKit.isNotEmpty(config.contextConfigLocation)) {
			dynamic.setInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, config.contextConfigLocation);
		}

		if (StringKit.isNotEmpty(config.namespace)) {
			dynamic.setInitParameter(WebcConfig.NAMESPACE, config.namespace);
		}

	}

	public static final boolean getBooleanParam(javax.servlet.FilterConfig filterConfig, String name, boolean def) {
		String param = filterConfig.getInitParameter(name);
		if (StringKit.isEmpty(param)) {
			return def;
		}
		return Boolean.parseBoolean(param);
	}

	public static final int getIntParam(javax.servlet.FilterConfig filterConfig, String name, int def) {
		String param = filterConfig.getInitParameter(name);
		if (StringKit.isEmpty(param)) {
			return def;
		}
		return Integer.parseInt(param);
	}

	public static final String getStringParam(javax.servlet.FilterConfig filterConfig, String name, String def) {
		String param = filterConfig.getInitParameter(name);
		if (StringKit.isEmpty(param)) {
			return def;
		}
		return param;
	}

}

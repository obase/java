package com.github.obase.webc.config;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoader;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.github.obase.WrappedException;
import com.github.obase.kit.StringKit;
<<<<<<< HEAD
import com.github.obase.webc.AuthType;
=======
import com.github.obase.webc.Webc;
>>>>>>> branch 'master' of git@github.com:obase/java.git

@JacksonXmlRootElement(localName = WebcConfig.ROOT)
public class WebcConfig {

	public static final String ROOT = "webc";
	public static final String NAMESPACE = "namespace";
	public static final String CONTEXT_CONFIG_LOCATION = ContextLoader.CONFIG_LOCATION_PARAM;
	public static final String ASYNC_LISTENER = "asyncListener";
	public static final String TIMEOUT_SECOND = "timeoutSecond"; // seconds
	public static final String SEND_ERROR = "sendError";
	public static final String CONTROL_PREFIX = "controlPrefix";
	public static final String CONTROL_SUFFIX = "controlSuffix";
	public static final String CONTROL_PROCESSOR = "controlProcessor";
	public static final String WSID_TOKEN_BASE = "wsidTokenBase";
<<<<<<< HEAD
	public static final String DEFAULT_AUTH_TYPE = "defaultAuthType";
	public static final String REFERER_DOMAIN = "refererDomain";
=======
>>>>>>> branch 'master' of git@github.com:obase/java.git

	public boolean withoutApplicationContext;
	public boolean withoutServletContext;
	public boolean withoutServiceContext;
	public String contextConfigLocation;

	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonProperty(value = "servlet")
	public final List<FilterInitParam> servlets = new LinkedList<FilterInitParam>();

	@JacksonXmlElementWrapper(useWrapping = false)
	@JsonProperty(value = "service")
	public final List<FilterInitParam> services = new LinkedList<FilterInitParam>();

	public static class FilterInitParam {
		public String namespace;
		public String contextConfigLocation;
		public Class<?> asyncListener;
		public int timeoutSecond;
		public boolean sendError;
		public Class<?> controlProcessor;
<<<<<<< HEAD
		public String controlPrefix; // multi values by comma
		public String controlSuffix;// multi values by comma
		public int wsidTokenBase; // BKDRHash的base,默认为0
		public AuthType defaultAuthType;
		public String refererDomain; // multi values by comma
=======
		public String controlPrefix;
		public String controlSuffix;
		public int wsidTokenBase; // BKDRHash的base,默认为0
>>>>>>> branch 'master' of git@github.com:obase/java.git
	}

	public static void encodeContextInitParam(ServletContext servletContext, WebcConfig config) {
		if (StringKit.isNotEmpty(config.contextConfigLocation)) {
			servletContext.setInitParameter(CONTEXT_CONFIG_LOCATION, config.contextConfigLocation);
		}
	}

	/**
	 * default init params and merge default values
	 */
	public static FilterInitParam decodeFilterInitParam(javax.servlet.FilterConfig filterConfig) {

		FilterInitParam ret = new FilterInitParam();
		ret.namespace = getStringParam(filterConfig, NAMESPACE, null);
		ret.contextConfigLocation = getStringParam(filterConfig, CONTEXT_CONFIG_LOCATION, null);
		ret.asyncListener = getClassParam(filterConfig, ASYNC_LISTENER, null);
<<<<<<< HEAD
		ret.timeoutSecond = getIntParam(filterConfig, TIMEOUT_SECOND, 0);
		ret.sendError = getBooleanParam(filterConfig, SEND_ERROR, false);
		ret.controlProcessor = getClassParam(filterConfig, CONTROL_PROCESSOR, null);
		ret.controlPrefix = getStringParam(filterConfig, CONTROL_PREFIX, null);
		ret.controlSuffix = getStringParam(filterConfig, CONTROL_SUFFIX, null);
		ret.wsidTokenBase = getIntParam(filterConfig, WSID_TOKEN_BASE, 0);
		String authTypeStr = getStringParam(filterConfig, DEFAULT_AUTH_TYPE, null);
		if (authTypeStr != null) {
			ret.defaultAuthType = AuthType.valueOf(authTypeStr);
		}
		ret.refererDomain = getStringParam(filterConfig, REFERER_DOMAIN, null);
=======
		ret.timeoutSecond = getIntParam(filterConfig, TIMEOUT_SECOND, Webc.DEFAULT_TIMEOUT_SECOND);
		ret.sendError = getBooleanParam(filterConfig, SEND_ERROR, false);
		ret.controlProcessor = getClassParam(filterConfig, CONTROL_PROCESSOR, null);
		ret.controlPrefix = getStringParam(filterConfig, CONTROL_PREFIX, null);
		ret.controlSuffix = getStringParam(filterConfig, CONTROL_SUFFIX, null);
		ret.wsidTokenBase = getIntParam(filterConfig, WSID_TOKEN_BASE, Webc.DEFAULT_WSID_TOKEN_BASE);
>>>>>>> branch 'master' of git@github.com:obase/java.git

		return ret;
	}

	public static void encodeFilterInitParam(FilterRegistration.Dynamic dynamic, FilterInitParam param) {

		if (StringKit.isNotEmpty(param.namespace)) {
			dynamic.setInitParameter(NAMESPACE, param.namespace);
		}

		if (StringKit.isNotEmpty(param.contextConfigLocation)) {
			dynamic.setInitParameter(CONTEXT_CONFIG_LOCATION, param.contextConfigLocation);
<<<<<<< HEAD
=======
		}

		if (param.asyncListener != null) {
			dynamic.setInitParameter(ASYNC_LISTENER, param.asyncListener.getCanonicalName());
		}

		if (param.timeoutSecond != 0) {
			dynamic.setInitParameter(TIMEOUT_SECOND, String.valueOf(param.timeoutSecond));
		}

		if (param.sendError) {
			dynamic.setInitParameter(SEND_ERROR, String.valueOf(param.sendError));
		}

		if (param.controlProcessor != null) {
			dynamic.setInitParameter(ASYNC_LISTENER, param.controlProcessor.getCanonicalName());
		}

		if (StringKit.isNotEmpty(param.controlPrefix)) {
			dynamic.setInitParameter(CONTROL_PREFIX, param.controlPrefix);
		}

		if (StringKit.isNotEmpty(param.controlSuffix)) {
			dynamic.setInitParameter(CONTROL_PREFIX, param.controlSuffix);
>>>>>>> branch 'master' of git@github.com:obase/java.git
		}

		if (param.asyncListener != null) {
			dynamic.setInitParameter(ASYNC_LISTENER, param.asyncListener.getCanonicalName());
		}

		if (param.timeoutSecond != 0) {
			dynamic.setInitParameter(TIMEOUT_SECOND, String.valueOf(param.timeoutSecond));
		}

		if (param.sendError) {
			dynamic.setInitParameter(SEND_ERROR, String.valueOf(param.sendError));
		}

		if (param.controlProcessor != null) {
			dynamic.setInitParameter(ASYNC_LISTENER, param.controlProcessor.getCanonicalName());
		}

		if (StringKit.isNotEmpty(param.controlPrefix)) {
			dynamic.setInitParameter(CONTROL_PREFIX, param.controlPrefix);
		}

		if (StringKit.isNotEmpty(param.controlSuffix)) {
			dynamic.setInitParameter(CONTROL_PREFIX, param.controlSuffix);
		}

		if (param.defaultAuthType != null) {
			dynamic.setInitParameter(DEFAULT_AUTH_TYPE, param.defaultAuthType.name());
		}
		if (param.refererDomain != null) {
			dynamic.setInitParameter(REFERER_DOMAIN, param.refererDomain);
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

	public static final Class<?> getClassParam(javax.servlet.FilterConfig filterConfig, String name, Class<?> def) {
		String param = filterConfig.getInitParameter(name);
		if (StringKit.isEmpty(param)) {
			return def;
		}
		try {
			return Class.forName(param);
		} catch (ClassNotFoundException e) {
			throw new WrappedException(e);
		}
	}

}

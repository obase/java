package com.github.obase.webc.config;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import com.github.obase.WrappedException;
import com.github.obase.kit.ClassKit;
import com.github.obase.kit.StringKit;
import com.github.obase.webc.AuthType;

public class WebcConfig {

	public static interface Props {
		String webc = "webc";
		String withoutApplicationContext = "withoutApplicationContext";
		String withoutServletContext = "withoutServletContext";
		String withoutServiceContext = "withoutServiceContext";
		String contextConfigLocation = "contextConfigLocation";
		String servlet = "servlet";
		String service = "service";
		String namespace = "namespace";
		String asyncListener = "asyncListener";
		String timeoutSecond = "timeoutSecond";
		String sendError = "sendError";
		String controlProcessor = "controlProcessor";
		String controlPrefix = "controlPrefix";
		String controlSuffix = "controlSuffix";
		String wsidTokenBase = "wsidTokenBase";
		String wsidDomain = "wsidDomain"; // For shared by multiple domain
		String defaultAuthType = "defaultAuthType";
		String refererDomain = "refererDomain";
	}

	public boolean withoutApplicationContext;
	public boolean withoutServletContext;
	public boolean withoutServiceContext;
	public String contextConfigLocation;
	public final List<FilterInitParam> servlets = new LinkedList<FilterInitParam>();
	public final List<FilterInitParam> services = new LinkedList<FilterInitParam>();

	public static class FilterInitParam {
		public String namespace;
		public String contextConfigLocation;
		public String asyncListener;
		public int timeoutSecond;
		public boolean sendError;
		public String controlProcessor;
		public String controlPrefix; // multi values by comma
		public String controlSuffix;// multi values by comma
		public int wsidTokenBase; // BKDRHash的base,默认为0
		public String wsidDomain;
		public AuthType defaultAuthType;
		public String refererDomain; // multi values by comma
	}

	public static void encodeContextInitParam(ServletContext servletContext, WebcConfig config) {
		if (StringKit.isNotEmpty(config.contextConfigLocation)) {
			servletContext.setInitParameter(Props.contextConfigLocation, config.contextConfigLocation);
		}
	}

	/**
	 * default init params and merge default values
	 */
	public static FilterInitParam decodeFilterInitParam(javax.servlet.FilterConfig filterConfig) {

		FilterInitParam ret = new FilterInitParam();
		ret.namespace = getStringParam(filterConfig, Props.namespace, null);
		ret.contextConfigLocation = getStringParam(filterConfig, Props.contextConfigLocation, null);
		ret.asyncListener = getStringParam(filterConfig, Props.asyncListener, null);
		ret.timeoutSecond = getIntParam(filterConfig, Props.timeoutSecond, 0);
		ret.sendError = getBooleanParam(filterConfig, Props.sendError, false);
		ret.controlProcessor = getStringParam(filterConfig, Props.controlProcessor, null);
		ret.controlPrefix = getStringParam(filterConfig, Props.controlPrefix, null);
		ret.controlSuffix = getStringParam(filterConfig, Props.controlSuffix, null);
		ret.wsidTokenBase = getIntParam(filterConfig, Props.wsidTokenBase, 0);
		ret.wsidDomain = getStringParam(filterConfig, Props.wsidDomain, null);
		String authTypeStr = getStringParam(filterConfig, Props.defaultAuthType, null);
		if (authTypeStr != null) {
			ret.defaultAuthType = AuthType.valueOf(authTypeStr);
		}
		ret.refererDomain = getStringParam(filterConfig, Props.refererDomain, null);

		return ret;
	}

	public static void encodeFilterInitParam(FilterRegistration.Dynamic dynamic, FilterInitParam param) {

		if (StringKit.isNotEmpty(param.namespace)) {
			dynamic.setInitParameter(Props.namespace, param.namespace);
		}

		if (StringKit.isNotEmpty(param.contextConfigLocation)) {
			dynamic.setInitParameter(Props.contextConfigLocation, param.contextConfigLocation);
		}

		if (param.asyncListener != null) {
			dynamic.setInitParameter(Props.asyncListener, param.asyncListener);
		}

		if (param.timeoutSecond != 0) {
			dynamic.setInitParameter(Props.timeoutSecond, String.valueOf(param.timeoutSecond));
		}

		if (param.sendError) {
			dynamic.setInitParameter(Props.sendError, String.valueOf(param.sendError));
		}

		if (param.controlProcessor != null) {
			dynamic.setInitParameter(Props.controlProcessor, param.controlProcessor);
		}

		if (StringKit.isNotEmpty(param.controlPrefix)) {
			dynamic.setInitParameter(Props.controlPrefix, param.controlPrefix);
		}

		if (StringKit.isNotEmpty(param.controlSuffix)) {
			dynamic.setInitParameter(Props.controlSuffix, param.controlSuffix);
		}

		if (param.wsidTokenBase != 0) {
			dynamic.setInitParameter(Props.wsidTokenBase, String.valueOf(param.wsidTokenBase));
		}
		if (param.wsidDomain != null) {
			dynamic.setInitParameter(Props.wsidDomain, param.wsidDomain);
		}

		if (param.defaultAuthType != null) {
			dynamic.setInitParameter(Props.defaultAuthType, param.defaultAuthType.name());
		}
		if (param.refererDomain != null) {
			dynamic.setInitParameter(Props.refererDomain, param.refererDomain);
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
			return ClassKit.forName(param);
		} catch (ClassNotFoundException e) {
			throw new WrappedException(e);
		}
	}

}

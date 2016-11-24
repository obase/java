package com.github.obase.webc;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.AsyncListener;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;

import com.github.obase.webc.annotation.ServletMethod;

public interface ServletMethodProcessor {

	void initMappingRules(FilterConfig filterConfig, Map<String, ServletMethodObject[]> rules) throws ServletException;

	HttpServletRequest preprocess(HttpServletRequest request, HttpServletResponse response, ServletMethodObject object) throws Exception;

	void postprocess(HttpServletRequest request, HttpServletResponse response, Throwable t);

	AsyncListener getAsyncListener();

	long getSessionTimeout();

	String parseLookupPath(Class<?> targetClass, Controller classAnnotation, Method targetMethod, ServletMethod methodAnnotation);

}

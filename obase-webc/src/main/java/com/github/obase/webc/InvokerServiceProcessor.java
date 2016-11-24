package com.github.obase.webc;

import java.io.IOException;
import java.util.Map;

import javax.servlet.AsyncListener;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface InvokerServiceProcessor {

	void initMappingRules(FilterConfig filterConfig, Map<String, InvokerServiceObject> rules) throws ServletException;

	HttpServletRequest preprocess(HttpServletRequest request, HttpServletResponse response, InvokerServiceObject object) throws IOException, ServletException;

	void postprocess(HttpServletRequest request, HttpServletResponse response, Throwable t);

	AsyncListener getAsyncListener();

	long getSessionTimeout();

}
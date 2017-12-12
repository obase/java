package com.github.obase.webc;

import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.webc.config.WebcConfig.FilterInitParam;

public interface InvokerServiceProcessor {

	void init(FilterInitParam params);

	void setup(Collection<InvokerServiceObject> rules) throws ServletException;

	HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, InvokerServiceObject object) throws Throwable;

	void error(HttpServletRequest request, HttpServletResponse response, Throwable t);

}
package com.github.obase.webc;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.webc.config.WebcConfig.FilterInitParam;

public interface InvokerServiceProcessor {

	void setup(FilterInitParam params, Map<Class<?>, InvokerServiceObject> rules) throws ServletException;

<<<<<<< HEAD
	HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, InvokerServiceObject object) throws Throwable;
=======
	HttpServletRequest process(HttpServletRequest request, HttpServletResponse response) throws Throwable;
>>>>>>> branch 'master' of git@github.com:obase/java.git

	void error(HttpServletRequest request, HttpServletResponse response, Throwable t);

}
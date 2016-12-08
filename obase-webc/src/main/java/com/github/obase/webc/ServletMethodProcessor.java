package com.github.obase.webc;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;

import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;

public interface ServletMethodProcessor {

	/**
	 * register and add more if necessary
	 */
	void setup(FilterInitParam params, Map<String, ServletMethodObject> rules) throws ServletException;

	/**
	 * process and replace request if necessary
	 */
	HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, ServletMethodObject object) throws Throwable;

	/**
	 * process when error. It could not throw exception any more
	 */
	void error(HttpServletRequest request, HttpServletResponse response, Throwable t);

	/**
	 * return the lookupPath for the servlet method
	 */
	String lookup(Controller classAnnotation, Class<?> clazz, ServletMethod methodAnnotation, String methodName);

}

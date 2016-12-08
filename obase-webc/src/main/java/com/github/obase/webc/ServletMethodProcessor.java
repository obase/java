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
<<<<<<< HEAD
	void setup(FilterInitParam params, Map<String, ServletMethodObject> rules) throws ServletException;
=======
	void setup(FilterInitParam params, Map<String, ServletMethodRules> rules) throws ServletException;
>>>>>>> branch 'master' of git@github.com:obase/java.git

	/**
	 * process and replace request if necessary
	 */
<<<<<<< HEAD
	HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, ServletMethodObject object) throws Throwable;
=======
	HttpServletRequest process(HttpServletRequest request, HttpServletResponse response) throws Throwable;
>>>>>>> branch 'master' of git@github.com:obase/java.git

	/**
	 * process when error. It could not throw exception any more
	 */
	void error(HttpServletRequest request, HttpServletResponse response, Throwable t);

	/**
	 * return the lookupPath for the servlet method
	 */
	String lookup(Controller classAnnotation, Class<?> clazz, ServletMethod methodAnnotation, String methodName);

}

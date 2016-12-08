<<<<<<< HEAD
package com.github.obase.webc;

import org.springframework.http.HttpMethod;

import com.github.obase.webc.annotation.ServletMethod;

/**
 * 保存ServletMethod的关系规则
 */
public class ServletMethodObject {

	public static final int HANDLES_LENGTH = HttpMethod.values().length;

	public AuthType authType; // process default parsing
	public final ServletMethod annotation;
	public final String lookupPath;
	public final ServletMethodHandler[] handlers = new ServletMethodHandler[HANDLES_LENGTH];

	public ServletMethodObject(ServletMethod annotation, String lookupPath) {
		this.annotation = annotation;
		this.lookupPath = lookupPath;
	}

}
=======
package com.github.obase.webc;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ServletMethodObject {

	protected Object bean;
	protected ServletMethodFilter[] filters;

	protected final ServletMethodObject bind(Object bean, ServletMethodFilter... filters) {
		this.bean = bean;
		this.filters = filters;
		return this;
	}

	public abstract void service(HttpServletRequest request, HttpServletResponse response) throws Exception;

	public final String toString() {
		return new StringBuilder(512).append("{bean:").append(bean == null ? null : bean.getClass().getCanonicalName()).append(",filters:").append(filters == null ? null : Arrays.toString(filters)).append("}").toString();
	}

}
>>>>>>> branch 'master' of git@github.com:obase/java.git

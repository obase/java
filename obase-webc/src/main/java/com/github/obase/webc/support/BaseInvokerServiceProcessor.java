package com.github.obase.webc.support;

import java.util.Map;

<<<<<<< HEAD
=======
import javax.servlet.ServletException;
>>>>>>> branch 'master' of git@github.com:obase/java.git
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.obase.WrappedException;
import com.github.obase.webc.InvokerServiceObject;
import com.github.obase.webc.InvokerServiceProcessor;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;

public class BaseInvokerServiceProcessor implements InvokerServiceProcessor {

	protected final Log logger = LogFactory.getLog(getClass());
	protected FilterInitParam params;

	@Override
	public void setup(FilterInitParam params, Map<Class<?>, InvokerServiceObject> rules) {
		this.params = params;
		if (logger.isInfoEnabled()) {
			logger.info("Load service : " + rules.keySet());
		}
	}

	@Override
<<<<<<< HEAD
	public HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, InvokerServiceObject object) throws Throwable {
=======
	public HttpServletRequest process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
>>>>>>> branch 'master' of git@github.com:obase/java.git
		return request;
	}

	@Override
	public void error(HttpServletRequest request, HttpServletResponse response, Throwable t) {
		if (t != null) {

			while (t instanceof WrappedException) {
				t = t.getCause();
			}

			if (logger.isErrorEnabled()) {
				logger.error("Invoke service failed", t);
			}

			if (t instanceof RuntimeException) {
				throw (RuntimeException) t;
			} else {
				throw new WrappedException(t);
			}
		}
	}

}

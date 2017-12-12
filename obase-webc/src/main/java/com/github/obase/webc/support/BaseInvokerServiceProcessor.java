package com.github.obase.webc.support;

import java.util.Collection;

import javax.servlet.ServletException;
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
	public void init(FilterInitParam params) {
		this.params = params;
	}

	@Override
	public void setup(Collection<InvokerServiceObject> rules) throws ServletException {
		if (logger.isInfoEnabled()) {
			logger.info("Setup invoker service : " + rules);
		}
	}

	@Override
	public HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, InvokerServiceObject object) throws Throwable {
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

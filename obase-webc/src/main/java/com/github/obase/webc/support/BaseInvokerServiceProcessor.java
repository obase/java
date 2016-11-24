package com.github.obase.webc.support;

import java.io.IOException;
import java.util.Map;

import javax.servlet.AsyncListener;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.obase.webc.ApplicationException;
import com.github.obase.webc.InvokerServiceObject;
import com.github.obase.webc.InvokerServiceProcessor;
import com.github.obase.webc.Webc;

public class BaseInvokerServiceProcessor implements InvokerServiceProcessor {

	protected final Log logger = LogFactory.getLog(getClass());

	protected AsyncListener asyncListener;
	protected long sessionTimeout = Webc.DEFAULT_SESSION_TIMEOUT;// seconds

	@Override
	public void initMappingRules(FilterConfig filterConfig, Map<String, InvokerServiceObject> rules) {
		StringBuilder sb = new StringBuilder();
		sb.append(filterConfig.getFilterName()).append(" load lookup rules as follows :\n");
		for (Map.Entry<String, InvokerServiceObject> entry : rules.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
		}
		sb.deleteCharAt(sb.length() - 1);
		logger.info(sb.toString());
	}

	@Override
	public HttpServletRequest preprocess(HttpServletRequest request, HttpServletResponse response, InvokerServiceObject object) throws IOException, ServletException {
		return request;
	}

	@Override
	public void postprocess(HttpServletRequest request, HttpServletResponse response, Throwable t) {
		if (t != null) {
			if (t instanceof ApplicationException) {
				throw (ApplicationException) t;
			} else {
				throw new ApplicationException(t);
			}
		}
	}

	@Override
	public AsyncListener getAsyncListener() {
		return asyncListener;
	}

	public void setAsyncListener(AsyncListener asyncListener) {
		this.asyncListener = asyncListener;
	}

	@Override
	public long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

}

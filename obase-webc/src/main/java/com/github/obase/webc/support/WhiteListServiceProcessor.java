package com.github.obase.webc.support;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.obase.webc.InvokerServiceObject;
import com.github.obase.webc.Kits;

public class WhiteListServiceProcessor extends BaseInvokerServiceProcessor {

	protected final Log logger = LogFactory.getLog(getClass());

	protected final Set<String> allows = new HashSet<String>();

	@Override
	public final HttpServletRequest preprocess(HttpServletRequest request, HttpServletResponse response, InvokerServiceObject object) throws IOException, ServletException {
		String clientIp = Kits.getClientIp(request);
		if (allows.contains(clientIp) || "localhost".equals(clientIp) || "127.0.0.1".equals(clientIp)) {
			return request;
		} else {
			return null;
		}
	}

	public void setAllows(Collection<String> allows) {
		this.allows.clear();
		this.allows.addAll(allows);
	}

	public void setAllows(String... allows) {
		this.allows.clear();
		Collections.addAll(this.allows, allows);
	}

}

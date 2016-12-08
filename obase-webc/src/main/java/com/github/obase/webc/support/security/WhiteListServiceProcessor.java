package com.github.obase.webc.support.security;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.webc.InvokerServiceObject;
import com.github.obase.webc.Kits;
import com.github.obase.webc.support.BaseInvokerServiceProcessor;

public class WhiteListServiceProcessor extends BaseInvokerServiceProcessor {

	protected final Set<String> whilteList = new HashSet<String>();

	@Override
	public final HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, InvokerServiceObject object) throws Throwable {
		String clientIp = Kits.getClientIp(request);
		if (whilteList.contains(clientIp) || "localhost".equals(clientIp) || "127.0.0.1".equals(clientIp)) {
			return request;
		} else {
			return null;
		}
	}

	public void setAllows(Collection<String> allows) {
		whilteList.clear();
		whilteList.addAll(allows);
	}

	public void setAllows(String... allows) {
		whilteList.clear();
		Collections.addAll(whilteList, allows);
	}

}

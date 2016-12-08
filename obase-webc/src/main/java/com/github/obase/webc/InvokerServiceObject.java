package com.github.obase.webc;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.github.obase.webc.annotation.InvokerService;

public final class InvokerServiceObject extends HttpInvokerServiceExporter {

	InvokerService annotation;

	public InvokerService getAnnotation() {
		return annotation;
	}

	public void setAnnotation(InvokerService annotation) {
		this.annotation = annotation;
	}

	public final String toString() {
		return new StringBuilder(512).append("{serviceInterface=").append(getServiceInterface()).append(",service=").append(getService()).append("}").toString();
	}
}

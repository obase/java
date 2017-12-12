package com.github.obase.webc;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

public final class InvokerServiceObject extends HttpInvokerServiceExporter {

	String remark; // summary to the service

	String category; // category of the service

	public String getRemark() {
		return remark;
	}

	public String getCategory() {
		return category;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return getServiceInterface().getCanonicalName();
	}
}

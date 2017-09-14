package com.huya.dbms.model;

import com.github.obase.mysql.annotation.Meta;
import com.huya.dbms.entity.Instance;

@Meta
public class InstanceExt extends Instance {

	private String host;

	private String vip;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getVip() {
		return vip;
	}

	public void setVip(String vip) {
		this.vip = vip;
	}

	public Integer getInstanceid() {
		return getId();
	}

	public void setInstanceid(Integer instanceid) {
		setId(instanceid);
	}

}

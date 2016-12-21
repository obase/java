package com.github.obase.mysql.demo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.obase.mysql.annotation.Meta;

@Meta
public class Session implements Serializable {

	private static final long serialVersionUID = 1L;

	String id;
	String domain;
	String project;
	String user;
	Set<String> roles;
	Long effectTime; // 最终有效时间戳

	/* {key:domain,val:service}, 已经policy+principal过滤 */
	final transient Map<String, Group> serviceCatalog = new HashMap<String, Group>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Long getEffectTime() {
		return effectTime;
	}

	public void setEffectTime(Long effectTime) {
		this.effectTime = effectTime;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}

	public Map<String, Group> getServiceCatalog() {
		return serviceCatalog;
	}

}

package com.huya.dbms.entity;

import com.github.obase.mysql.annotation.Column;
import com.github.obase.mysql.annotation.Engine;
import com.github.obase.mysql.annotation.Index;
import com.github.obase.mysql.annotation.Indexes;
import com.github.obase.mysql.annotation.Table;

@Table(engine = Engine.InnoDB)
@Indexes({ @Index(name = "Idx_Instanext_host", columns = "host"), @Index(name = "Idx_Instanext_vip", columns = "vip") })
public class Instanext extends Entity {

	@Column(key = true, notNull = true, comment = "instance table id")
	private Integer instanceid;

	@Column(length = 100, notNull = true, defaultValue = "", comment = "some instance have hostname")
	private String host;

	@Column(length = 15, notNull = true, defaultValue = "", comment = "vip if had")
	private String vip;

	@Column(length = 100, notNull = true, defaultValue = "")
	private String remark;

	@Column(length = 500, notNull = true, defaultValue = "")
	private String description;

	public Integer getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(Integer instanceid) {
		this.instanceid = instanceid;
	}

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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getId() {
		return instanceid;
	}

	public void setId(Integer id) {
		this.instanceid = id;
	}

}

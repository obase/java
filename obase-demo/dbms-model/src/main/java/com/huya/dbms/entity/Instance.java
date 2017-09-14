package com.huya.dbms.entity;

import com.github.obase.mysql.annotation.Column;
import com.github.obase.mysql.annotation.Engine;
import com.github.obase.mysql.annotation.Index;
import com.github.obase.mysql.annotation.Indexes;
import com.github.obase.mysql.annotation.Table;

@Table(engine = Engine.InnoDB)
@Indexes({ @Index(name = "Idx_Instance_name", columns = "name"), @Index(name = "Idx_Instance_ipport", columns = { "ip", "port" }),
		@Index(name = "Idx_Instance_masteripport", columns = { "masterip", "masterport" }) })
public class Instance extends Entity {

	@Column(key = true, notNull = true, autoIncrement = true, comment = "instance id")
	private Integer id;

	@Column(length = 40, notNull = true, comment = "instance name")
	private String name;

	@Column(length = 15, notNull = true, comment = "ip")
	private String ip;

	@Column(notNull = true, comment = "database port")
	private Integer port;

	@Column(length = 1, notNull = true, comment = "instance role:M/master,S/slave")
	private Character role;

	@Column(length = 10, notNull = true, comment = "E/S/C/D:etc/sandbox/cloudmysql/dbms")
	private String type;

	@Column(length = 100, notNull = true, comment = "master dba")
	private String mdba;

	@Column(length = 100, notNull = true, comment = "slave dba")
	private String sdba;

	@Column(length = 20, notNull = true, comment = "instance\\'s location")
	private String idc;

	@Column(length = 10, notNull = true, comment = "department")
	private String dep;

	@Column(notNull = true, comment = "whether test server")
	private Byte istest;

	@Column(notNull = true, comment = "whether backup per day")
	private Byte isbackup;

	@Column(length = 15, notNull = true, defaultValue = "", comment = "ip")
	private String masterip;

	@Column(comment = "database port")
	private Integer masterport;

	@Column(length = 100, notNull = true, defaultValue = "")
	private String remark;

	@Column(length = 500, notNull = true, defaultValue = "")
	private String description;
	
	@Column(comment = "primary project id if have")
	private Integer projectid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Character getRole() {
		return role;
	}

	public void setRole(Character role) {
		this.role = role;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMdba() {
		return mdba;
	}

	public void setMdba(String mdba) {
		this.mdba = mdba;
	}

	public String getSdba() {
		return sdba;
	}

	public void setSdba(String sdba) {
		this.sdba = sdba;
	}

	public String getIdc() {
		return idc;
	}

	public void setIdc(String idc) {
		this.idc = idc;
	}

	public String getDep() {
		return dep;
	}

	public void setDep(String dep) {
		this.dep = dep;
	}

	public Byte getIstest() {
		return istest;
	}

	public void setIstest(Byte istest) {
		this.istest = istest;
	}

	public Byte getIsbackup() {
		return isbackup;
	}

	public void setIsbackup(Byte isbackup) {
		this.isbackup = isbackup;
	}

	public String getMasterip() {
		return masterip;
	}

	public void setMasterip(String masterip) {
		this.masterip = masterip;
	}

	public Integer getMasterport() {
		return masterport;
	}

	public void setMasterport(Integer masterport) {
		this.masterport = masterport;
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

	public Integer getProjectid() {
		return projectid;
	}

	public void setProjectid(Integer projectid) {
		this.projectid = projectid;
	}

}

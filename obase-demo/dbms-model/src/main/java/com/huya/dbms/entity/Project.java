package com.huya.dbms.entity;

import com.github.obase.mysql.annotation.Column;
import com.github.obase.mysql.annotation.Engine;
import com.github.obase.mysql.annotation.Index;
import com.github.obase.mysql.annotation.Indexes;
import com.github.obase.mysql.annotation.Table;

@Table(engine = Engine.InnoDB)
@Indexes({ @Index(name = "Idx_Project_projectcode", columns = "projectcode"), @Index(name = "Idx_Project_name", columns = "name") })
public class Project extends Entity {

	@Column(key = true, notNull = true, autoIncrement = true, comment = "项目主键")
	private Integer id;

	@Column(length = 20, notNull = true, defaultValue = "", comment = "项目类型")
	private String projecttype;

	@Column(length = 100, unique = true, notNull = true, defaultValue = "", comment = "项目代号")
	private String projectcode;

	@Column(length = 100, notNull = true, defaultValue = "", comment = "项目名称")
	private String name;

	@Column(length = 200, notNull = true, defaultValue = "", comment = "管理员")
	private String admin;

	@Column(length = 400, notNull = true, defaultValue = "", comment = "普通用户")
	private String member;

	@Column(length = 400, notNull = true, defaultValue = "", comment = "运维人员")
	private String op;

	@Column(length = 200, notNull = true, defaultValue = "", comment = "项目源码位置")
	private String scm;

	@Column(length = 100, notNull = true, defaultValue = "", comment = "备注")
	private String remark;

	@Column(length = 500, notNull = true, defaultValue = "", comment = "详情")
	private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProjecttype() {
		return projecttype;
	}

	public void setProjecttype(String projecttype) {
		this.projecttype = projecttype;
	}

	public String getProjectcode() {
		return projectcode;
	}

	public void setProjectcode(String projectcode) {
		this.projectcode = projectcode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getScm() {
		return scm;
	}

	public void setScm(String scm) {
		this.scm = scm;
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

}

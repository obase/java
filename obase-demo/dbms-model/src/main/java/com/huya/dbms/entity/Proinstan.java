package com.huya.dbms.entity;

import com.github.obase.mysql.annotation.Column;
import com.github.obase.mysql.annotation.Engine;
import com.github.obase.mysql.annotation.Index;
import com.github.obase.mysql.annotation.Indexes;
import com.github.obase.mysql.annotation.Table;

@Table(engine = Engine.InnoDB)
@Indexes({ @Index(name = "Idx_Proinstan_projectid", columns = "projectid"), @Index(name = "Idx_Proinstan_instanceid", columns = "instanceid"),
		@Index(name = "Idx_Proinstan_dbname", columns = "dbname") })
public class Proinstan extends Entity {

	@Column(key = true, notNull = true, autoIncrement = true, comment = "id")
	private Integer id;

	@Column(notNull = true, comment = "project table id")
	private Integer projectid;

	@Column(notNull = true, comment = "instance table id")
	private Integer instanceid;

	@Column(length = 100, notNull = true, defaultValue = "", comment = "some old project mapped dbname")
	private String dbname;

	@Column(length = 100, notNull = true, defaultValue = "")
	private String remark;

	@Column(length = 500, notNull = true, defaultValue = "")
	private String description;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProjectid() {
		return projectid;
	}

	public void setProjectid(Integer projectid) {
		this.projectid = projectid;
	}

	public Integer getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(Integer instanceid) {
		this.instanceid = instanceid;
	}

	public String getDbname() {
		return dbname;
	}

	public void setDbname(String dbname) {
		this.dbname = dbname;
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

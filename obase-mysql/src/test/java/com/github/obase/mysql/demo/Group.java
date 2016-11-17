package com.github.obase.mysql.demo;

import java.util.Date;

import com.github.obase.mysql.annotation.Column;
import com.github.obase.mysql.annotation.Engine;
import com.github.obase.mysql.annotation.ForeignKey;
import com.github.obase.mysql.annotation.Index;
import com.github.obase.mysql.annotation.Indexes;
import com.github.obase.mysql.annotation.PrimaryKey;
import com.github.obase.mysql.annotation.Reference;
import com.github.obase.mysql.annotation.Table;
import com.github.obase.mysql.annotation.Using;

@Table(name = "Group1", characterSet = "UTF8", engine = Engine.MyISAM, comment = "this is a test")
@Indexes(@Index(name = "UK_createBy", columns = "createBy", using = Using.HASH))
@ForeignKey(@Reference(name = "FK_createBy", columns = "createBy", targetTable = "test2", targetColumns = "id"))
@PrimaryKey(columns = "id")
public class Group extends User {

	@Column(key = true)
	private Long id;

	@Column(name = "name", length = 128, notNull = true)
	private String name3;

	@Column
	private Integer category;

	@Column
	private String remark;

	@Column
	private String createBy;

	@Column
	private Date createTime;

	public String getName3() {
		return name3;
	}

	public void setName3(String name3) {
		this.name3 = name3;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}

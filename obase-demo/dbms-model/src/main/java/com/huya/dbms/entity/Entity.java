package com.huya.dbms.entity;

import java.util.Date;

import com.github.obase.mysql.annotation.Column;
import com.github.obase.mysql.annotation.OptimisticLock;

@OptimisticLock(column = "version")
public abstract class Entity {

	@Column(defaultValue = "0", comment = "版本控制,用于乐观锁")
	private Long version;

	@Column(length = 36, comment = "记录创建者")
	private String createBy;

	@Column(comment = "记录创建时间")
	private Date createTime;

	@Column(length = 36, comment = "最后修改者")
	private String modifyBy;

	@Column(comment = "记录修改时间")
	private Date modifyTime;

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getModifyBy() {
		return modifyBy;
	}

	public void setModifyBy(String modifyBy) {
		this.modifyBy = modifyBy;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

}

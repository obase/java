package com.huya.dbms.entity;

import com.github.obase.mysql.annotation.Column;
import com.github.obase.mysql.annotation.Engine;
import com.github.obase.mysql.annotation.Table;

@Table(engine = Engine.InnoDB)
public class Admin extends Entity {

	@Column(length = 32, notNull = true, key = true, comment = "YY通行证")
	private String id;

	@Column(length = 32, comment = "实名")
	private String realname;

	@Column(length = 32, comment = "邮箱")
	private String email;

	@Column(length = 16, comment = "手机")
	private String phone;

	@Column(defaultValue = "0", comment = "用户等级,7:管理员,1:用户,0:游客")
	private int level;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}

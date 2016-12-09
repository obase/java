package com.github.obase.webc.security;

public class SimplePrincipal implements Principal {

	private static final long serialVersionUID = 1L;

	private String yyuid;
	private String passport;
	private String realname;
	private String nickname;
	private String deptname;
	private String email;
	private String phone;
	private String jobCode;
	private int level;

	public String getYyuid() {
		return yyuid;
	}

	public void setYyuid(String yyuid) {
		this.yyuid = yyuid;
	}

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getDeptname() {
		return deptname;
	}

	public void setDeptname(String deptname) {
		this.deptname = deptname;
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

	public String getJobCode() {
		return jobCode;
	}

	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}

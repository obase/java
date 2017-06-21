package com.github.obase.webc.yy;

import com.github.obase.kit.StringKit.Join;
import com.github.obase.kit.StringKit.Split;
import com.github.obase.security.Principal;

public class UserPrincipal implements Principal {

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

	@Override
	public String encode() {
		Join j = Join.build('\001');
		j.join(nvlv(passport));
		j.join(nvlv(realname));
		j.join(nvlv(nickname));
		j.join(nvlv(deptname));
		j.join(nvlv(email));
		j.join(nvlv(phone));
		j.join(nvlv(jobCode));
		j.join(Integer.toString(level));
		return j.toString();
	}

	@Override
	public Principal decode(String text) {
		Split s = Split.wrap(text, '\001');
		passport = s.next();
		realname = s.next();
		nickname = s.next();
		deptname = s.next();
		email = s.next();
		phone = s.next();
		jobCode = s.next();
		level = Integer.parseInt(s.next());
		return this;
	}

	private String nvlv(String val) {
		return val == null ? "" : val;
	}
}

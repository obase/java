package com.github.obase.webc.support.security;

import java.io.Serializable;

public interface Principal extends Serializable {

	String getYyuid();

	String getPassport();

	String getRealname();

	String getNickname();

	String getDeptname();

	String getEmail();

	String getPhone();

	String getJobCode();

	int getLevel(); // user level

}

package com.huya.dbms.controller.sys;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.github.obase.kit.StringKit;
import com.github.obase.webc.ServletMethodFilter;
import com.github.obase.webc.annotation.ServletMethod;

@Component
public class AdminFilter implements ServletMethodFilter {

	@Override
	public boolean process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return true;
	}

	@Override
	public boolean matches(String lookupPath, Class<?> targetClass, String methodName, ServletMethod annotation) {
		return targetClass == AdminController.class && StringKit.equals("ADMIN", annotation.category());
	}

	@Override
	public int getOrder() {
		return 0;
	}

}

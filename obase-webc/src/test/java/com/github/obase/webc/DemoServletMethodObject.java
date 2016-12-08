package com.github.obase.webc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于ASM加速的demo类
 */
<<<<<<< HEAD
public class DemoServletMethodObject extends ServletMethodHandler {
=======
public class DemoServletMethodObject extends ServletMethodObject {
>>>>>>> branch 'master' of git@github.com:obase/java.git

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
		for (ServletMethodFilter filter : filters) {
			if (!filter.process(request, response)) {
				return;
			}
		}
		((DemoController) bean).test(request, response);
	}

}

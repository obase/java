package com.github.obase.webc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于ASM加速的demo类
 */
public class DemoServletMethodObject extends ServletMethodObject {

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

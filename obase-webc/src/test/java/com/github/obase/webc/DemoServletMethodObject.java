package com.github.obase.webc;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
public class DemoServletMethodObject extends ServletMethodHandler {
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

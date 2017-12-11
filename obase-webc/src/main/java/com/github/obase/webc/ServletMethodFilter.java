package com.github.obase.webc;

import java.util.Comparator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.obase.webc.annotation.ServletMethod;

public interface ServletMethodFilter {

	boolean process(HttpServletRequest request, HttpServletResponse response) throws Exception;

	boolean matches(Class<?> targetClass, String methodName, ServletMethod annotation, String lookupPath);

	int getOrder();

	ServletMethodFilter[] EMPTY_ARRAY = new ServletMethodFilter[0];
	Comparator<ServletMethodFilter> OrderBy = new Comparator<ServletMethodFilter>() {
		@Override
		public int compare(ServletMethodFilter o1, ServletMethodFilter o2) {
			if (o1 == o2) {
				return 0;
			} else {
				int d1 = o1.getOrder();
				int d2 = o2.getOrder();
				if (d1 < d2) {
					return -1;
				} else if (d1 > d2) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	};

}

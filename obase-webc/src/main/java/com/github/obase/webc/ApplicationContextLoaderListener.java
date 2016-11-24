package com.github.obase.webc;

import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ApplicationContextLoaderListener extends ContextLoaderListener {

	boolean inited;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		ApplicationContext appctx = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
		if (appctx == null) {
			inited = true;
			super.contextInitialized(event);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (inited) {
			super.contextDestroyed(event);
		}
	}

}

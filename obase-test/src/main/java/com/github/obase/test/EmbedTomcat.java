package com.github.obase.test;

import java.io.File;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.naming.resources.VirtualDirContext;

public final class EmbedTomcat extends SpringJUnitTester {

	public static final String WEBAPP_DIRECTORY = "src/main/webapp/";
	public static final String ROOT_CONTEXT = "";
	public static final int HTTP_PORT = 80;
	public static final int HTTPS_PORT = 443;
	public static final int OFF = -1;

	public static void start() {
		start(ROOT_CONTEXT, HTTP_PORT, OFF, null, null, null);
	}

	public static void start(int httpPort) {
		start(ROOT_CONTEXT, httpPort, OFF, null, null, null);
	}

	public static void start(String keyAlias, String password, String keystorePath) {
		start(ROOT_CONTEXT, OFF, HTTPS_PORT, keyAlias, password, keystorePath);
	}

	public static void start(int httpsPort, String keyAlias, String password, String keystorePath) {
		start(ROOT_CONTEXT, OFF, httpsPort, keyAlias, password, keystorePath);
	}

	public static void start(int httpPort, int httpsPort, String keyAlias, String password, String keystorePath) {
		start(ROOT_CONTEXT, httpPort, httpsPort, keyAlias, password, keystorePath);
	}

	public static void start(String contextPath, int httpPort, int httpsPort, String keyAlias, String password, String keystorePath) {

		// FIX: A context path must either be an empty string or start with a '/' and do not end with a '/'.
		if (contextPath == null || contextPath.equals("/")) {
			contextPath = ROOT_CONTEXT;
		}
		try {
			// initial
			processSystemEnvironment();

			Tomcat tomcat = new Tomcat();

			if (httpsPort > 0) {
				tomcat.setPort(httpsPort);
				Connector connector = tomcat.getConnector();
				connector.setProtocol(Http11NioProtocol.class.getCanonicalName()); // set nio, default is bio
				connector.setSecure(true);
				connector.setScheme("https");
				connector.setAttribute("keyAlias", keyAlias);
				connector.setAttribute("keystorePass", password);
				connector.setAttribute("keystoreFile", keystorePath);
				connector.setAttribute("clientAuth", "false");
				connector.setAttribute("sslProtocol", "TLS");
				connector.setAttribute("SSLEnabled", true);

				if (httpPort > 0) {
					connector = new Connector(Http11NioProtocol.class.getCanonicalName());
					connector.setPort(httpPort);
					connector.setRedirectPort(httpsPort);
					tomcat.getService().addConnector(connector);
				}
			} else if (httpPort > 0) {
				tomcat.setPort(httpPort);
			} else {
				throw new IllegalArgumentException("Both port are invalid");
			}

			File webapp = new File(WEBAPP_DIRECTORY);
			if (webapp.exists()) {
				StandardContext ctx = (StandardContext) tomcat.addWebapp(contextPath, webapp.getAbsolutePath());
				// Declare an alternative location for your "WEB-INF/classes" dir Servlet 3.0 annotation will work
				VirtualDirContext resources = new VirtualDirContext();
				resources.setExtraResourcePaths("/WEB-INF/classes=" + new File("target/classes").getAbsolutePath() + ',' + new File("target/test-classes").getAbsolutePath());
				ctx.setResources(resources);
				ctx.setDefaultWebXml(new File("src/main/webapp/WEB-INF/web.xml").getAbsolutePath());
				for (LifecycleListener ll : ctx.findLifecycleListeners()) {
					if (ll instanceof ContextConfig) {
						((ContextConfig) ll).setDefaultWebXml(ctx.getDefaultWebXml());
					}
				}
			}

			tomcat.start();
			tomcat.getServer().await();
		} catch (Exception e) {
			throw new RuntimeException("tomcat launch failed", e);
		}
	}

}

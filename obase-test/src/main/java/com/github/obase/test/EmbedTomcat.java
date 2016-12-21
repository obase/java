package com.github.obase.test;

import java.io.File;

import org.apache.catalina.LifecycleListener;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.EmptyResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.apache.tomcat.util.scan.StandardJarScanner;

public final class EmbedTomcat extends SpringJUnitTester {

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

	// FOR Tomcat 8.x
	public static void start(String contextPath, int httpPort, int httpsPort, String keyAlias, String password, String keystorePath) {

		processSystemEnvironment();

		final File ROOT = new File("./");
		final File WEBAPP = new File(ROOT, "src/main/webapp/");
		final File WEBXML = new File(ROOT, "src/main/webapp/WEB-INF/web.xml");
		final File TARGET_CLASSES = new File(ROOT, "target/classes/");
		final File TARGET_TEST_CLASSES = new File(ROOT, "target/test-classes");

		// FIX: A context path must either be an empty string or start with a '/' and do not end with a '/'.
		if (contextPath == null || contextPath.equals("/")) {
			contextPath = ROOT_CONTEXT;
		}
		try {

			Tomcat tomcat = new Tomcat();

			if (httpsPort > 0) {
				tomcat.setPort(httpsPort);
				Connector connector = new Connector(Http11Nio2Protocol.class.getCanonicalName());
				connector.setSecure(true);
				connector.setScheme("https");
				connector.setAttribute("keyAlias", keyAlias);
				connector.setAttribute("keystorePass", password);
				connector.setAttribute("keystoreFile", keystorePath);
				connector.setAttribute("clientAuth", "false");
				connector.setAttribute("sslProtocol", "TLS");
				connector.setAttribute("SSLEnabled", true);
				tomcat.setConnector(connector);

				if (httpPort > 0) {
					connector = new Connector(Http11Nio2Protocol.class.getCanonicalName());
					connector.setPort(httpPort);
					connector.setRedirectPort(httpsPort);
					tomcat.getService().addConnector(connector);
				}

			} else if (httpPort > 0) {

				tomcat.setPort(httpPort);
				Connector connector = new Connector(Http11Nio2Protocol.class.getCanonicalName());
				tomcat.setConnector(connector);

			} else {
				throw new IllegalArgumentException("Both port are invalid");
			}

			StandardContext ctx;
			if (WEBAPP.exists()) {
				ctx = (StandardContext) tomcat.addWebapp(contextPath, WEBAPP.getAbsolutePath());
				if (WEBXML.exists()) {
					ctx.setDefaultWebXml(WEBXML.getAbsolutePath());
				}
			} else {
				ctx = (StandardContext) tomcat.addWebapp(contextPath, ROOT.getAbsolutePath());
			}
			ctx.setParentClassLoader(EmbedTomcat.class.getClassLoader());

			/* FIX: No global web.xml */
			for (LifecycleListener ll : ctx.findLifecycleListeners()) {
				if (ll instanceof ContextConfig) {
					((ContextConfig) ll).setDefaultWebXml(null);
				}
			}

			/* FIX: scanner bug */
			((StandardJarScanner) ctx.getJarScanner()).setScanManifest(false);

			WebResourceRoot resources = new StandardRoot(ctx);
			boolean targetClassesExists = TARGET_CLASSES.exists(), targetTestClassesExists = TARGET_TEST_CLASSES.exists();
			if (targetClassesExists || targetTestClassesExists) {
				if (targetClassesExists) {
					resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", TARGET_CLASSES.getAbsolutePath(), "/"));
				}
				if (targetTestClassesExists) {
					resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", TARGET_TEST_CLASSES.getAbsolutePath(), "/"));
				}
			} else {
				resources.addPreResources(new EmptyResourceSet(resources));
			}
			ctx.setResources(resources);

			tomcat.start();
			tomcat.getServer().await();
		} catch (Exception e) {
			throw new RuntimeException("EmbedTomcat start failed", e);
		}
	}

}

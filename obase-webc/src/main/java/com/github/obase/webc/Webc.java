package com.github.obase.webc;

import static org.springframework.asm.Opcodes.AALOAD;
import static org.springframework.asm.Opcodes.ACC_PUBLIC;
import static org.springframework.asm.Opcodes.ACC_SUPER;
import static org.springframework.asm.Opcodes.ALOAD;
import static org.springframework.asm.Opcodes.ARRAYLENGTH;
import static org.springframework.asm.Opcodes.ASTORE;
import static org.springframework.asm.Opcodes.CHECKCAST;
import static org.springframework.asm.Opcodes.DUP;
import static org.springframework.asm.Opcodes.GETFIELD;
import static org.springframework.asm.Opcodes.GOTO;
import static org.springframework.asm.Opcodes.ICONST_0;
import static org.springframework.asm.Opcodes.IFNE;
import static org.springframework.asm.Opcodes.IF_ICMPLT;
import static org.springframework.asm.Opcodes.ILOAD;
import static org.springframework.asm.Opcodes.INVOKEINTERFACE;
import static org.springframework.asm.Opcodes.INVOKESPECIAL;
import static org.springframework.asm.Opcodes.INVOKEVIRTUAL;
import static org.springframework.asm.Opcodes.ISTORE;
import static org.springframework.asm.Opcodes.RETURN;

import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.Label;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.support.ServletContextResource;

import com.github.obase.kit.StringKit;

/**
 * All the constants and utilities methods
 *
 */
public interface Webc {

	String CONTENT_TYPE_PLAIN = "text/plain; charset=utf-8";
	String CONTENT_TYPE_JSON = "application/json; charset=utf-8";
	String CONTENT_TYPE_HTML = "text/html; charset=utf-8";
	String CONTENT_TYPE_XML = "text/xml; charset=utf-8";
	String CHARSET_NAME = "UTF-8";
	Charset CHARSET = Charset.forName(CHARSET_NAME);
	String $ = "$"; // ignore method part
	int BUILDER_CAPACITY = 2048;
	char COMMA = ',';
	char COLON = ':';
	char LAND = '&';
	char EQUA = '=';

	String DEFAULT_CONFIG_LOCATION = "/WEB-INF/webc.xml";
	String DEFAULT_CONFIG_LOCATION2 = "/META-INF/webc.xml";
	String DEFAULT_CONTEXT_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";
	String DEFAULT_CONTEXT_CONFIG_LOCATION2 = "/META-INF/applicationContext.xml";
	String DEFAULT_SERVLET_CONTEXT_CONFIG_LOCATION = "/WEB-INF/servletContext.xml";
	String DEFAULT_SERVLET_CONTEXT_CONFIG_LOCATION2 = "/META-INF/servletContext.xml";
	String DEFAULT_SERVICE_CONTEXT_CONFIG_LOCATION = "/WEB-INF/serviceContext.xml";
	String DEFAULT_SERVICE_CONTEXT_CONFIG_LOCATION2 = "/META-INF/serviceContext.xml";
	String DEFAULT_NAMESPACE_FOR_SERVLET = null;
	String DEFAULT_NAMESPACE_FOR_SERVICE = "service";
	int DEFAULT_TIMEOUT_SECOND = 60 * 60; // default 1 hour timeout

	String DEFAULT_CONTROL_PREFIX = "controller"; // @Since: 1.2.0, delete the last '.'
	String DEFAULT_CONTROL_SUFFIX = "Controller";
	int DEFAULT_WSID_TOKEN_BASE = 49999;
	AuthType DEFAULT_AUTH_TYPE = AuthType.PERMISSION;

	String INVOKER_SERVICE_PREFIX = "HttpInvokerServiceExporter$";
	String ATTR_WSID = "$_WSID";
	String ATTR_PRINCIPAL = "$_PRINCIPAL";
	String ATTR_NAMESPACE = "$_NAMESPACE";
	String ATTR_LOOKUP_PATH = "$_LOOKUP_PATH";
	String GLOBAL_ATTRIBUTE_PREFFIX = "$_";

	int SC_OK = 200;
	int SC_OK_EVEN_ERROR = 210; // FIXBUG: don use 200~209, which RFC take
	int SC_SERVER_ERROR = 500;

	int SC_PERMISSION_DENIED = 601;
	int SC_SESSION_TIMEOUT = 602;
	int SC_MISSING_TOKEN = 603;
	int SC_MISSING_VERIFIER = 604;
	int SC_INVALID_ACCOUNT = 605;
	int SC_INVALID_ACCESS = 606;

	int SC_FILE_UPLOAD_FAILED = 620;
	int SC_UPLOAD_SIZE_EXCEEDED = 621;

	class Util {

		public static String getUrlPatternForNamespace(String namespace) {
			if (StringKit.isEmpty(namespace)) {
				return "/*";
			}
			StringBuilder sb = new StringBuilder(namespace.length() + 4);
			return sb.append('/').append(namespace).append("/*").toString();
		}

		public static <T> T findWebcBean(ApplicationContext appctx, Class<T> requiredType, String name) {

			T bean = null;

			if (StringKit.isNotEmpty(name)) {
				bean = appctx.getBean(name, requiredType);
			} else {
				try {
					bean = appctx.getBean(requiredType);
				} catch (NoSuchBeanDefinitionException e) {
					Map<String, T> beans = appctx.getBeansOfType(requiredType);
					if (beans.size() > 0) {
						bean = beans.values().iterator().next();
					}
				}
			}

			return bean;
		}

		public static Resource getDefaultConfigResource(ServletContext servletContext, String servletpath, Class<?> clazz, String classpath) {
			ServletContextResource scr = new ServletContextResource(servletContext, servletpath);
			if (scr.exists()) {
				return scr;
			}
			ClassPathResource cpr = new ClassPathResource(classpath, clazz);
			if (cpr.exists()) {
				return cpr;
			}
			return null;
		}

		public static String getDefaultConfigLocation(ServletContext servletContext, String servletpath, Class<?> clazz, String classpath) {
			ServletContextResource scr = new ServletContextResource(servletContext, servletpath);
			if (scr.exists()) {
				return servletpath;
			}
			ClassPathResource cpr = new ClassPathResource(classpath, clazz);
			if (cpr.exists()) {
				return ResourceUtils.CLASSPATH_URL_PREFIX + classpath;
			}
			return null;
		}

		static byte[] dumpServletMethodObject(String className, Class<?> targetClass, String targetMethod) {

			String internalName = className.replace('.', '/');
			String descriptor = 'L' + internalName + ';';

			ClassWriter cw = new ClassWriter(0);
			MethodVisitor mv;
			cw.visit(MajorJavaVersion, ACC_PUBLIC + ACC_SUPER, internalName, null, Type.getInternalName(ServletMethodHandler.class), null);
			{
				mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getType(void.class)), null, null);
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitLineNumber(4, l0);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(ServletMethodHandler.class), "<init>", Type.getMethodDescriptor(Type.getType(void.class)), false);
				mv.visitInsn(RETURN);
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitLocalVariable("this", descriptor, null, l0, l1, 0);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			{
				mv = cw.visitMethod(ACC_PUBLIC, "service", Type.getMethodDescriptor(Type.getType(void.class), Type.getType(HttpServletRequest.class), Type.getType(HttpServletResponse.class)), null, new String[] { Type.getInternalName(Exception.class) });
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitLineNumber(7, l0);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, internalName, "filters", Type.getDescriptor(ServletMethodFilter[].class));
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, 6);
				mv.visitInsn(ARRAYLENGTH);
				mv.visitVarInsn(ISTORE, 5);
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 4);
				Label l1 = new Label();
				mv.visitJumpInsn(GOTO, l1);
				Label l2 = new Label();
				mv.visitLabel(l2);
				mv.visitFrame(Opcodes.F_FULL, 7, new Object[] { internalName, Type.getInternalName(HttpServletRequest.class), Type.getInternalName(HttpServletResponse.class), Opcodes.TOP, Opcodes.INTEGER, Opcodes.INTEGER, Type.getDescriptor(ServletMethodFilter[].class) }, 0, new Object[] {});
				mv.visitVarInsn(ALOAD, 6);
				mv.visitVarInsn(ILOAD, 4);
				mv.visitInsn(AALOAD);
				mv.visitVarInsn(ASTORE, 3);
				Label l3 = new Label();
				mv.visitLabel(l3);
				mv.visitLineNumber(8, l3);
				mv.visitVarInsn(ALOAD, 3);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(ServletMethodFilter.class), "process", Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(HttpServletRequest.class), Type.getType(HttpServletResponse.class)), true);

				Label l4 = new Label();
				mv.visitJumpInsn(IFNE, l4);
				Label l5 = new Label();
				mv.visitLabel(l5);
				mv.visitLineNumber(9, l5);
				mv.visitInsn(RETURN);
				mv.visitLabel(l4);
				mv.visitLineNumber(7, l4);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitIincInsn(4, 1);
				mv.visitLabel(l1);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ILOAD, 4);
				mv.visitVarInsn(ILOAD, 5);
				mv.visitJumpInsn(IF_ICMPLT, l2);
				Label l6 = new Label();
				mv.visitLabel(l6);
				mv.visitLineNumber(12, l6);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, internalName, "bean", Type.getDescriptor(Object.class));
				mv.visitTypeInsn(CHECKCAST, Type.getInternalName(targetClass));
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(targetClass), targetMethod, Type.getMethodDescriptor(Type.getType(void.class), Type.getType(HttpServletRequest.class), Type.getType(HttpServletResponse.class)), false);
				Label l7 = new Label();
				mv.visitLabel(l7);
				mv.visitLineNumber(13, l7);
				mv.visitInsn(RETURN);
				Label l8 = new Label();
				mv.visitLabel(l8);
				mv.visitLocalVariable("this", descriptor, null, l0, l8, 0);
				mv.visitLocalVariable("request", Type.getDescriptor(HttpServletRequest.class), null, l0, l8, 1);
				mv.visitLocalVariable("response", Type.getDescriptor(HttpServletResponse.class), null, l0, l8, 2);
				mv.visitLocalVariable("filter", Type.getDescriptor(ServletMethodFilter.class), null, l3, l4, 3);
				mv.visitMaxs(3, 7);
				mv.visitEnd();
			}
			cw.visitEnd();

			return cw.toByteArray();
		}

		static final int MajorJavaVersion;
		static {
			String javaVersion = System.getProperty("java.version");
			if (javaVersion.contains("1.9.")) {
				MajorJavaVersion = Opcodes.V1_8;
			} else if (javaVersion.contains("1.8.")) {
				MajorJavaVersion = Opcodes.V1_8;
			} else if (javaVersion.contains("1.7.")) {
				MajorJavaVersion = Opcodes.V1_7;
			} else if (javaVersion.contains("1.6.")) {
				MajorJavaVersion = Opcodes.V1_6;
			} else {
				MajorJavaVersion = Opcodes.V1_6;
			}
		}

		public static String extractDomainFromUrl(String url, boolean includePort) {
			if (StringKit.isEmpty(url)) {
				return null;
			}

			int pos1 = url.indexOf("://");
			if (pos1 == -1) {
				pos1 = 0;
			} else {
				pos1 += 3;
			}

			int pos2 = url.indexOf('/', pos1);
			int pos3 = includePort ? -1 : url.indexOf(':', pos1);

			if (pos2 == -1) {
				if (pos3 == -1) {
					return pos1 == 0 ? url : url.substring(pos1);
				} else {
					return url.substring(pos1, pos3);
				}
			} else {
				if (pos3 == -1) {
					return url.substring(pos1, pos2);
				} else {
					return url.substring(pos1, pos2 < pos3 ? pos2 : pos3);
				}
			}
		}

	}

}

package com.github.obase.webc;

import static org.springframework.asm.Opcodes.AALOAD;
import static org.springframework.asm.Opcodes.ACC_PUBLIC;
import static org.springframework.asm.Opcodes.ACC_SUPER;
import static org.springframework.asm.Opcodes.ALOAD;
import static org.springframework.asm.Opcodes.ARRAYLENGTH;
import static org.springframework.asm.Opcodes.CHECKCAST;
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

import java.lang.reflect.Method;
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
	String $$ = "$$"; // ignore controller part
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
	int DEFAULT_ASYNC_TIMEOUT = 60 * 60 * 1000; // default 1 hour timeout
	int DEFAULT_SESSION_TIMEOUT = 30 * 60 * 1000;

	String DEFAULT_CONTROLLER_PREFIX = "controller.";
	String DEFAULT_CONTROLLER_SUFFIX = "Controller";
	byte[] DEFAULT_CSRF_SECRET_BYTES = "zaqx!@#".getBytes();

	String INVOKER_SERVICE_PREFIX = "HttpInvokerServiceExporter$";
	String ATTR_HTTP_METHOD = "$_HTTP_METHOD";
	String ATTR_WSID = "$_WSID";
	String ATTR_PRINCIPAL = "$_PRINCIPAL";
	String ATTR_NAMESPACE = "$_NAMESPACE";
	String GLOBAL_ATTRIBUTE_PREFFIX = "$_";

	int ERRNO_UNKNOWN_ERROR = -1;
	int ERRNO_PERMISSION_DENIED = 602;
	int ERRNO_CSRF_ERROR = 603;
	int ERRNO_SESSION_TIMEOUT = 604;
	int ERRNO_MISSING_TOKEN = 605;
	int ERRNO_MISSING_VERIFIER = 606;
	int ERRNO_INVALID_ACCOUNT = 607;

	int ERRNO_FILE_UPLOAD_FAILED = 620;
	int ERRNO_UPLOAD_SIZE_EXCEEDED = 621;

	class Util {

		public static String getUrlPatternForNamespace(String namespace) {
			if (StringKit.isEmpty(namespace)) {
				return "/*";
			}
			StringBuilder sb = new StringBuilder(namespace.length() + 4);
			return sb.append('/').append(namespace).append("/*").toString();
		}

		public static <T> T findBean(ApplicationContext appctx, Class<T> requiredType, String name) {
			Map<String, T> beans = appctx.getBeansOfType(requiredType);
			if (beans.size() > 0) {
				if (name == null) {
					return beans.values().iterator().next();
				} else {
					return beans.get(name);
				}
			}
			return null;
		}

		public static Resource getDefaultConfigResource(ServletContext servletContext, String servletpath,
				Class<?> clazz, String classpath) {
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

		public static String getDefaultConfigLocation(ServletContext servletContext, String servletpath, Class<?> clazz,
				String classpath) {
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

		static byte[] dumpServletMethodObject(String className, Class<?> targetClass, Method targetMethod) {

			String internalName = className.replace('.', '/');
			String descriptor = 'L' + internalName + ';';

			ClassWriter cw = new ClassWriter(0);
			MethodVisitor mv;

			cw.visit(MajorJavaVersion, ACC_PUBLIC + ACC_SUPER, internalName, null,
					Type.getInternalName(ServletMethodObject.class), null);
			{
				mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getType(void.class)), null,
						null);
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitLineNumber(2, l0);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(ServletMethodObject.class), "<init>",
						Type.getMethodDescriptor(Type.getType(void.class)), false);
				mv.visitInsn(RETURN);
				Label l1 = new Label();
				mv.visitLabel(l1);
				mv.visitLocalVariable("this", descriptor, null, l0, l1, 0);
				mv.visitMaxs(1, 1);
				mv.visitEnd();
			}
			{
				mv = cw.visitMethod(ACC_PUBLIC, "service",
						Type.getMethodDescriptor(Type.getType(void.class), Type.getType(HttpServletRequest.class),
								Type.getType(HttpServletResponse.class)),
						null, new String[] { Type.getInternalName(Exception.class) });
				mv.visitCode();
				Label l0 = new Label();
				mv.visitLabel(l0);
				mv.visitLineNumber(4, l0);
				mv.visitInsn(ICONST_0);
				mv.visitVarInsn(ISTORE, 3);
				Label l1 = new Label();
				mv.visitLabel(l1);
				Label l2 = new Label();
				mv.visitJumpInsn(GOTO, l2);
				Label l3 = new Label();
				mv.visitLabel(l3);
				mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { Opcodes.INTEGER }, 0, null);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, internalName, "filters", Type.getDescriptor(ServletMethodFilter[].class));
				mv.visitVarInsn(ILOAD, 3);
				mv.visitInsn(AALOAD);
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(ServletMethodFilter.class), "process",
						Type.getMethodDescriptor(Type.getType(boolean.class), Type.getType(HttpServletRequest.class),
								Type.getType(HttpServletResponse.class)),
						true);
				Label l4 = new Label();
				mv.visitJumpInsn(IFNE, l4);
				mv.visitInsn(RETURN);
				mv.visitLabel(l4);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitIincInsn(3, 1);
				mv.visitLabel(l2);
				mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
				mv.visitVarInsn(ILOAD, 3);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, internalName, "filters", Type.getDescriptor(ServletMethodFilter[].class));
				mv.visitInsn(ARRAYLENGTH);
				mv.visitJumpInsn(IF_ICMPLT, l3);
				Label l5 = new Label();
				mv.visitLabel(l5);
				mv.visitLineNumber(5, l5);
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, internalName, "bean", Type.getDescriptor(Object.class));
				mv.visitTypeInsn(CHECKCAST, Type.getInternalName(targetClass));
				mv.visitVarInsn(ALOAD, 1);
				mv.visitVarInsn(ALOAD, 2);
				mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(targetClass), targetMethod.getName(),
						Type.getMethodDescriptor(targetMethod), false);
				Label l6 = new Label();
				mv.visitLabel(l6);
				mv.visitLineNumber(6, l6);
				mv.visitInsn(RETURN);
				Label l7 = new Label();
				mv.visitLabel(l7);
				mv.visitLocalVariable("this", descriptor, null, l0, l7, 0);
				mv.visitLocalVariable("request", Type.getDescriptor(HttpServletRequest.class), null, l0, l7, 1);
				mv.visitLocalVariable("response", Type.getDescriptor(HttpServletResponse.class), null, l0, l7, 2);
				mv.visitLocalVariable("i", Type.getDescriptor(int.class), null, l1, l5, 3);
				mv.visitMaxs(3, 4);
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
				pos2 = pos3;
			} else if (pos3 != -1) {
				pos2 = Math.min(pos2, pos3);
			}

			return pos2 == -1 ? (pos1 == 0 ? url : url.substring(pos1)) : url.substring(pos1, pos2);
		}

	}

}

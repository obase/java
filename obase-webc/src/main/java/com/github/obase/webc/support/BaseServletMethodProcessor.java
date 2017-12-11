package com.github.obase.webc.support;

import static com.github.obase.webc.Webc.SC_INVALID_ACCOUNT;
import static com.github.obase.webc.Webc.SC_MISSING_TOKEN;
import static com.github.obase.webc.Webc.SC_MISSING_VERIFIER;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.github.obase.Message;
import com.github.obase.MessageException;
import com.github.obase.WrappedException;
import com.github.obase.kit.ArrayKit;
import com.github.obase.kit.StringKit;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.ServletMethodProcessor;
import com.github.obase.webc.Webc;
import com.github.obase.webc.annotation.ServletController;
import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;

public class BaseServletMethodProcessor implements ServletMethodProcessor {

	protected final Log logger = LogFactory.getLog(getClass());

	protected FilterInitParam params;

	protected boolean sendError;
	protected String[] controlPrefixArray;
	protected String[] controlSuffixArray;

	@Override
	public void init(FilterInitParam params) {
		this.params = params;
		this.sendError = params.sendError;
		if (params.controlPrefix != null) {
			this.controlPrefixArray = StringKit.split(params.controlPrefix, Webc.COMMA, true);
		}
		if (params.controlSuffix != null) {
			this.controlSuffixArray = StringKit.split(params.controlSuffix, Webc.COMMA, true);
		}
	}

	@Override
	public void setup(Collection<ServletMethodObject> objects) throws ServletException {
		if (logger.isInfoEnabled()) {

			Set<String> set = new HashSet<String>();
			for (ServletMethodObject object : objects) {
				set.add(object.lookupPath);
			}
			logger.info("Setup lookup paths: " + set);
		}
	}

	@Override
	public HttpServletRequest process(HttpServletRequest request, HttpServletResponse response, ServletMethodObject object) throws Throwable {
		return request;
	}

	@Override
	public void error(HttpServletRequest request, HttpServletResponse response, Throwable t) {
		if (t != null) {

			while (t instanceof WrappedException) {
				t = t.getCause();
			}

			int sc = 0, errno = 0;
			String errmsg = null;
			if (t instanceof MessageException) {
				MessageException ae = (MessageException) t;
				sc = Webc.SC_SERVER_ERROR;
				errno = ae.getErrno();
				errmsg = ae.getErrmsg();
			} else if (t instanceof MultipartException) {
				if (t instanceof MaxUploadSizeExceededException) {
					MaxUploadSizeExceededException me = (MaxUploadSizeExceededException) t;
					errno = sc = Webc.SC_UPLOAD_SIZE_EXCEEDED;
					errmsg = "Upload size exceeded: " + me.getMaxUploadSize();
				} else {
					errno = sc = Webc.SC_FILE_UPLOAD_FAILED;
					errmsg = "File Upload Size Failed: " + t.getMessage();
				}
			} else {
				sc = Webc.SC_SERVER_ERROR;
				errno = Message.ERRNO_UNDEFINED;
				errmsg = t.getMessage();
			}

			try {
				sendError(response, sc, errno, errmsg);
				posterror(sc, errno, errmsg, t);
			} catch (IOException e) {
				logger.error("Process error failed", e);
			}
		}
	}

	protected void posterror(int sc, int errno, String errmsg, Throwable t) {
		logger.error(String.format("sc=%d,errno=%d,errmsg=%s", sc, errno, errmsg), t);
	}

	// lookup path = package path + class path + method path, it will ignore if "$"
	@Override
	public String lookup(ServletController servletController, Controller controller, Class<?> targetClass, ServletMethod servletMethod, String methodName) {

		StringBuilder sb = new StringBuilder(128);
		String tmp;

		if (servletController != null) {
			// 1. lookup package path
			tmp = servletController.pack();
			if (!Webc.$.equals(tmp)) {
				if (StringKit.isNotEmpty(tmp)) {
					if (tmp.charAt(0) != '/') {
						sb.append('/');
					}
					sb.append(tmp);
				} else {
					tmp = ltrimControlPrefix(targetClass.getPackage());
					if (StringKit.isNotEmpty(tmp)) {
						sb.append('/').append(tmp.replace('.', '/'));
					}
				}
			}

			// 2. lookup class path
			tmp = servletController.path();
			if (!Webc.$.equals(tmp)) {
				if (StringKit.isNotEmpty(tmp)) {
					if (tmp.charAt(0) != '/') {
						sb.append('/');
					}
					sb.append(tmp);
				} else {
					tmp = rtrimControllSuffix(targetClass);
					if (StringKit.isNotEmpty(tmp)) {
						sb.append('/').append(tmp);
					}
				}
			}
		} else {
			// 1. lookup package path & class path
			tmp = controller.value();
			if (!Webc.$.equals(tmp)) {
				if (StringKit.isNotEmpty(tmp)) {
					if (tmp.charAt(0) != '/') {
						sb.append('/');
					}
					sb.append(tmp);
				} else {
					tmp = ltrimControlPrefix(targetClass.getPackage());
					if (StringKit.isNotEmpty(tmp)) {
						sb.append('/').append(tmp.replace('.', '/'));
					}
					tmp = rtrimControllSuffix(targetClass);
					if (StringKit.isNotEmpty(tmp)) {
						sb.append('/').append(tmp);
					}
				}
			}
		}

		tmp = servletMethod.path();
		if (StringKit.isEmpty(tmp)) {
			tmp = servletMethod.value();
		}

		if (!Webc.$.equals(tmp)) {
			if (StringKit.isNotEmpty(tmp)) {
				if (tmp.charAt(0) != '/') {
					sb.append('/');
				}
				sb.append(tmp);
			} else {
				sb.append('/');
				sb.append(methodName);
			}
		}

		// 3. lookup method path

		return sb.toString();

	}

	protected String ltrimControlPrefix(Package pack) {

		String name = pack.getName();
		int plen, nlen = name.length();
		if (ArrayKit.isNotEmpty(controlPrefixArray)) {
			for (String p : controlPrefixArray) {
				if (name.startsWith(p)) {
					plen = p.length();
					if (plen == nlen) {
						return "";
					} else if (p.charAt(plen - 1) == '.') {
						return name.substring(plen);
					} else if (name.charAt(plen) == '.') {
						return name.substring(plen + 1);
					}
				}
			}
		} else {
			int pos = name.indexOf(Webc.DEFAULT_CONTROL_PREFIX);
			if (pos != -1) {
				plen = Webc.DEFAULT_CONTROL_PREFIX.length();
				if ((pos == 0 || name.charAt(pos - 1) == '.')) {
					if (pos + plen == nlen) {
						return "";
					} else if (name.charAt(pos + plen) == '.') {
						return name.substring(pos + plen + 1);
					}
				}
			}
		}
		return name;
	}

	protected String rtrimControllSuffix(Class<?> claz) {
		String name = claz.getSimpleName();
		StringBuilder sb = new StringBuilder(name);
		if (ArrayKit.isNotEmpty(controlSuffixArray)) {
			for (String s : controlSuffixArray) {
				if (name.endsWith(s)) {
					sb.setLength(sb.length() - s.length());
					break;
				}
			}
		} else {
			if (name.endsWith(Webc.DEFAULT_CONTROL_SUFFIX)) {
				sb.setLength(sb.length() - Webc.DEFAULT_CONTROL_SUFFIX.length());
			}
		}
		char ch = name.charAt(0);
		if (ch >= 'A' && ch <= 'Z') {
			sb.setCharAt(0, (char) (ch + 32));
		}
		return sb.toString();
	}

	/**
	 * override for subclass
	 */
	public void sendError(HttpServletResponse response, int sc, int errno, String errmsg) throws IOException {
		switch (errno) {
		case SC_MISSING_TOKEN:
		case SC_MISSING_VERIFIER:
		case SC_INVALID_ACCOUNT:
			Kits.sendError(response, errno, errmsg);
			break;
		default:
			if (sendError) {
				Kits.sendError(response, sc, errno + ':' + errmsg);
			} else {
				Kits.writeErrorMessage(response, errno, errmsg);
			}
		}
	}

}

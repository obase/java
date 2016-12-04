package com.github.obase.webc.support;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Map;

import javax.servlet.AsyncListener;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.github.obase.webc.ApplicationException;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodObject;
import com.github.obase.webc.ServletMethodProcessor;
import com.github.obase.webc.Webc;
import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.json.Jsons;
import com.github.obase.data.Message;
import com.github.obase.kit.ArrayKit;
import com.github.obase.kit.StringKit;

public class BaseServletMethodProcessor implements ServletMethodProcessor {

	protected final Log logger = LogFactory.getLog(getClass());

	protected AsyncListener asyncListener;
	protected long sessionTimeout = Webc.DEFAULT_SESSION_TIMEOUT;// seconds
	protected boolean sendError;
	protected String[] packagePrefix;
	protected String[] classSuffix;

	@Override
	public void initMappingRules(FilterConfig filterConfig, Map<String, ServletMethodObject[]> rules)
			throws ServletException {

		StringBuilder sb = new StringBuilder();
		sb.append(filterConfig.getFilterName()).append(" load lookup rules as follows :\n");
		for (Map.Entry<String, ServletMethodObject[]> entry : rules.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(Arrays.toString(entry.getValue())).append('\n');
		}
		sb.deleteCharAt(sb.length() - 1);
		logger.info(sb.toString());
	}

	@Override
	public HttpServletRequest preprocess(HttpServletRequest request, HttpServletResponse response,
			ServletMethodObject object) throws Exception {
		return request;
	}

	@Override
	public void postprocess(HttpServletRequest request, HttpServletResponse response, Throwable t) {
		if (t != null) {

			logger.error("Postprocess error for " + Kits.getServletPath(request), t);

			int sc = 0, errno = 0;
			String errmsg = null;
			if (t instanceof ApplicationException) {
				ApplicationException ae = (ApplicationException) t;
				sc = ae.getErrno() > 0 ? ae.getErrno() : HttpURLConnection.HTTP_INTERNAL_ERROR;
				errno = sc;
				errmsg = ae.getErrmsg();
			} else if (t instanceof MultipartException) {
				if (t instanceof MaxUploadSizeExceededException) {
					MaxUploadSizeExceededException me = (MaxUploadSizeExceededException) t;
					sc = Webc.ERRNO_UPLOAD_SIZE_EXCEEDED;
					errno = Webc.ERRNO_UPLOAD_SIZE_EXCEEDED;
					errmsg = "Upload size exceeded " + me.getMaxUploadSize();
				} else {
					sc = Webc.ERRNO_FILE_UPLOAD_FAILED;
					errno = Webc.ERRNO_FILE_UPLOAD_FAILED;
					errmsg = "File Upload Size Failed: " + t.getMessage();
				}
			} else {
				sc = HttpURLConnection.HTTP_INTERNAL_ERROR;
				errno = Webc.ERRNO_UNKNOWN_ERROR;
				errmsg = "Unknown Error: " + t.getMessage();
			}

			try {
				if (sendError) {
					Kits.sendError(response, sc, Jsons.writeAsString(new Message<>(errno, errmsg)));
				} else {
					Kits.writeErrorMessage(response, sc, errno, errmsg);
				}
				postprocessThroable(sc, errmsg, t);
			} catch (IOException e) {
				logger.error("Write error message failed", e);
			}
		}
	}

	protected void postprocessThroable(int errno, String errmsg, Throwable t) {
		// for subclass
	}

	@Override
	public AsyncListener getAsyncListener() {
		return this.asyncListener;
	}

	public void setAsyncListener(AsyncListener asyncListener) {
		this.asyncListener = asyncListener;
	}

	public void setSessionTimeout(long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	public void setPackagePrefix(String[] packagePrefix) {
		this.packagePrefix = packagePrefix;
	}

	public void setPackagePrefix(String packagePrefix) {
		if (StringKit.isNotEmpty(packagePrefix)) {
			this.packagePrefix = StringKit.split(packagePrefix, Webc.COMMA, true);
		}
	}

	public void setClassSuffix(String[] classSuffix) {
		this.classSuffix = classSuffix;
	}

	public void setClassSuffix(String classSuffix) {
		if (StringKit.isNotEmpty(classSuffix)) {
			this.packagePrefix = StringKit.split(classSuffix, Webc.COMMA, true);
		}
	}

	public void setSendError(boolean sendError) {
		this.sendError = sendError;
	}

	@Override
	public String parseLookupPath(Class<?> targetClass, Controller classAnnotation, Method targetMethod,
			ServletMethod methodAnnotation) {

		StringBuilder sb = new StringBuilder(512);
		if (!Webc.$.equals(classAnnotation.value())) {
			if (StringKit.isEmpty(classAnnotation.value())) {

				String className = targetClass.getCanonicalName();
				sb.append(className);

				if (ArrayKit.isNotEmpty(packagePrefix)) {
					for (String p : packagePrefix) {
						if (className.startsWith(p)) {
							sb.delete(0, p.length());
							break;
						}
					}
				} else {
					int pos = className.indexOf(Webc.DEFAULT_CONTROLLER_PREFIX);
					if (pos == 0 || (pos > 0 && className.charAt(pos - 1) == '.')) {
						sb.delete(0, pos + Webc.DEFAULT_CONTROLLER_PREFIX.length());
					}
				}

				if (ArrayKit.isNotEmpty(classSuffix)) {
					for (String s : classSuffix) {
						if (className.endsWith(s)) {
							sb.delete(sb.length() - s.length(), sb.length());
						}
					}
				} else {
					if (className.endsWith(Webc.DEFAULT_CONTROLLER_SUFFIX)) {
						sb.delete(sb.length() - Webc.DEFAULT_CONTROLLER_SUFFIX.length(), sb.length());
					}
				}

				int pos = sb.lastIndexOf(".") + 1;
				char ch = sb.charAt(pos);
				if (ch >= 'A' && ch <= 'Z') {
					ch -= ('A' - 'a');
					sb.setCharAt(pos, ch);
				}

			} else {
				sb.append(classAnnotation.value());
			}

			if (sb.charAt(0) != '.') {
				sb.insert(0, '.');
			}

			if (sb.charAt(sb.length() - 1) == '.') {
				sb.deleteCharAt(sb.length() - 1);
			}
		}

		if (!Webc.$.equals(methodAnnotation.value())) {
			if (StringKit.isEmpty(methodAnnotation.value())) {
				sb.append('.').append(targetMethod.getName());
			} else {
				sb.append('.').append(methodAnnotation.value());
			}
		}

		return sb.toString().replace('.', '/');
	}

}

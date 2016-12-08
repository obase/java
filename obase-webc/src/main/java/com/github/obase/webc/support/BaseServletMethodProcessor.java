package com.github.obase.webc.support;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

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
import com.github.obase.json.Jsons;
import com.github.obase.kit.ArrayKit;
import com.github.obase.kit.StringKit;
import com.github.obase.webc.Kits;
import com.github.obase.webc.ServletMethodProcessor;
import com.github.obase.webc.ServletMethodRules;
import com.github.obase.webc.Webc;
import com.github.obase.webc.annotation.ServletMethod;
import com.github.obase.webc.config.WebcConfig.FilterInitParam;

public class BaseServletMethodProcessor implements ServletMethodProcessor {

	protected final Log logger = LogFactory.getLog(getClass());

	protected FilterInitParam params;

	protected boolean sendError;
	protected String[] controlPrefixArray;
	protected String[] controlSuffixArray;

	@Override
	public void setup(FilterInitParam params, Map<String, ServletMethodRules> rules) throws ServletException {
		this.params = params;
		this.sendError = params.sendError;
		if (params.controlPrefix != null) {
			this.controlPrefixArray = StringKit.split(params.controlPrefix, Webc.COMMA, true);
		}
		if (params.controlSuffix != null) {
			this.controlSuffixArray = StringKit.split(params.controlSuffix, Webc.COMMA, true);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Load lookupPath:" + rules.keySet());
		}
	}

	@Override
	public HttpServletRequest process(HttpServletRequest request, HttpServletResponse response) throws Exception {
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

				if (logger.isErrorEnabled()) {
					logger.error(errmsg, t);
				}
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

	/**
	 * uniform lookup path rule
	 */
	@Override
	public final String lookup(Controller classAnnotation, Class<?> targetClass, ServletMethod methodAnnotation, String methodName) {

		StringBuilder sb = new StringBuilder(512);
		if (!Webc.$.equals(classAnnotation.value())) {
			if (StringKit.isEmpty(classAnnotation.value())) {

				String className = targetClass.getCanonicalName();
				sb.append(className);

				if (ArrayKit.isNotEmpty(controlPrefixArray)) {
					for (String p : controlPrefixArray) {
						if (className.startsWith(p)) {
							sb.delete(0, p.length());
							break;
						}
					}
				} else {
					int pos = className.indexOf(Webc.DEFAULT_CONTROL_PREFIX);
					if (pos == 0 || (pos > 0 && className.charAt(pos - 1) == '.')) {
						sb.delete(0, pos + Webc.DEFAULT_CONTROL_PREFIX.length());
					}
				}

				if (ArrayKit.isNotEmpty(controlSuffixArray)) {
					for (String s : controlSuffixArray) {
						if (className.endsWith(s)) {
							sb.delete(sb.length() - s.length(), sb.length());
						}
					}
				} else {
					if (className.endsWith(Webc.DEFAULT_CONTROL_SUFFIX)) {
						sb.delete(sb.length() - Webc.DEFAULT_CONTROL_SUFFIX.length(), sb.length());
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
				sb.append('.').append(methodName);
			} else {
				sb.append('.').append(methodAnnotation.value());
			}
		}

		return sb.toString().replace('.', '/');
	}

}

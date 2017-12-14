package com.github.obase.webc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.cglib.beans.BeanMap;
import org.springframework.web.util.HtmlUtils;

import com.github.obase.Message;
import com.github.obase.Page;
import com.github.obase.json.Jsons;
import com.github.obase.kit.ArrayKit;
import com.github.obase.kit.MapKit;
import com.github.obase.kit.StringKit;
import com.github.obase.kit.TimeKit;
import com.github.obase.security.Principal;

public abstract class Kits {

	Kits() {
	}

	public static Kit bind(HttpServletRequest request, HttpServletResponse response) {
		return new Kit(request, response);
	}

	public static interface PropertyConverter<T> {
		T convert(String value);
	}

	@SuppressWarnings("rawtypes")
	static final Map<Class, PropertyConverter> PropertyConverterCache = new HashMap<Class, PropertyConverter>();
	static {
		PropertyConverter<Boolean> CBoolean = new PropertyConverter<Boolean>() {
			@Override
			public Boolean convert(String value) {
				if (value == null) {
					return null;
				}
				return Boolean.valueOf(value);
			}
		};

		PropertyConverter<Byte> CByte = new PropertyConverter<Byte>() {
			@Override
			public Byte convert(String value) {
				if (value == null) {
					return null;
				}
				return Byte.valueOf(value);
			}
		};

		PropertyConverter<Short> CShort = new PropertyConverter<Short>() {
			@Override
			public Short convert(String value) {
				if (value == null) {
					return null;
				}
				return Short.valueOf(value);
			}
		};

		PropertyConverter<Integer> CInteger = new PropertyConverter<Integer>() {
			@Override
			public Integer convert(String value) {
				if (value == null) {
					return null;
				}
				return Integer.valueOf(value);
			}
		};

		PropertyConverter<Long> CLong = new PropertyConverter<Long>() {
			@Override
			public Long convert(String value) {
				if (value == null) {
					return null;
				}
				return Long.valueOf(value);
			}
		};

		PropertyConverter<Float> CFloat = new PropertyConverter<Float>() {
			@Override
			public Float convert(String value) {
				if (value == null) {
					return null;
				}
				return Float.valueOf(value);
			}
		};

		PropertyConverter<Double> CDouble = new PropertyConverter<Double>() {
			@Override
			public Double convert(String value) {
				if (value == null) {
					return null;
				}
				return Double.valueOf(value);
			}
		};

		PropertyConverter<Date> CDate = new PropertyConverter<Date>() {
			@Override
			public Date convert(String value) {
				if (value == null) {
					return null;
				}
				return TimeKit.parseUnixTime(value);
			}
		};

		PropertyConverterCache.put(Boolean.class, CBoolean);
		PropertyConverterCache.put(boolean.class, CBoolean);
		PropertyConverterCache.put(Byte.class, CByte);
		PropertyConverterCache.put(byte.class, CByte);
		PropertyConverterCache.put(Short.class, CShort);
		PropertyConverterCache.put(short.class, CShort);
		PropertyConverterCache.put(Integer.class, CInteger);
		PropertyConverterCache.put(int.class, CInteger);
		PropertyConverterCache.put(Long.class, CLong);
		PropertyConverterCache.put(long.class, CLong);
		PropertyConverterCache.put(Float.class, CFloat);
		PropertyConverterCache.put(float.class, CFloat);
		PropertyConverterCache.put(Double.class, CDouble);
		PropertyConverterCache.put(double.class, CDouble);
		PropertyConverterCache.put(Date.class, CDate);
	}

	@SuppressWarnings("unchecked")
	public static <T> PropertyConverter<T> getPropertyConverter(Class<T> key) {
		return PropertyConverterCache.get(key);
	}

	public static <T> void setPropertyConverter(Class<T> key, PropertyConverter<T> value) {
		PropertyConverterCache.put(key, value);
	}

	public static void render(HttpServletRequest request, HttpServletResponse response, String pagePath) throws ServletException, IOException {
		request.getRequestDispatcher(pagePath).include(request, response);
	}

	public static String renderContent(HttpServletRequest request, HttpServletResponse response, String pagePath) throws ServletException, IOException {

		final StringWriter out = new StringWriter(2048);
		HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response) {
			final ServletOutputStream sos = new ServletOutputStream() {
				@Override
				public void write(int b) throws IOException {
					out.write(b);
				}

				@Override
				public boolean isReady() {
					return true;
				}

				@Override
				public void setWriteListener(WriteListener arg0) {

				}
			};
			final PrintWriter pw = new PrintWriter(out);

			@Override
			public ServletOutputStream getOutputStream() throws IOException {
				return sos;
			}

			@Override
			public PrintWriter getWriter() throws IOException {
				return pw;
			}

		};

		request.getRequestDispatcher(pagePath).include(request, wrapper);
		out.close();

		return out.toString();
	}

	public static void dispatch(HttpServletRequest request, HttpServletResponse response, String path) {
		request.getAsyncContext().dispatch(path);
	}

	public static void include(HttpServletRequest request, HttpServletResponse response, String pagePath) throws ServletException, IOException {
		request.getRequestDispatcher(pagePath).include(request, response);
	}

	public static void forward(HttpServletRequest request, HttpServletResponse response, String pagePath) throws ServletException, IOException {
		request.getRequestDispatcher(pagePath).forward(request, response);
	}

	public static void sendRedirect(HttpServletResponse response, String path, Map<String, String> params) throws ServletException, IOException {

		if (MapKit.isNotEmpty(params)) {
			StringBuilder sb = new StringBuilder(512);
			sb.append(path);
			if (path.indexOf('?') == -1) {
				sb.append('?');
			}
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				if (StringKit.isNotEmpty(val)) {
					sb.append(key).append('=').append(URLEncoder.encode(val, Webc.CHARSET.name())).append('&');
				}
			}
			sb.setLength(sb.length() - 1);
			path = sb.toString();
		}

		response.sendRedirect(path);
	}

	public static void sendRedirect(HttpServletResponse response, String location) throws ServletException, IOException {
		response.sendRedirect(location);
	}

	public static void sendRedirect2(HttpServletRequest request, HttpServletResponse response, String path) throws ServletException, IOException {

		Map<String, String[]> params = request.getParameterMap();
		if (MapKit.isNotEmpty(params)) {
			StringBuilder sb = new StringBuilder(512);
			sb.append(path);
			if (path.indexOf('?') == -1) {
				sb.append('?');
			}
			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				String key = entry.getKey();
				String[] vals = entry.getValue();
				if (ArrayKit.isNotEmpty(vals)) {
					for (String val : vals) {
						sb.append(key).append('=').append(URLEncoder.encode(val, Webc.CHARSET.name())).append('&');
					}
				}
			}
			sb.setLength(sb.length() - 1);
			path = sb.toString();
		}

		response.sendRedirect(path);
	}

	public static void sendError(HttpServletResponse response, int errno, String errmsg) throws IOException {
		if (response.isCommitted()) {
			return;
		}
		response.sendError(errno, errmsg);
	}

	public static void writeResponse(HttpServletResponse response, String contentType, int sc, CharSequence content) throws IOException {
		response.setContentType(contentType);
		response.setStatus(sc);
		response.getWriter().write(content.toString());
	}

	public static void writePlain(HttpServletResponse response, int sc, CharSequence content) throws IOException {
		writeResponse(response, Webc.CONTENT_TYPE_PLAIN, sc, content);
	}

	public static void writePlain(HttpServletResponse response, CharSequence content) throws IOException {
		writeResponse(response, Webc.CONTENT_TYPE_PLAIN, Webc.SC_OK, content);
	}

	public static void writeHtml(HttpServletResponse response, int sc, CharSequence content) throws IOException {
		writeResponse(response, Webc.CONTENT_TYPE_HTML, sc, content);
	}

	public static void writeHtml(HttpServletResponse response, CharSequence content) throws IOException {
		writeResponse(response, Webc.CONTENT_TYPE_HTML, Webc.SC_OK, content);
	}

	public static void writeJsonObject(HttpServletResponse response, int sc, Object object) throws IOException {
		response.setContentType(Webc.CONTENT_TYPE_JSON);
		response.setStatus(sc);
		Jsons.writeValue(response.getWriter(), object);
	}

	public static void writeJsonObject(HttpServletResponse response, Object object) throws IOException {
		writeJsonObject(response, Webc.SC_OK, object);
	}

	public static void writeJson(HttpServletResponse response, int sc, CharSequence json) throws IOException {
		writeResponse(response, Webc.CONTENT_TYPE_JSON, sc, json);
	}

	public static void writeJson(HttpServletResponse response, CharSequence json) throws IOException {
		writeJson(response, Webc.SC_OK, json);
	}

	public static void writeXml(HttpServletResponse response, int code, CharSequence content) throws IOException {
		writeResponse(response, Webc.CONTENT_TYPE_XML, code, content);
	}

	public static void writeXml(HttpServletResponse response, CharSequence content) throws IOException {
		writeResponse(response, Webc.CONTENT_TYPE_XML, Webc.SC_OK, content);
	}

	public static <T> void writeMessage(HttpServletResponse response, int sc, String src, int errno, String errmsg, T data) throws IOException {
		response.setContentType(Webc.CONTENT_TYPE_JSON);
		response.setStatus(sc);
		Message<T> sm = new Message<T>(src, errno, errmsg, data);
		Jsons.writeValue(response.getWriter(), sm);
	}

	public static <T> void writeSuccessMessage(HttpServletResponse response, T data) throws IOException {
		writeMessage(response, Webc.SC_OK, null, 0, null, data);
	}

	public static void writeErrorMessage(HttpServletResponse response, int errno, String errmsg) throws IOException {
		writeMessage(response, Webc.SC_OK_EVEN_ERROR, null, errno, errmsg, null);
	}

	public static <T> void writeSuccessMessage(HttpServletResponse response, String src, T data) throws IOException {
		writeMessage(response, Webc.SC_OK, src, 0, null, data);
	}

	public static void writeErrorMessage(HttpServletResponse response, String src, int errno, String errmsg) throws IOException {
		writeMessage(response, Webc.SC_OK_EVEN_ERROR, src, errno, errmsg, null);
	}

	public static String readParam(HttpServletRequest request, String name) {
		String param = request.getParameter(name);
		if (param != null) {
			param = param.trim();
			return param.length() == 0 ? null : param;
		}
		return null;
	}

	public static String readParam(HttpServletRequest request, String name, String def) {
		String param = request.getParameter(name);
		if (param != null) {
			param = param.trim();
			return param.length() == 0 ? def : param;
		}
		return def;
	}

	public static <T extends Enum<T>> T readEnumParam(HttpServletRequest request, String name, Class<T> type) {
		String val = readParam(request, name);
		return (val == null ? null : Enum.valueOf(type, val));
	}

	public static boolean readBooleanParam(HttpServletRequest request, String name, boolean def) {
		String val = readParam(request, name);
		return val == null ? def : Boolean.parseBoolean(val);
	}

	public static byte readByteParam(HttpServletRequest request, String name, byte def) {
		String val = readParam(request, name);
		return val == null ? def : Byte.parseByte(val);
	}

	public static short readShortParam(HttpServletRequest request, String name, short def) {
		String val = readParam(request, name);
		return val == null ? def : Short.parseShort(val);
	}

	public static int readIntParam(HttpServletRequest request, String name, int def) {
		String val = readParam(request, name);
		return val == null ? def : Integer.parseInt(val);
	}

	public static long readLongParam(HttpServletRequest request, String name, long def) {
		String val = readParam(request, name);
		return val == null ? def : Long.parseLong(val);
	}

	public static float readFloatParam(HttpServletRequest request, String name, float def) {
		String val = readParam(request, name);
		return val == null ? def : Float.parseFloat(val);
	}

	public static double readDoubleParam(HttpServletRequest request, String name, double def) {
		String val = readParam(request, name);
		return val == null ? def : Double.parseDouble(val);
	}

	/**
	 * Format: yyyy-MM-dd HH:mm:ss
	 */
	public static Date readUnixTimeParam(HttpServletRequest request, String name) {
		String val = readParam(request, name);
		if (val != null) {
			return TimeKit.parseUnixTime(val);
		}
		return null;
	}

	public static Date readTimeParam(HttpServletRequest request, String name, String pattern) throws ParseException {
		String val = readParam(request, name);
		if (val != null) {
			return TimeKit.parse(val, pattern);
		}
		return null;
	}

	public static String[] readArrayParam(HttpServletRequest request, String name, char sep) {
		String val = readParam(request, name);
		if (val != null) {
			return StringKit.split(val, sep, true);
		}
		return null;
	}

	public static String[] readArrayParam(HttpServletRequest request, String name) {
		return readArrayParam(request, name, Webc.COMMA);
	}

	public static <T> T readJsonParam(HttpServletRequest request, String name, Class<T> type) {
		String val = readParam(request, name);
		if (val != null) {
			return Jsons.readValue(val, type);
		}
		return null;
	}

	public static <T> T readJsonParam2(HttpServletRequest request, String name, Class<?> type, Class<?>... subTypes) {
		String val = readParam(request, name);
		if (val != null) {
			return Jsons.readGeneric(val, type, subTypes);
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void populate(HttpServletRequest request, Object object) {

		if (object instanceof Map) {
			Map map = (Map) object;
			for (Object prop : map.keySet()) {
				map.put(prop, readParam(request, (String) prop));
			}
		} else {
			BeanMap map = BeanMap.create(object);
			for (Object prop : map.keySet()) {
				String name = (String) prop;
				String val = readParam(request, name);
				if (val != null) {
					Class<?> type = map.getPropertyType(name);
					if (type.isEnum()) {
						map.put(prop, Enum.valueOf((Class<Enum>) type, name));
					} else {
						PropertyConverter converter = getPropertyConverter(type);
						if (converter != null) {
							map.put(prop, converter.convert(val));
						} else {
							try {
								map.put(prop, type.getConstructor(String.class).newInstance(val));
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
					}
				}
			}
		}

	}

	public static Map<String, String> readParamMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String[]> params = request.getParameterMap();
		if (MapKit.isNotEmpty(params)) {
			String val;
			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				val = null;
				for (String v : entry.getValue()) {
					v = v.trim();
					if (v.length() > 0) {
						val = v;
						break;
					}
				}
				map.put(entry.getKey(), val == null ? null : val.trim());
			}
		}
		return map;
	}

	public static Map<String, String[]> readParamMap2(HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		if (MapKit.isNotEmpty(params)) {
			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				String[] vals = entry.getValue();
				for (int i = 0; i < vals.length; i++) {
					vals[i] = vals[i].trim();
					if (vals[i].length() == 0) {
						vals[i] = null;
					}
				}
			}
		}
		return params;
	}

	public static String readCookie(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (ArrayKit.isNotEmpty(cookies)) {
			for (Cookie cookie : cookies) {
				if (StringKit.equals(name, cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public static Map<String, String> readCookieMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		Cookie[] cookies = request.getCookies();
		if (ArrayKit.isNotEmpty(cookies)) {
			for (Cookie cookie : cookies) {
				map.put(cookie.getName(), cookie.getValue());
			}
		}
		return map;
	}

	public static boolean readBooleanCookie(HttpServletRequest request, String name, boolean def) {
		String ck = readCookie(request, name);
		return StringKit.isEmpty(ck) ? def : Boolean.parseBoolean(ck);
	}

	public static int readIntCookie(HttpServletRequest request, String name, int def) {
		String ck = readCookie(request, name);
		return StringKit.isEmpty(ck) ? def : Integer.parseInt(ck);
	}

	public static long readLongCookie(HttpServletRequest request, String name, long def) {
		String ck = readCookie(request, name);
		return StringKit.isEmpty(ck) ? def : Long.parseLong(ck);
	}

	public static String readHeader(HttpServletRequest request, String name) {
		String val = request.getHeader(name);
		if (val != null) {
			val = val.trim();
			return val.length() == 0 ? null : val;
		}
		return null;
	}

	public static Map<String, String> readHeaderMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		for (Enumeration<String> enums = request.getHeaderNames(); enums.hasMoreElements();) {
			String name = enums.nextElement();
			map.put(name, Kits.readHeader(request, name));
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(HttpServletRequest request, String name) {
		return (T) request.getAttribute(name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSessionAttribute(HttpServletRequest request, String name) {
		return (T) request.getSession().getAttribute(name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getApplicationAttribute(HttpServletRequest request, String name) {
		return (T) request.getServletContext().getAttribute(name);
	}

	public static void setAttribute(HttpServletRequest request, String name, Object value) {
		request.setAttribute(name, value);
	}

	public static void setAttribute(HttpServletRequest request, String name, String value, boolean escaped) {
		request.setAttribute(name, escaped ? HtmlUtils.htmlEscape(value) : value);
	}

	public static void setSessionAttribute(HttpServletRequest request, String name, Object value) {
		request.getSession().setAttribute(name, value);
	}

	public static void setApplicationAttribute(HttpServletRequest request, String name, Object value) {
		request.getServletContext().setAttribute(name, value);
	}

	public static void writeCookie(HttpServletResponse response, String name, String value, String domain, String path, int expiry) {
		Cookie ck = new Cookie(name, value);
		ck.setHttpOnly(true);
		if (domain != null) {
			ck.setDomain(domain);
		}
		ck.setPath(path);
		ck.setMaxAge(expiry);
		response.addCookie(ck);
	}

	public static String getClientIp(HttpServletRequest request) {

		String client = null;
		client = request.getHeader("X-Forwarded-For");
		if (StringKit.isEmpty(client)) {
			client = request.getHeader("X-Real-IP");
			if (StringKit.isEmpty(client)) {
				client = request.getRemoteAddr();
			}
		}

		if (StringKit.isNotEmpty(client)) {
			int pos = client.indexOf(',');
			if (pos > 0) {
				client = client.substring(0, pos);
			}
		}

		return client;
	}

	public static String getReferer(HttpServletRequest request) {
		return request.getHeader("Referer");
	}

	public static String getHost(HttpServletRequest request) {
		return request.getHeader("Host");
	}

	public static String getLookupPath(HttpServletRequest request) {
		return (String) request.getAttribute(Webc.ATTR_LOOKUP_PATH);
	}

	public static String getServletPath(HttpServletRequest request) {
		return request.getServletPath();
	}

	public static String getServletPath(HttpServletRequest request, String lookupPath) {
		String namespace = (String) request.getAttribute(Webc.ATTR_NAMESPACE);
		if (StringKit.isEmpty(namespace)) {
			return lookupPath;
		}
		return new StringBuilder(128).append('/').append(namespace).append(lookupPath).toString();
	}

	public static Wsid getWsid(ServletRequest request) {
		return (Wsid) request.getAttribute(Webc.ATTR_WSID);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Principal> T getPrincipal(ServletRequest request) {
		return (T) request.getAttribute(Webc.ATTR_PRINCIPAL);
	}

	public static String getNamespace(ServletRequest request) {
		return (String) request.getAttribute(Webc.ATTR_NAMESPACE);
	}

	// don't close reader
	public static String readBody(HttpServletRequest request) throws IOException {
		StringBuilder sb = new StringBuilder(request.getContentLength());

		BufferedReader reader = request.getReader();
		int len = 0;
		for (char[] cbuf = new char[512]; (len = reader.read(cbuf)) > 0;) {
			sb.append(cbuf, 0, len);
		}

		return sb.toString();
	}

	public static <T> T readJsonBody(HttpServletRequest request, Class<T> type) throws IOException {
		String json = readBody(request);
		if (StringKit.isEmpty(json)) {
			return null;
		}
		return Jsons.readValue(json, type);
	}

	public static <T> T readJsonBody2(HttpServletRequest request, Class<T> parametrized, Class<?>... parameterClasses) throws IOException {
		String json = readBody(request);
		if (StringKit.isEmpty(json)) {
			return null;
		}
		return Jsons.readGeneric(json, parametrized, parameterClasses);
	}

	public static Map<String, Object> readQueryParam(HttpServletRequest request) throws IOException {
		return readQueryParam(request.getQueryString(), true, request.getCharacterEncoding());
	}

	public static Map<String, Object> readQueryParam(String queryString, boolean nullIfEmpty, String charset) throws IOException {

		Map<String, Object> params = new HashMap<String, Object>();
		if (StringKit.isNotEmpty(queryString)) {
			int vlen = queryString.length(), mark = 0, pos = 0;
			while (pos < vlen && (pos = queryString.indexOf(Webc.LAND, mark)) != -1) {
				if (mark < pos) {
					splitQueryParam(params, queryString.substring(mark, pos), true, charset);
				}
				pos = mark = pos + 1;
			}
			if (mark < vlen) {
				splitQueryParam(params, queryString.substring(mark), true, charset);
			}
		}
		return params;
	}

	private static void splitQueryParam(Map<String, Object> params, String pair, boolean nullIfEmpty, String charset) throws UnsupportedEncodingException {
		int pos = pair.indexOf(Webc.EQUA);
		if (pos == -1) {
			params.put(pair.trim(), null);
		} else if (pos == 0) {
			pair = pair.trim();
			if (pair.length() == 0 && nullIfEmpty) {
				pair = null;
			} else {
				pair = URLDecoder.decode(pair, charset);
			}
			params.put(null, pair);
		} else {
			String key = pair.substring(0, pos).trim();
			pair = pair.substring(pos + 1).trim();
			if (pair.length() == 0 && nullIfEmpty) {
				pair = null;
			} else {
				pair = URLDecoder.decode(pair, charset);
			}
			params.put(key, pair);
		}
	}

	public static String getServletPath(String namespace, String lookupPath, String queryString) {

		if (StringKit.isEmpty(namespace) && StringKit.isEmpty(queryString)) {
			return lookupPath;
		}

		StringBuilder sb = new StringBuilder(512);
		if (StringKit.isNotEmpty(namespace)) {
			sb.append('/');
			sb.append(namespace);
		}
		sb.append(lookupPath);
		if (StringKit.isNotEmpty(queryString)) {
			sb.append('?').append(queryString);
		}
		return sb.toString();
	}

	public static String getLookupPath(String namespace, String servletPath) {

		int pos1 = servletPath.indexOf(namespace); // should be 1
		if (pos1 == -1) {
			pos1 = 0;
		} else {
			pos1 += namespace.length();
		}
		int pos2 = servletPath.indexOf('?');

		return pos2 == -1 ? (pos1 == 0 ? servletPath : servletPath.substring(pos1)) : servletPath.substring(pos1, pos2);
	}

	public static final String[] PAGE_PARAMS = { "start", "limit", "field", "direction" };

	/**
	 * page params: _start, _limit, _field, _direction
	 */
	public static <T> Page<T> readPage(HttpServletRequest request, Class<T> type) {
		return readPage(request, type, PAGE_PARAMS);
	}

	public static <T> Page<T> readPage(HttpServletRequest request, Class<T> type, String[] pageParams) {
		int _start = readIntParam(request, pageParams[0], 0);
		int _limit = readIntParam(request, pageParams[1], 0);
		String _field = readParam(request, pageParams[2]);
		String _direction = readParam(request, pageParams[3]);
		return new Page<>(_start, _limit, _field, "DESC".equalsIgnoreCase(_direction));
	}

}

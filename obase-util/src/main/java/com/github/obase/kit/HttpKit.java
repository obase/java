package com.github.obase.kit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpKit {

	static SSLSocketFactory sslSocketFactory;
	static HostnameVerifier sslHostnameVerifier;

	static void initSslHostnameVerifier() {
		if (sslHostnameVerifier == null) {
			synchronized (HttpKit.class) {
				if (sslHostnameVerifier == null) {
					sslHostnameVerifier = new HostnameVerifier() {
						public boolean verify(String urlHostName, SSLSession session) {
							return urlHostName != null && urlHostName.equals(session.getPeerHost());
						}
					};
				}
			}
		}

	}

	static void initSslSocketFactory() {
		if (sslSocketFactory == null) {
			synchronized (HttpKit.class) {
				if (sslSocketFactory == null) {
					try {
						SSLContext context = SSLContext.getInstance("TLS");
						final X509TrustManager trustManager = new X509TrustManager() {
							public X509Certificate[] getAcceptedIssuers() {
								return null;
							}

							public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
							}

							public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

							}
						};
						context.init(null, new TrustManager[] { trustManager }, null);
						sslSocketFactory = context.getSocketFactory();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
	}

	private static HttpURLConnection createConnection(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		if (conn instanceof HttpsURLConnection) {
			if (sslSocketFactory == null) {
				initSslSocketFactory();
			}
			if (sslHostnameVerifier == null) {
				initSslHostnameVerifier();
			}
			HttpsURLConnection conns = (HttpsURLConnection) conn;
			conns.setSSLSocketFactory(sslSocketFactory);
			conns.setHostnameVerifier(sslHostnameVerifier);
		}
		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);
		conn.setRequestProperty("Connection", "close");
		return conn;
	}

	public static HttpResponse doRest(String method, String url, String params, Map<String, String> headers) throws IOException {
		HttpRequest request = new HttpRequest();
		if (GET.equals(method) || DELETE.equals(method)) {
			request.query = params;
		} else if (POST.equals(method) || PUT.equals(method)) {
			request.content = params;
		} else {
			throw new IllegalArgumentException("Method should be GET,DELETE,POST or PUT");
		}
		request.method = method;
		request.url = url;
		request.properties = headers;
		return doHttp(request);
	}

	public static HttpResponse doHttp(HttpRequest request) throws IOException {

		HttpURLConnection connection = null;
		try {
			String url = request.url;
			if (StringKit.isNotEmpty(request.query)) {
				StringBuilder sb = new StringBuilder(url.length() + request.query.length() + 2);
				sb.append(url);
				if (url.indexOf('?') == -1) {
					sb.append('?');
				}
				sb.append(request.query);
				url = sb.toString();
			}
			// 获取连接
			connection = createConnection(url);
			connection.setRequestMethod(request.method);
			if (request.connectTimeout > 0) {
				connection.setConnectTimeout(request.connectTimeout);
			}
			if (request.readTimeout > 0) {
				connection.setReadTimeout(request.readTimeout);
			}
			connection.setDoOutput(true);
			connection.setDoInput(true);
			// 设置请求头
			if (MapKit.isNotEmpty(request.properties)) {
				for (Map.Entry<String, String> entry : request.properties.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			connection.connect();
			// 设置请求正文
			if (StringKit.isNotEmpty(request.content)) {
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), request.encoding == null ? UTF8 : request.encoding));
				out.write(request.content);
				out.close();
			}

			int code = connection.getResponseCode();
			String message = connection.getResponseMessage();
			String content = null;
			InputStream in = connection.getErrorStream();
			if (in == null) {
				in = connection.getInputStream();
			}
			if (in != null) {
				int length = connection.getContentLength(); // -1 if error
				StringBuilder sb = new StringBuilder(length > 0 ? length : BUFFERS);
				String encoding = connection.getContentEncoding();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in, encoding == null ? UTF8 : encoding));
				int len = 0;
				for (char[] cbuf = new char[2048]; (len = reader.read(cbuf)) > 0;) {
					sb.append(cbuf, 0, len);
				}
				content = sb.toString();
			}
			return new HttpResponse(code, message, content);
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String OPTIONS = "OPTIONS";
	public static final String HEAD = "HEAD";
	public static final String TRACE = "TRACE";
	public static final String CONNECT = "CONNECT";

	public static final String UTF8 = "UTF-8";
	public static final int BUFFERS = 4096;

	public static final char LAND = '&';
	public static final char EQUA = '=';

	public static class HttpRequest {
		public String method;
		public String url;
		public String query;
		public Map<String, String> properties;
		public String content;
		public int connectTimeout;
		public int readTimeout;
		public String encoding;
	}

	public static class HttpResponse {

		public final int code;
		public final String message;
		public final String content;

		protected HttpResponse(int code, String message, String content) {
			this.code = code;
			this.message = message;
			this.content = content;
		}
	}

	public static String joinQuery(Map<String, Object> params, String charset) throws IOException {
		StringBuilder sb = new StringBuilder(BUFFERS);
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			Object val = entry.getValue();
			if (val != null) {
				sb.append(entry.getKey()).append(EQUA).append(URLEncoder.encode(val.toString(), charset)).append(LAND);
			}
		}
		if (sb.length() > 0) { // delete last &
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public static Map<String, Object> splitQuery(String queryString, boolean nullIfEmpty, String charset) throws IOException {

		Map<String, Object> params = new HashMap<String, Object>();
		if (StringKit.isNotEmpty(queryString)) {
			int vlen = queryString.length(), mark = 0, pos = 0;
			while (pos < vlen && (pos = queryString.indexOf(LAND, mark)) != -1) {
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
		int pos = pair.indexOf(EQUA);
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
}

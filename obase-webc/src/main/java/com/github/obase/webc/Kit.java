package com.github.obase.webc;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;

import com.github.obase.webc.support.security.Principal;

import redis.clients.jedis.JedisPool;

public final class Kit extends Kits {

	final HttpServletRequest request;
	final HttpServletResponse response;
	final JedisPool jedisPool;

	Kit(HttpServletRequest request, HttpServletResponse response, JedisPool jedisPool) {
		this.request = request;
		this.response = response;
		this.jedisPool = jedisPool;
	}

	/*********************************** 实例方法列表 ***********************************/
	public void render(String pagePath) throws ServletException, IOException {
		Kits.render(request, response, pagePath);
	}

	public String renderContent(String pagePath) throws ServletException, IOException {
		return Kits.renderContent(request, response, pagePath);
	}

	public void dispatch(String path) {
		Kits.dispatch(request, response, path);
	}

	public void include(String pagePath) throws ServletException, IOException {
		Kits.include(request, response, pagePath);
	}

	public void forward(String pagePath) throws ServletException, IOException {
		Kits.forward(request, response, pagePath);
	}

	public void sendRedirect(String path, Map<String, String> params) throws ServletException, IOException {
		Kits.sendRedirect(response, path, params);
	}

	public void sendRedirect(String location) throws ServletException, IOException {
		Kits.sendRedirect(response, location);
	}

	public void sendRedirect2(String path) throws ServletException, IOException {
		Kits.sendRedirect2(request, response, path);
	}

	public void sendError(int errno, String errmsg) throws IOException {
		Kits.sendError(response, errno, errmsg);
	}

	public void writeResponse(String contentType, int sc, CharSequence content) throws IOException {
		Kits.writeResponse(response, contentType, sc, content);
	}

	public void writePlain(int sc, CharSequence content) throws IOException {
		Kits.writePlain(response, sc, content);
	}

	public void writePlain(CharSequence content) throws IOException {
		Kits.writePlain(response, Webc.SC_OK, content);
	}

	public void writeHtml(int sc, CharSequence content) throws IOException {
		Kits.writeHtml(response, sc, content);
	}

	public void writeHtml(CharSequence content) throws IOException {
		Kits.writeHtml(response, Webc.SC_OK, content);
	}

	public void writeJsonObject(int sc, Object object) throws IOException {
		Kits.writeJsonObject(response, sc, object);
	}

	public void writeJsonObject(Object object) throws IOException {
		Kits.writeJsonObject(response, Webc.SC_OK, object);
	}

	public void writeJson(int sc, CharSequence json) throws IOException {
		Kits.writeJson(response, sc, json);
	}

	public void writeJson(CharSequence json) throws IOException {
		Kits.writeJson(response, Webc.SC_OK, json);
	}

	public void writeXml(int sc, CharSequence content) throws IOException {
		Kits.writeXml(response, sc, content);
	}

	public void writeXml(CharSequence content) throws IOException {
		Kits.writeXml(response, Webc.SC_OK, content);
	}

	public <T> void writeMessage(int sc, String src, int errno, String errmsg, T data) throws IOException {
		Kits.writeMessage(response, sc, src, errno, errmsg, data);
	}

	public <T> void writeSuccessMessage(T data) throws IOException {
		Kits.writeSuccessMessage(response, data);
	}

	public void writeErrorMessage(int errno, String errmsg) throws IOException {
		Kits.writeErrorMessage(response, errno, errmsg);
	}

	public <T> void writeSuccessMessage(String src, T data) throws IOException {
		Kits.writeSuccessMessage(response, src, data);
	}

	public void writeErrorMessage(String src, int errno, String errmsg) throws IOException {
		Kits.writeErrorMessage(response, src, errno, errmsg);
	}

	public String readParam(String name) {
		return Kits.readParam(request, name);
	}

	public String readParam(String name, String def) {
		return Kits.readParam(request, name, def);
	}

	public <T extends Enum<T>> T readEnumParam(String name, Class<T> type) {
		return Kits.readEnumParam(request, name, type);
	}

	public boolean readBooleanParam(String name, boolean def) {
		return Kits.readBooleanParam(request, name, def);
	}

	public byte readByteParam(String name, byte def) {
		return Kits.readByteParam(request, name, def);
	}

	public short readShortParam(String name, short def) {
		return Kits.readShortParam(request, name, def);
	}

	public int readIntParam(String name, int def) {
		return Kits.readIntParam(request, name, def);
	}

	public long readLongParam(String name, long def) {
		return Kits.readLongParam(request, name, def);
	}

	public float readFloatParam(String name, float def) {
		return Kits.readFloatParam(request, name, def);
	}

	public double readDoubleParam(String name, double def) {
		return Kits.readDoubleParam(request, name, def);
	}

	/**
	 * Format: yyyy-MM-dd HH:mm:ss
	 */
	public Date readUnixTimeParam(String name) {
		return Kits.readUnixTimeParam(request, name);
	}

	public Date readTimeParam(String name, String pattern) throws ParseException {
		return Kits.readTimeParam(request, name, pattern);
	}

	public String[] readArrayParam(String name, char sep) {
		return Kits.readArrayParam(request, name, sep);
	}

	public String[] readArrayParam(String name) {
		return Kits.readArrayParam(request, name);
	}

	public <T> T readJsonParam(String name, Class<T> type) {
		return Kits.readJsonParam(request, name, type);
	}

	public <T> T readJsonParam2(String name, Class<?> type, Class<?>... subTypes) {
		return Kits.readJsonParam2(request, name, type, subTypes);
	}

	public void populate(Object object) {
		Kits.populate(request, object);
	}

	public Map<String, String> readParamMap() {
		return Kits.readParamMap(request);
	}

	public Map<String, String[]> readParamMap2() {
		return Kits.readParamMap2(request);
	}

	public String readCookie(String name) {
		return Kits.readCookie(request, name);
	}

	public Map<String, String> readCookieMap() {
		return Kits.readCookieMap(request);
	}

	public boolean readBooleanCookie(String name, boolean def) {
		return Kits.readBooleanCookie(request, name, def);
	}

	public int readIntCookie(String name, int def) {
		return Kits.readIntCookie(request, name, def);
	}

	public long readLongCookie(String name, long def) {
		return Kits.readLongCookie(request, name, def);
	}

	public String readHeader(String name) {
		return Kits.readHeader(request, name);
	}

	public Map<String, String> readHeaderMap() {
		return Kits.readHeaderMap(request);
	}

	public <T> T getAttribute(String name) {
		return Kits.getAttribute(request, name);
	}

	public <T> T getSessionAttribute(String name) {
		return Kits.getSessionAttribute(request, name);
	}

	public <T> T getApplicationAttribute(String name) {
		return Kits.getApplicationAttribute(request, name);
	}

	public <T> T getGlobalAttribute(String name) {
		return Kits.getGlobalAttribute(jedisPool, name);
	}

	public void setAttribute(String name, Object value) {
		Kits.setAttribute(request, name, value);
	}

	public void setAttribute(String name, String value, boolean escaped) {
		Kits.setAttribute(request, name, value, escaped);
	}

	public void setSessionAttribute(String name, Object value) {
		Kits.setSessionAttribute(request, name, value);
	}

	public void setApplicationAttribute(String name, Object value) {
		Kits.setApplicationAttribute(request, name, value);
	}

	public void setGlobalAttribute(String name, Object value, int expireSeconds) {
		Kits.setGlobalAttribute(jedisPool, name, value, expireSeconds);
	}

	public void writeCookie(String name, String value, int expiry) {
		Kits.writeCookie(response, name, value, expiry);
	}

	public void writeCookie(String name, String value, String path, int expiry) {
		Kits.writeCookie(response, name, value, path, expiry);
	}

	public String getClientIp() {
		return Kits.getClientIp(request);
	}

	public String getReferer() {
		return Kits.getReferer(request);
	}

	public String getHost() {
		return Kits.getHost(request);
	}

	public String getLookupPath() {
		return Kits.getLookupPath(request);
	}

	public String getServletPath() {
		return Kits.getServletPath(request);
	}

	public String getServletPath(String lookupPath) {
		return Kits.getServletPath(request, lookupPath);
	}

	public HttpMethod getHttpMethod() {
		return Kits.getHttpMethod(request);
	}

	public Wsid getWsid() {
		return Kits.getWsid(request);
	}

	public Principal getPrincipal() {
		return Kits.getPrincipal(request);
	}

	public String getNamespace() {
		return Kits.getNamespace(request);
	}

	// don't close reader
	public String readBody() throws IOException {
		return Kits.readBody(request);
	}

	public <T> T readJsonBody(Class<T> type) throws IOException {
		return Kits.readJsonBody(request, type);
	}

	public <T> T readJsonBody2(Class<T> parametrized, Class<?>... parameterClasses) throws IOException {
		return Kits.readJsonBody2(request, parametrized, parameterClasses);
	}

	public Map<String, String> readQueryParam() throws IOException {
		return Kits.readQueryParam(request);
	}

}

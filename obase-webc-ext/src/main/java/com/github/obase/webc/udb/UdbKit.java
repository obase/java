package com.github.obase.webc.udb;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.duowan.udb.auth.UserinfoForOauth;
import com.duowan.udb.util.codec.AESHelper;
import com.duowan.universal.login.BasicCredentials;
import com.duowan.universal.login.Credentials;
import com.duowan.universal.login.OAuthHeaderNames;
import com.duowan.universal.login.client.UniversalLoginClient;
import com.duowan.universal.login.client.UniversalLoginClient.CookieDomainEnum;
import com.duowan.universal.login.client.YYSecCenterOpenWSInvoker;
import com.github.obase.kit.StringKit;
import com.github.obase.webc.Kits;
import com.github.obase.webc.Webc;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.support.security.Principal;

public final class UdbKit {

	static Log logger = LogFactory.getLog(UdbKit.class);

	private UdbKit() {
	}

	public static final String LOOKUP_PATH_LOGIN = "/login";
	public static final String LOOKUP_PATH_LOGOUT = "/logout";
	public static final String LOOKUP_PATH_GEN_URL_TOKEN = "/genUrlToken";
	public static final String LOOKUP_PATH_CALLBACK = "/callback";
	public static final String LOOKUP_PATH_DENY_CALLBACK = "/denyCallback";

	public static final int ERRNO_PERMISSION_DENIED = Webc.ERRNO_PERMISSION_DENIED;
	public static final int ERRNO_CSRF_ERROR = Webc.ERRNO_CSRF_ERROR;
	public static final int ERRNO_SESSION_TIMEOUT = Webc.ERRNO_SESSION_TIMEOUT;
	public static final int ERRNO_MISSING_TOKEN = Webc.ERRNO_MISSING_TOKEN;
	public static final int ERRNO_MISSING_VERIFIER = Webc.ERRNO_MISSING_VERIFIER;
	public static final int ERRNO_INVALID_ACCOUNT = Webc.ERRNO_INVALID_ACCOUNT;

	public static final String PARAM_URL = "url";
	public static final String PARAM_SSL = "ssl";

	public static String getRealUrl(HttpServletRequest request, String protocol, String servletPathPrefix, String lookupPath) {
		StringBuilder sb = new StringBuilder(256);
		if (protocol != null) {
			sb.append(protocol);
		}
		sb.append("//");
		sb.append(Kits.getHost(request));
		sb.append(request.getContextPath());
		if (StringKit.isNotEmpty(servletPathPrefix)) {
			if (servletPathPrefix.charAt(0) != '/') {
				sb.append('/');
			}
			sb.append(servletPathPrefix);
		}
		sb.append(lookupPath);
		return sb.toString();
	}

	public static void login(HttpServletRequest request, HttpServletResponse response, String servletPathPrefix) throws Exception {

		String url = Kits.readParam(request, PARAM_URL);

		StringBuilder sb = new StringBuilder(512);
		sb.append("<!DOCTYPE html>");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		sb.append("<title>YY统一验证中心</title>");
		sb.append("<script src=\"https://res.udb.duowan.com/js/jquery-1.4.2.min.js\" type=\"text/javascript\"></script>");
		sb.append("<script type=\"text/javascript\" src=\"https://res.udb.duowan.com/lgn/js/oauth/udbsdk/pcweb/udb.sdk.pcweb.popup.min.js\"></script>");
		sb.append("<script type=\"text/javascript\">");
		sb.append("function sdklogin(){ UDB.sdk.PCWeb.popupOpenLgn(document.location.protocol+'").append(getRealUrl(request, null, servletPathPrefix, LOOKUP_PATH_GEN_URL_TOKEN)).append("?ssl='+(document.location.protocol=='https:')");
		if (url != null) {
			sb.append("+'&url=").append(URLEncoder.encode(url, Webc.CHARSET_NAME)).append("'");
		}
		sb.append(",'','');}");
		sb.append("</script>");
		sb.append("</head>");
		sb.append("<body onload=\"sdklogin();\">");
		sb.append("</body>");
		sb.append("</html>");

		Kits.writeHtml(response, HttpsURLConnection.HTTP_OK, sb);
	}

	public static void genUrlToken(HttpServletRequest request, HttpServletResponse response, String servletPathPrefix, String appid, String appkey) throws IOException {
		try {
			boolean pssl = Kits.readBooleanParam(request, PARAM_SSL, false);
			String purl = Kits.readParam(request, PARAM_URL);

			Credentials cc = new BasicCredentials(appid, appkey);
			UniversalLoginClient duowan = new UniversalLoginClient(cc);
			duowan.initialize(getRealUrl(request, pssl ? "https:" : "http:", servletPathPrefix, LOOKUP_PATH_CALLBACK + (StringKit.isEmpty(purl) ? "" : "?url=" + purl)));
			String tmpTokensecret = duowan.getTokenSecret();
			tmpTokensecret = AESHelper.encrypt(tmpTokensecret, appkey);
			URL redirectURL = duowan.getAuthorizationURL();
			String url = redirectURL.toExternalForm() + "&denyCallbackURL=" + getRealUrl(request, pssl ? "https:" : "http:", servletPathPrefix, LOOKUP_PATH_DENY_CALLBACK) + "&UIStyle=qlogin&cssid=" + appid;

			StringBuilder sb = new StringBuilder(256).append("{\"success\":\"1\",\"url\":").append("\"").append(url).append("\"").append(",\"ttokensec\":").append("\"").append(tmpTokensecret).append("\"").append("}");

			Kits.writeJson(response, HttpsURLConnection.HTTP_OK, sb);
		} catch (Exception e) {
			logger.error("udb产生认证url与token失败", e);
			Kits.writeJson(response, HttpsURLConnection.HTTP_OK, "{\"success\":\"0\",\"errMsg\":\"UDB统一登录失败,请与管理员联系\"}");
		}
	}

	public static void callback(HttpServletRequest request, HttpServletResponse response, String servletPathPrefix, String appid, String appkey, String homepage, Callback c) throws Exception {

		// 服务端返回的信息
		final String oauthToken = Kits.readParam(request, OAuthHeaderNames.TOKEN_KEY, null);
		final String oauthVerfier = Kits.readParam(request, OAuthHeaderNames.VERIFIER, null);
		@SuppressWarnings("unused")
		String isRemMe = Kits.readParam(request, "isRemMe", null);// 自动登录（该对应bbs登录有自动登陆的需求的系统）1表示自动登陆，0表示未自动登陆
		// 查找请求token密钥
		String tokenSecret = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("udboauthtmptokensec".equals(cookie.getName())) {
					tokenSecret = AESHelper.decrypt(cookie.getValue(), appkey); // 用appkey将tokenSecret解密出来
					break;
				}
			}
		}
		// 校验下
		if (tokenSecret == null) {
			response.addHeader("X-DUOWAN-UDB-ERROR", String.valueOf(ERRNO_SESSION_TIMEOUT));
			c.sendBadParameterError(response, ERRNO_SESSION_TIMEOUT, "Timeout for udb session, please try again!");
			return;
		}
		if (StringKit.isEmpty(oauthToken)) {
			response.addHeader("X-DUOWAN-UDB-ERROR", String.valueOf(ERRNO_MISSING_TOKEN));
			c.sendBadParameterError(response, ERRNO_MISSING_TOKEN, "Missing udb param oauthToken");
			return;
		}
		if (StringKit.isEmpty(oauthVerfier)) {
			response.addHeader("X-DUOWAN-UDB-ERROR", String.valueOf(ERRNO_MISSING_TOKEN));
			c.sendBadParameterError(response, ERRNO_MISSING_VERIFIER, "Missing udb param oauthVerfier");
			return;
		}
		// 用返回的requestToken以及veriferCode，同udb通信
		Credentials cc = new BasicCredentials(appid, appkey);
		UniversalLoginClient duowan = new UniversalLoginClient(cc);
		// 获取accesstoeken信息
		String[] accessTokenInfo = duowan.getAccessToken(oauthToken, tokenSecret, oauthVerfier);
		// 推荐使用
		String yyuid = duowan.getYyuid(accessTokenInfo[0]);
		/* 获取用户信息，未来要废弃username， */
		@SuppressWarnings("deprecation")
		String[] uProfile = duowan.getUserProfile(accessTokenInfo[0]); // passport
		if (!c.postUdbLogin(request, response, yyuid, uProfile)) {
			response.addHeader("X-DUOWAN-UDB-ERROR", String.valueOf(ERRNO_INVALID_ACCOUNT));
			c.sendBadParameterError(response, ERRNO_INVALID_ACCOUNT, "Invalid account!");
			return;
		}

		List<String> reqDomainList = new LinkedList<String>();
		reqDomainList.add("lgn.tuboshu.com");
		reqDomainList.add(CookieDomainEnum.YY_DOMAIN);
		reqDomainList.add(CookieDomainEnum.YY_TV_DOMAIN);
		reqDomainList.add(CookieDomainEnum.DUOWAN_DOMAIN);
		String writeCookieURL = duowan.getWriteCookieURL(accessTokenInfo[0], yyuid, reqDomainList);

		/* 获取登陆前的url,如无则使用默认LOGINED_SYS_URI */
		String url = StringKit.isNotEmpty(homepage) ? ("document.location.protocol+'" + getRealUrl(request, null, servletPathPrefix, homepage) + "'") : ("'" + Kits.readParam(request, PARAM_URL, "/") + "'");
		StringBuilder sb = new StringBuilder(256).append("<script language=\"JavaScript\" type=\"text/javascript\">function udb_callback(){self.parent.UDB.sdk.PCWeb.writeCrossmainCookieWithCallBack('" + writeCookieURL
				+ "',function(){self.parent.document.location.href=" + url + ";});};udb_callback();</script>").append("</head><body>");

		Kits.writeHtml(response, Webc.HTTP_OK, sb);
	}

	public static void denyCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
		StringBuilder out = new StringBuilder(512);
		out.append("<!DOCTYPE html>");
		out.append("<html>");
		out.append("<head>");
		out.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		out.append("<title>YY统一验证中心</title>");
		out.append("<script src=\"https://res.udb.duowan.com/js/jquery-1.4.2.min.js\" type=\"text/javascript\"></script>");
		out.append("<script type=\"text/javascript\" src=\"https://res.udb.duowan.com/lgn/js/oauth/udbsdk/pcweb/udb.sdk.pcweb.popup.min.js\"></script>");
		out.append("<script language=\"JavaScript\" type=\"text/javascript\">");
		out.append("self.parent.UDB.sdk.PCWeb.popupCloseLgn();");
		out.append("</script>");
		out.append("</head></html>");
		Kits.writeHtml(response, Webc.HTTP_OK, out);
	}

	public static void logout(HttpServletRequest request, HttpServletResponse response, String servletPathPrefix, String appid, String appkey, String logoutpage, Callback c) throws IOException {

		/* 清除cookie信息 */
		c.preUdbLogout(request, response);

		String deleteCookieURL = YYSecCenterOpenWSInvoker.getOAuthCookieDeleteURL(appid, appkey);
		StringBuilder sb = new StringBuilder(512);

		/* 默认跳回home */
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		sb.append("<title>YY统一验证中心</title>");
		sb.append("<script src=\"https://res.udb.duowan.com/js/jquery-1.4.2.min.js\" type=\"text/javascript\"></script>");
		sb.append("<script src=\"https://res.udb.duowan.com/lgn/js/oauth/udbsdk/pcweb/udb.sdk.pcweb.popup.min.js\" type=\"text/javascript\"></script>");
		sb.append("</head>");
		sb.append("<script type=\"text/javascript\">");
		sb.append("function logout(){ UDB.sdk.PCWeb.deleteCrossmainCookieWithCallBack(\"" + deleteCookieURL + "\" ,");
		sb.append("	 function() { top.location.href = document.location.protocol+'");
		if (StringKit.isNotEmpty(logoutpage)) {
			sb.append(getRealUrl(request, null, servletPathPrefix, logoutpage));
		} else {
			sb.append(getRealUrl(request, null, servletPathPrefix, LOOKUP_PATH_LOGIN));
		}
		sb.append("' } ); }");
		sb.append("</script>");
		sb.append("<body onload=\"logout();\">");
		sb.append("</body>");
		sb.append("</html>");

		Kits.writeHtml(response, Webc.HTTP_OK, sb);

	}

	/**
	 * return [passport, yyuid] if success, null if fail
	 */
	public static String[] tryOssLogin(HttpServletRequest request, HttpServletResponse response, String appid, String appkey) throws IOException {
		UserinfoForOauth userinfoForOauth = new UserinfoForOauth(request, response, appid, appkey);
		if (userinfoForOauth.validate()) {
			@SuppressWarnings("deprecation")
			String username = userinfoForOauth.getUsername();
			String yyuid = userinfoForOauth.getYyuid();

			return new String[] { username, yyuid };
		}
		return null;
	}

	public static interface Callback {

		boolean postUdbLogin(HttpServletRequest request, HttpServletResponse response, String yyuid, String[] uProfile) throws IOException;

		void preUdbLogout(HttpServletRequest request, HttpServletResponse response) throws IOException;

		Principal validateAndExtendPrincipal(Wsid wsid) throws IOException;

		void sendBadParameterError(HttpServletResponse resp, int errno, String errmsg) throws IOException;
	}
}

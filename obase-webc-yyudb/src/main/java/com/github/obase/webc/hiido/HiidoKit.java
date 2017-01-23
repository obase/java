package com.github.obase.webc.hiido;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.obase.MessageException;
import com.github.obase.WrappedException;
import com.github.obase.kit.CollectKit;
import com.github.obase.security.Principal;
import com.github.obase.webc.Wsid;
import com.github.obase.webc.YyudbErrno;
import com.github.obase.webc.yy.UserPrincipal;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionOptions;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

public final class HiidoKit {

	static Log logger = LogFactory.getLog(HiidoKit.class);

	private HiidoKit() {
	}

	public static final String HIIDO_LOGIN_URL = "https://portal.hiido.com";

	public static final String LOOKUP_PATH_POST_HIIDO_LOGIN = "/postHiidoLogin";

	public static final String PARAM_TOKEN = "token";

	static final Map<String, Boolean> INVALID, VALID;

	static {
		Map<String, Boolean> map1 = new HashMap<String, Boolean>(2);
		map1.put("is_alive", true);
		map1.put("is_show", true);
		VALID = Collections.unmodifiableMap(map1);
		Map<String, Boolean> map2 = new HashMap<String, Boolean>(2);
		map2.put("is_alive", false);
		map2.put("is_show", false);
		INVALID = Collections.unmodifiableMap(map2);
	}

	private static Map<String, Map<String, Boolean>> as(String user, Map<String, Boolean> val) {
		Map<String, Map<String, Boolean>> rt = new HashMap<String, Map<String, Boolean>>(1);
		rt.put(user, val);
		return rt;
	}

	public static UserPrincipal getStaffInfoByToken(String udbApi, String agentId, byte[] agentPwdBytes, String publicKey, String token) {
		JSONObject result = jsonrpc(udbApi, agentId, agentPwdBytes, publicKey, "getStaffInfoByToken", Arrays.<Object> asList(token));
		if (RpcKit.code(result) != 1) {
			return null;
		}
		Map<String, Object> data = RpcKit.dataObject(result);
		if (data.containsKey("passport")) {
			UserPrincipal principal = new UserPrincipal();
			principal.setJobCode(RpcKit._String(data, "job_code", null));
			principal.setPassport(RpcKit._String(data, "passport", null));
			principal.setEmail(RpcKit._String(data, "email", null));
			principal.setRealname(RpcKit._String(data, "realname", null));
			principal.setNickname(RpcKit._String(data, "nickname", null));
			principal.setDeptname(RpcKit._String(data, "deptname", null));
			return principal;
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Principal> getMyAgentStaffInfo(String udbApi, String agentId, byte[] agentPwdBytes, String publicKey) {
		// 执行同步, 将usrmgr中maintain中的user同步到hiido
		JSONObject result = jsonrpc(udbApi, agentId, agentPwdBytes, publicKey, "getMyAgentStaffInfo", Arrays.<Object> asList(Collections.emptyList()));
		if (RpcKit.code(result) != 1) {
			throw new MessageException(YyudbErrno.SOURCE, YyudbErrno.HIIDO_VALID_FAILED, result.toJSONString());
		}

		// 已注册用户
		List<Principal> staffs = new LinkedList<Principal>();
		List list = RpcKit.array(result, "data", "data"); // 二层
		if (CollectKit.isNotEmpty(list)) {
			for (Object item : list) {
				Map<String, Object> data = (Map<String, Object>) item;
				if (data.containsKey("passport")) {
					UserPrincipal principal = new UserPrincipal();
					principal.setJobCode(RpcKit._String(data, "job_code", null));
					principal.setPassport(RpcKit._String(data, "passport", null));
					principal.setEmail(RpcKit._String(data, "email", null));
					principal.setRealname(RpcKit._String(data, "realname", null));
					principal.setNickname(RpcKit._String(data, "nickname", null));
					principal.setDeptname(RpcKit._String(data, "deptname", null));

					staffs.add(principal);
				}
			}
		}

		return staffs;
	}

	public void validaMyStaffAgentInfo(String udbApi, String agentId, byte[] agentPwdBytes, String publicKey, boolean valid, String... users) {
		if (users == null || users.length == 0) {
			return;
		}
		List<Map<String, Map<String, Boolean>>> updUserMap = new ArrayList<Map<String, Map<String, Boolean>>>(users.length);
		for (String user : users) {
			updUserMap.add(as(user, valid ? VALID : INVALID));
		}
		// 执行同步, 将usrmgr中maintain中的user同步到hiido
		JSONObject result = jsonrpc(udbApi, agentId, agentPwdBytes, publicKey, "updateMyStaffAgentInfo", updUserMap);
		if (RpcKit.code(result) != 1) {
			throw new MessageException(YyudbErrno.SOURCE, YyudbErrno.HIIDO_VALID_FAILED, result.toJSONString());
		}
	}

	public JSONObject jsonrpc(String udbApi, String agentId, byte[] agentPwdBytes, String publicKey, String method) {
		return jsonrpc(udbApi, agentId, agentPwdBytes, publicKey, new JSONRPC2Request(method, null));
	}

	@SuppressWarnings("unchecked")
	public static JSONObject jsonrpc(String udbApi, String agentId, byte[] agentPwdBytes, String publicKey, String method, @SuppressWarnings("rawtypes") List params) {
		return jsonrpc(udbApi, agentId, agentPwdBytes, publicKey, new JSONRPC2Request(method, params, null));
	}

	public static JSONObject jsonrpc(String udbApi, String agentId, byte[] agentPwdBytes, String publicKey, String method, Map<String, Object> params) {
		return jsonrpc(udbApi, agentId, agentPwdBytes, publicKey, new JSONRPC2Request(method, params, null));
	}

	public static String identityString(String timestamp, String agentId, byte[] agentPwdBytes, String publicKey) throws GeneralSecurityException, IOException {
		byte[] cipher = RSAUtils.encryptByPublicKey(agentPwdBytes, publicKey);// 公钥加密
		String cipherText = Base64.encodeBase64String(cipher);
		String md5Text = DigestUtils.md5Hex(agentId + timestamp + cipherText);
		String identityStr = Base64.encodeBase64String((agentId + ";" + timestamp + ";" + cipherText + ";" + md5Text).getBytes());
		return identityStr;
	}

	public static String identityString(String timestamp, String agentId, byte[] agentPwdBytes, String publicKey, String dynamicKey) throws GeneralSecurityException, IOException {
		byte[] cipher = RSAUtils.encryptByPublicKey(agentPwdBytes, publicKey);// 公钥加密
		String cipherText = Base64.encodeBase64String(cipher);
		String md5Text = DigestUtils.md5Hex(agentId + timestamp + cipherText + dynamicKey);
		String identityStr = Base64.encodeBase64String((agentId + ";" + timestamp + ";" + cipherText + ";" + dynamicKey + ";" + md5Text).getBytes());
		return identityStr;
	}

	public static JSONObject jsonrpc(String udbApi, String agentId, byte[] agentPwdBytes, String publicKey, JSONRPC2Request request) {

		try {
			JSONRPC2Session session = new JSONRPC2Session(new URL(udbApi));

			// 选项, 必须接受cookie
			JSONRPC2SessionOptions options = session.getOptions();
			options.acceptCookies(true);
			options.trustAllCerts(true);

			// 时序
			long now = System.currentTimeMillis();

			// 认证, 用户信息保存在cookie
			JSONRPC2Request authReq = new JSONRPC2Request("authenticate", Arrays.<Object> asList(identityString(String.valueOf(now), agentId, agentPwdBytes, publicKey)), 0); // id是int
			JSONRPC2Response authResp = session.send(authReq);
			if (!authResp.indicatesSuccess()) {
				JSONRPC2Error error = authResp.getError();
				throw new MessageException("hiido.authenticate", error.getCode(), error.getMessage());
			}

			request.setID(1); // 统一
			JSONRPC2Response response = session.send(request);
			if (!response.indicatesSuccess()) {
				JSONRPC2Error error = response.getError();
				throw new MessageException("hiido." + request.getMethod(), error.getCode(), error.getMessage());
			}
			return (JSONObject) response.getResult();

		} catch (Exception e) {
			throw new WrappedException(e);
		}
	}

	/******************************************
	 * 辅助工具类
	 ******************************************/
	static class RSAUtils {

		/**
		 * 加密算法RSA
		 */
		public static final String KEY_ALGORITHM = "RSA";

		/**
		 * 签名算法
		 */
		public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

		/**
		 * 获取公钥的key
		 */
		private static final String PUBLIC_KEY = "RSAPublicKey";

		/**
		 * 获取私钥的key
		 */
		private static final String PRIVATE_KEY = "RSAPrivateKey";

		/**
		 * RSA最大加密明文大小
		 */
		private static final int MAX_ENCRYPT_BLOCK = 117;

		/**
		 * RSA最大解密密文大小
		 */
		private static final int MAX_DECRYPT_BLOCK = 128;

		/**
		 * <p>
		 * 生成密钥对(公钥和私钥)
		 * </p>
		 * 
		 * @return
		 * @throws Exception
		 */
		public static Map<String, Object> genKeyPair() throws Exception {
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
			keyPairGen.initialize(1024);
			KeyPair keyPair = keyPairGen.generateKeyPair();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			Map<String, Object> keyMap = new HashMap<String, Object>(2);
			keyMap.put(PUBLIC_KEY, publicKey);
			keyMap.put(PRIVATE_KEY, privateKey);
			return keyMap;
		}

		/**
		 * <p>
		 * 用私钥对信息生成数字签名
		 * </p>
		 * 
		 * @param data
		 *            已加密数据
		 * @param privateKey
		 *            私钥(BASE64编码)
		 * 
		 * @return
		 * @throws Exception
		 */
		public static String sign(byte[] data, String privateKey) throws Exception {
			byte[] keyBytes = Base64.decodeBase64(privateKey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
			signature.initSign(privateK);
			signature.update(data);
			return Base64.encodeBase64String(signature.sign());
		}

		/**
		 * <p>
		 * 校验数字签名
		 * </p>
		 * 
		 * @param data
		 *            已加密数据
		 * @param publicKey
		 *            公钥(BASE64编码)
		 * @param sign
		 *            数字签名
		 * 
		 * @return
		 * @throws Exception
		 * 
		 */
		public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
			byte[] keyBytes = Base64.decodeBase64(publicKey);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			PublicKey publicK = keyFactory.generatePublic(keySpec);
			Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
			signature.initVerify(publicK);
			signature.update(data);
			return signature.verify(Base64.decodeBase64(sign));
		}

		/**
		 * <P>
		 * 私钥解密
		 * </p>
		 * 
		 * @param encryptedData
		 *            已加密数据
		 * @param privateKey
		 *            私钥(BASE64编码)
		 * @return
		 * @throws Exception
		 */
		public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
			byte[] keyBytes = Base64.decodeBase64(privateKey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateK);
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return decryptedData;
		}

		/**
		 * <p>
		 * 公钥解密
		 * </p>
		 * 
		 * @param encryptedData
		 *            已加密数据
		 * @param publicKey
		 *            公钥(BASE64编码)
		 * @return
		 * @throws Exception
		 */
		public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
			byte[] keyBytes = Base64.decodeBase64(publicKey);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key publicK = keyFactory.generatePublic(x509KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, publicK);
			int inputLen = encryptedData.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段解密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
					cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_DECRYPT_BLOCK;
			}
			byte[] decryptedData = out.toByteArray();
			out.close();
			return decryptedData;
		}

		/**
		 * <p>
		 * 公钥加密
		 * </p>
		 * 
		 * @param data
		 *            源数据
		 * @param publicKey
		 *            公钥(BASE64编码)
		 * @return
		 * @throws NoSuchPaddingException
		 * @throws NoSuchAlgorithmException
		 * @throws InvalidKeySpecException
		 * @throws InvalidKeyException
		 * @throws IOException
		 * @throws BadPaddingException
		 * @throws IllegalBlockSizeException
		 * @throws Exception
		 */
		public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws GeneralSecurityException, IOException {
			byte[] keyBytes = Base64.decodeBase64(publicKey);
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key publicK = keyFactory.generatePublic(x509KeySpec);
			// 对数据加密
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicK);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}
			byte[] encryptedData = out.toByteArray();
			out.close();
			return encryptedData;
		}

		/**
		 * <p>
		 * 私钥加密
		 * </p>
		 * 
		 * @param data
		 *            源数据
		 * @param privateKey
		 *            私钥(BASE64编码)
		 * @return
		 * @throws Exception
		 */
		public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
			byte[] keyBytes = Base64.decodeBase64(privateKey);
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
			Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
			Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, privateK);
			int inputLen = data.length;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int offSet = 0;
			byte[] cache;
			int i = 0;
			// 对数据分段加密
			while (inputLen - offSet > 0) {
				if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
					cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
				} else {
					cache = cipher.doFinal(data, offSet, inputLen - offSet);
				}
				out.write(cache, 0, cache.length);
				i++;
				offSet = i * MAX_ENCRYPT_BLOCK;
			}
			byte[] encryptedData = out.toByteArray();
			out.close();
			return encryptedData;
		}

		/**
		 * <p>
		 * 获取私钥
		 * </p>
		 * 
		 * @param keyMap
		 *            密钥对
		 * @return
		 * @throws Exception
		 */
		public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
			Key key = (Key) keyMap.get(PRIVATE_KEY);
			return Base64.encodeBase64String(key.getEncoded());
		}

		/**
		 * <p>
		 * 获取公钥
		 * </p>
		 * 
		 * @param keyMap
		 *            密钥对
		 * @return
		 * @throws Exception
		 */
		public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
			Key key = (Key) keyMap.get(PUBLIC_KEY);
			return Base64.encodeBase64String(key.getEncoded());
		}

	}

	/**********************************
	 * 辅助办法
	 **********************************/
	public static final class RpcKit {
		public static int code(JSONObject result) {
			Number num = (Number) result.get("code");
			return num == null ? -1 : num.intValue();
		}

		public static String msg(JSONObject result) {
			return (String) result.get("msg");
		}

		public static JSONObject dataObject(JSONObject result) {
			Object data = result.get("data");
			return (JSONObject) data;
		}

		public static JSONArray dataArray(JSONObject result) {
			Object data = result.get("data");
			return (JSONArray) data;
		}

		public static JSONObject object(JSONObject result, String... steps) {
			if (steps == null || steps.length == 0) {
				return result;
			}
			Object rt = result;
			for (int i = 0; i < steps.length && rt != null; i++) {
				rt = ((JSONObject) rt).get(steps[i]);
			}
			return (JSONObject) rt;
		}

		public static JSONArray array(JSONObject result, String... steps) {
			if (steps == null || steps.length == 0) {
				return null;
			}
			Object rt = result;
			for (int i = 0; i < steps.length && rt != null; i++) {
				rt = ((JSONObject) rt).get(steps[i]);
			}
			return (JSONArray) rt;
		}

		public static int _int(Map<String, Object> map, String key, int def) {
			Number rt = (Number) map.get(key);
			return rt == null ? def : rt.intValue();
		}

		public static String _String(Map<String, Object> map, String key, String def) {
			Object rt = map.get(key);
			return rt == null ? def : rt.toString();
		}

	}

	public static interface Callback {

		boolean postHiidoLogin(HttpServletRequest request, HttpServletResponse response, String token) throws ServletException, IOException;

		Principal validateAndExtendPrincipal(Wsid wsid) throws IOException;

		void sendError(HttpServletResponse resp, int sc, int errno, String errmsg) throws IOException;
	}
}

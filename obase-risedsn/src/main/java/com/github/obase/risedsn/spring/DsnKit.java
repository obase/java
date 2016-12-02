package com.github.obase.risedsn.spring;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Properties;

import org.springframework.beans.factory.config.TypedStringValue;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

final class DsnKit {

	static final String DEF_DRIVER_CLASS = "com.mysql.jdbc.Driver";
	static final String DEF_JDBC_URL = "jdbc:mysql://${host}:${port}/${default_db}?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull";

	static String getEnviromentProperty(String key, boolean required) {
		String result = System.getProperty(key, System.getenv(key));
		if (isEmpty(result) && required) {
			throw new RuntimeException(String.format("[RISEDEV]升龙数据源初始失败,没有环境变量：%s", key));
		}
		return result;
	}

	static String getEnviromentProperty(Properties props, String key, boolean required) {
		String result = props.getProperty(key);
		if (isEmpty(result) && required) {
			throw new RuntimeException(String.format("[RISEDEV]升龙数据源初始失败,没有环境变量：%s", key));
		}
		return result;
	}

	static String getElementProperty(Element element, String prop, boolean required) {
		String result = element.getAttribute(prop);
		if (isEmpty(result) && required) {
			throw new RuntimeException(String.format("[RISEDEV]升龙数据源初始失败,元素%缺少属性%s", element.getTagName(), prop));
		}
		return result;
	}

	static Element getDwenvElement(Element element, boolean required) {
		NodeList nodeList = element.getElementsByTagNameNS("*", RiseEnvs.DWENV);
		if (nodeList == null || nodeList.getLength() == 0) {
			throw new RuntimeException(String.format("[RISEDEV]升龙数据源初始失败,没有环境配置：%s, 实例ID：%s", RiseEnvs.DWENV, element.getAttribute("id")));
		}
		return (Element) nodeList.item(0);
	}

	static Object getValue(Object val) {
		if (val instanceof TypedStringValue) {
			return ((TypedStringValue) val).getValue();
		}
		return val;
	}

	static WeakReference<Properties> DSN_PROP_REF;

	static synchronized Properties loadDsnProeprties(String key) {
		if (DSN_PROP_REF == null || DSN_PROP_REF.get() == null) {
			Properties props = new Properties();
			File file = new File(RiseEnvs.DSN_PROPERTIES_PATH);
			if (file.exists() && file.isFile()) {
				InputStream fis = null;
				try {
					fis = new BufferedInputStream(new FileInputStream(file));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					int len = 0;
					for (byte[] buf = new byte[1024]; (len = fis.read(buf)) > 0;) {
						baos.write(buf, 0, len);
					}
					ByteArrayInputStream bais = new ByteArrayInputStream(TripleDESCodec.decrypt(key, baos.toByteArray()));
					props.load(bais);
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
			DSN_PROP_REF = new WeakReference<Properties>(props);
		}
		return DSN_PROP_REF.get();
	}

	static boolean existsDsnProperties() {
		File file = new File(RiseEnvs.DSN_PROPERTIES_PATH);
		return (file.exists() && file.isFile());
	}

	static boolean isEmpty(String val) {
		return val == null || val.length() == 0;
	}
	
	/**
	 * return { host, port, pass };
	 */
	static String[] getRedisProperty(String dsn, String key) {
		// 读取测试顺序: 1.新升龙的环境变量. 2.旧升龙的环境变量. 3. 旧升龙的dsn文件
		String hostKey = dsn + "_host";
		String portKey = null;
		String passKey = null;

		String host = getEnviromentProperty(hostKey, false);
		String port = null;
		String pass = null;

		if (!isEmpty(host)) {
			// 新升龙数据源, 由环境变量提供
			portKey = dsn + "_port";
			passKey = dsn + "_password";

			port = getEnviromentProperty(portKey, true);
			pass = getEnviromentProperty(passKey, false);

		} else {
			// 旧升龙数据源
			hostKey = dsn + ".redis.host";
			portKey = dsn + ".redis.port";
			passKey = dsn + ".redis.password";

			host = getEnviromentProperty(hostKey, false);
			if (!isEmpty(host)) {
				// 由环境变量提供
				port = getEnviromentProperty(portKey, true);
				pass = getEnviromentProperty(passKey, false);
			} else {
				// 由dns文件提供
				Properties props = loadDsnProeprties(key);
				host = getEnviromentProperty(props, hostKey, true);
				port = getEnviromentProperty(props, portKey, true);
				pass = getEnviromentProperty(props, passKey, false);
			}
		}

		return new String[] { host, port, pass };
	}
	
	/**
	 * return { host, port, user, pass, db };
	 */
	static String[] getMysqlProperty(String dsn, boolean slave, String key) {
		// 读取测试顺序: 1.新升龙的环境变量. 2.旧升龙的环境变量. 3. 旧升龙的dsn文件
		String hostKey = dsn + (slave ? "_slave_host" : "_host");
		String portKey = null;
		String userKey = null;
		String passKey = null;
		String dbKey = null;

		String host = getEnviromentProperty(hostKey, false);
		String port = null;
		String user = null;
		String pass = null;
		String db = null;

		if (!isEmpty(host)) {
			// 新升龙数据源, 由环境变量提供
			portKey = dsn + "_port";
			userKey = dsn + "_user";
			passKey = dsn + "_password";
			dbKey = dsn + "_default_db";

			port = getEnviromentProperty(portKey, true);
			user = getEnviromentProperty(userKey, true);
			pass = getEnviromentProperty(passKey, true);
			db = getEnviromentProperty(dbKey, true);

		} else {
			// 旧升龙数据源
			hostKey = dsn + (slave ? ".jdbc.slaveHost" : ".jdbc.host");
			portKey = dsn + (slave ? ".jdbc.slavePort" : ".jdbc.port");
			userKey = dsn + ".jdbc.username";
			passKey = dsn + ".jdbc.password";
			dbKey = dsn + ".jdbc.dbname";

			host = getEnviromentProperty(hostKey, false);
			if (!isEmpty(host)) {
				// 由环境变量提供
				port = getEnviromentProperty(portKey, true);
				user = getEnviromentProperty(userKey, true);
				pass = getEnviromentProperty(passKey, true);
				db = getEnviromentProperty(dbKey, true);
			} else {
				// 由dns文件提供
				Properties props = loadDsnProeprties(key);
				host = getEnviromentProperty(props, hostKey, true);
				port = getEnviromentProperty(props, portKey, true);
				user = getEnviromentProperty(props, userKey, true);
				pass = getEnviromentProperty(props, passKey, true);
				db = getEnviromentProperty(props, dbKey, true);
			}
		}
		return new String[] { host, port, user, pass, db };
	}
}

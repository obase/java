package com.github.obase.risedsn.spring;

/**
 * 升龙环境配置
 */
public final class RiseEnvs {

	public static final String SYS_ENV_DWENV = "DWENV";
	public static final String SYS_ENV_DWPROJECTNO = "DWPROJECTNO";

	public static final String DWENV_PROD = "prod";
	public static final String DWENV_TEST = "test";
	public static final String DWENV_DEV = "dev";

	public static final String DWPROJECTNO = getSysEnv(SYS_ENV_DWPROJECTNO);
	public static final String DWENV = getSysEnv(SYS_ENV_DWENV);
	public static final boolean IS_PROD = DWENV_PROD.equalsIgnoreCase(DWENV);
	public static final boolean IS_TEST = DWENV_TEST.equalsIgnoreCase(DWENV);
	public static final boolean IS_DEV = DWENV_DEV.equalsIgnoreCase(DWENV);
	public static final boolean DEBUG = IS_TEST | IS_DEV;

	// 旧数据源路径
	public static final String DSN_PROPERTIES_PATH = "/data/app/" + DWPROJECTNO + "/config/" + DWENV + "/dsn.properties";

	/**
	 * 从System.Properties或System.Enviorments获取
	 * 
	 * @param name,
	 *            属性或环境变量名称
	 * @return
	 */
	public static String getSysEnv(String name) {
		String val = System.getProperty(name);
		if (val == null) {
			val = System.getenv(name);
		}
		return val;
	}

}

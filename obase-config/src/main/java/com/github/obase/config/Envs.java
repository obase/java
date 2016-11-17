package com.github.obase.config;

public final class Envs {

	public static final String SYS_ENV_DWENV = "DWENV";
	public static final String SYS_ENV_DWPROJECTNO = "DWPROJECTNO";
	public static final String SYS_ENV_DWCONTAINER = "DWCONTAINER";

	public static final String DWENV = System.getenv(SYS_ENV_DWENV);
	public static final String DWPROJECTNO = System.getenv(SYS_ENV_DWPROJECTNO);
	public static final String DWCONTAINER = System.getenv(SYS_ENV_DWCONTAINER);

	public static final String DWENV_PROD = "prod";
	public static final String DWENV_TEST = "test";
	public static final String DWENV_DEV = "dev";
	public static final String DWENV_ZAIBEI = "zaibei";
	public static final String DWENV_GRAY = "gray";
	public static final String DWENV_PREV = "prev";
	public static final String DWENV_OTHER = "other";

	public static final boolean IS_PROD = DWENV_PROD.equalsIgnoreCase(DWENV);
	public static final boolean IS_TEST = DWENV_TEST.equalsIgnoreCase(DWENV);
	public static final boolean IS_DEV = DWENV_DEV.equalsIgnoreCase(DWENV);
	public static final boolean IS_ZAIBEI = DWENV_ZAIBEI.equalsIgnoreCase(DWENV);
	public static final boolean IS_GRAY = DWENV_GRAY.equalsIgnoreCase(DWENV);
	public static final boolean IS_PREV = DWENV_PREV.equalsIgnoreCase(DWENV);
	public static final boolean IS_OTHER = DWENV_OTHER.equalsIgnoreCase(DWENV);

	public static final boolean DEBUG = IS_TEST | IS_DEV;

	public static final String DSN_PROPERTIES_PATH = "/data/app/" + DWPROJECTNO + "/config/" + DWENV + "/dsn.properties";
	public static final String APP_PROPERTIES_PATH = "/data/app/" + DWPROJECTNO + "/config/" + DWENV + "/app.properties";
	public static final String ENV_PROPERTIES_PATH = "/data/app/" + DWPROJECTNO + "/config/" + DWENV + "/env.properties";
	public static final String PROJECT_WEBAPP_PATH = "/data/webapps/" + DWPROJECTNO + "/";

	public static String getSystemVariable(String name) {
		String val = System.getProperty(name);
		if (val == null) {
			val = System.getenv(name);
		}
		return val;
	}

}

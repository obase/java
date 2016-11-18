package com.github.obase.test;

import java.io.File;
import java.util.Properties;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public abstract class SpringJUnitTester {

	private static final Log log = LogFactory.getLog(SpringJUnitTester.class);

	public static final String DWPROJECTNO = System.getenv("DWPROJECTNO");
	public static final String DWENV = System.getenv("DWENV");
	public static final String ENV_PROPERTIES = "../config/${DWENV}/env.properties";
	public static final String CWD = new File("").getAbsolutePath();

	@ClassRule
	public static final EnvironmentVariables envs = new EnvironmentVariables();

	@BeforeClass
	public static void processSystemEnvironment() {

		String dwenv = isNotEmpty(DWENV) ? DWENV : "dev";
		loadSystemEnvironment(new File(CWD, ENV_PROPERTIES.replace("${DWENV}", dwenv)));
	}

	public static void loadSystemEnvironment(File envFile) {

		if (!envFile.exists()) {
			return;
		}

		try {
			Properties envProps = PropertiesLoaderUtils.loadProperties(new FileSystemResource(envFile));
			for (String name : envProps.stringPropertyNames()) {
				envs.set(name, envProps.getProperty(name));
			}
		} catch (Exception e) {
			log.error("Load system environment faild: " + envFile.getAbsolutePath() + ", error: " + e.getMessage());
		}
	}

	public static boolean isEmpty(String val) {
		return val == null || val.length() == 0;
	}

	public static boolean isNotEmpty(String val) {
		return val != null && val.length() > 0;
	}

}

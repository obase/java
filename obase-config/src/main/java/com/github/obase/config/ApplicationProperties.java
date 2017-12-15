package com.github.obase.config;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PlaceholderConfigurerSupport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringValueResolver;

import com.github.obase.MessageException;
import com.github.obase.WrappedException;
import com.github.obase.crypto.AES;
import com.github.obase.env.Envs;
import com.github.obase.kit.StringKit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <pre>
 * # 静态配置
 * 
 * ## app.properties, xx.properties
 * 使用locations({DWPROJECTNO}/config/{DWENV}/app.propertes)加载的应用配置. **支持Spring通配符\***
 * ## system.envs
 * 使用System.getenv()加载的环境变量
 * 
 * ## system.properties
 * 使用System.getProperties()加载的系统配置
 * 
 * # 动态配置
 * 
 * ## 来自jdbc query
 * 使用dataSourceRef + query(默认SELECT * FROM `app.properties`)加载的配置项.
 * 
 * ## 来自redis hash
 *  使用jedisPoolRef + hash(默认app.properties)加载的配置项.
 * 
 * # 规则处理
 * 根据rules规则处理加载的配置项,目前支持下述规则.
 * 
 * ## type
 * - boolean
 * - byte
 * - char
 * - short
 * - int
 * - long
 * - float
 * - double
 * - string #默认#
 * 
 * - booleanArray
 * - byteArray
 * - charArray
 * - shorArray
 * - intArray
 * - longArray
 * - floatArray
 * - doubleArray
 * - stringArray
 * 
 * *array**只支持逗号分隔，如果值中包含逗号，请使用string自行分割.
 * 
 * ## required 
 * *错误**只以log记录日志,不影响程序运转.
 * 
 * ## default
 * 如果值为空,自动使用默认值.
 * 
 * ## crpyted + passwd
 * *敏感**配置加密存储,基于AES类进行加解密. 同时提供Windows及Linux下的AES工具:  
 * - windows: http://risedev.yy.com/schema/config/AES.exe
 * 
 * - linux:http://risedev.yy.com/schema/config/AES
 * 
 * *用法**
 * ```
 * AES (-e|-d) (key) (content)
 * ```
 * 参数|作用
 * ---|---
 * -e: |表示加密  
 * -d: |表示解密  
 * key: |表示密码  
 * content: |表示内容  
 * 
 * # 动态配置定期更新
 * 使用timer(单位秒)启动schedule线程,定期更新动态配置(从数据表或缓存).
 * 
 * # 动态配置变更事件
 * 使用addPropertyChangeLister()添加属性变更事件监听器,在值发生改变时触发事件处理.
 * 
 * </pre>
 */
public class ApplicationProperties implements BeanFactoryPostProcessor, BeanNameAware, BeanFactoryAware, ApplicationListener<ContextRefreshedEvent>, InitializingBean, DisposableBean {

	private static final Log logger = LogFactory.getLog(ApplicationProperties.class);

	public static final String DEFAULT_QUERY = "SELECT * FROM `app_properties`";
	public static final String DEFAULT_HASH = "app_properties";
	public static final String DEFAULT_ABSOLUTE_CONFIG_APP_PROPERTIES = Envs.APP_PROPERTIES_PATH;
	public static final String DEFAULT_RELATIVE_CONFIG_APP_PROPERTIES = "../config/" + Envs.DWENV + "/app.properties";
	public static final String VERSION = "version";

	static final Map<String, String> EMPTY_MAP = Collections.emptyMap();
	static final Properties EMPTY_PROPS = new Properties();

	// 静态配置
	String locations; // app配置路径,支持多值,用逗号分隔

	boolean ignoreSystemEnvironment; // 是否忽略System.getenv()变量
	boolean ignoreSystemProperties; // 是否忽略System.getProperties()变量
	boolean ignorePropertyPlaceholder; // 是否忽略PropertySourcePlaceholderResolver功能
	boolean ignoreUnresolvablePlaceholder; // 是 否忽略不能解析的占位符
	boolean fatalIfError; // 如果错误,直接抛出异常.默认输出错误日志.

	// 动态配置
	String dataSourceRef;
	String query; // 数据表查询sql
	String jedisPoolRef;
	String hash; // Redis的hash数据结构
	String rules; // 约束规则的xml路径
	int timer; // 定时器,单位秒

	public void setLocations(String locations) {
		this.locations = locations;
	}

	public void setIgnorePropertyPlaceholder(boolean ignorePropertyPlaceholder) {
		this.ignorePropertyPlaceholder = ignorePropertyPlaceholder;
	}

	public void setIgnoreUnresolvablePlaceholder(boolean ignoreUnresolvablePlaceholder) {
		this.ignoreUnresolvablePlaceholder = ignoreUnresolvablePlaceholder;
	}

	public void setIgnoreSystemEnvironment(boolean ignoreSystemEnvironment) {
		this.ignoreSystemEnvironment = ignoreSystemEnvironment;
	}

	public void setIgnoreSystemProperties(boolean ignoreSystemProperties) {
		this.ignoreSystemProperties = ignoreSystemProperties;
	}

	public void setDataSourceRef(String dataSourceRef) {
		this.dataSourceRef = dataSourceRef;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setJedisPoolRef(String jedisPoolRef) {
		this.jedisPoolRef = jedisPoolRef;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public void setFatalIfError(boolean fatalIfError) {
		this.fatalIfError = fatalIfError;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	String beanName;
	BeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	Rules checkRules;
	DataSource dataSource;
	JedisPool jedisPool;
	ScheduledExecutorService service;

	// 静态配置
	Map<String, String> systemEnvironment = EMPTY_MAP;
	Properties systemProperties = EMPTY_PROPS;
	final Map<String, String> statics = new HashMap<String, String>();
	// 动态配置，可能为空
	final Map<String, String> dynamic = new ConcurrentHashMap<String, String>(); // 存储动态配置string值
	final Map<String, List<PropertyChangeListener>> propertyChangeListenerMap = new ConcurrentHashMap<String, List<PropertyChangeListener>>();

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		List<PropertyChangeListener> list = propertyChangeListenerMap.get(propertyName);
		if (list == null) {
			list = new LinkedList<PropertyChangeListener>();
			propertyChangeListenerMap.put(propertyName, list);
		}
		list.add(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		List<PropertyChangeListener> list = propertyChangeListenerMap.get(propertyName);
		if (list != null) {
			list.remove(listener);
			if (list.size() == 0) { // 级联清空
				propertyChangeListenerMap.remove(propertyName);
			}
		}
	}

	public void removePropertyChangeListener(String propertyName) {
		propertyChangeListenerMap.remove(propertyName);
	}

	/**
	 * Initial component
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		// 先解析
		if (StringKit.isNotEmpty(rules)) {
			try {
				// 默认取第一个
				Resource rs = resolver.getResources(rules)[0];
				checkRules = RulesSAXParser.parse(rs);
			} catch (Exception e) {
				if (fatalIfError) {
					throw new WrappedException("[ApplicationProperties] parse check rules failed: " + rules, e);
				} else {
					logger.error("[ApplicationProperties] parse check rules failed: " + rules + ", error: " + e.getMessage());
				}
			}
		}

		// 环境变量
		if (!ignoreSystemEnvironment) {
			systemEnvironment = System.getenv();
		}
		// 系统配置
		if (!ignoreSystemProperties) {
			systemProperties = System.getProperties();
		}

		// 静态配置
		try {
			if (StringKit.isEmpty(locations)) {
				FileSystemResource fsr = new FileSystemResource(DEFAULT_ABSOLUTE_CONFIG_APP_PROPERTIES);
				if (!fsr.exists()) {
					fsr = new FileSystemResource(DEFAULT_RELATIVE_CONFIG_APP_PROPERTIES);
				}
				if (fsr.exists()) {
					for (Map.Entry<Object, Object> entry : PropertiesLoaderUtils.loadProperties(fsr).entrySet()) {
						statics.put((String) entry.getKey(), (String) entry.getValue());
					}
				}
			} else {
				String[] arr = StringKit.split(locations, ',', true);
				if (arr != null && arr.length > 0) {
					Properties temp = new Properties();
					for (String loc : arr) {
						Resource[] rscs = resolver.getResources(loc);
						if (rscs != null) {
							for (Resource rsc : rscs) {
								if (rsc.exists())
									PropertiesLoaderUtils.fillProperties(temp, rsc);
							}
						}
					}

					for (Map.Entry<Object, Object> entry : temp.entrySet()) {
						statics.put((String) entry.getKey(), (String) entry.getValue());
					}
				}
			}
		} catch (IOException e) {
			if (fatalIfError) {
				throw new WrappedException("[ApplicationProperties] load statics configuration failed: " + locations, e);
			} else {
				logger.error("[ApplicationProperties] load statics configuration failed: " + locations + ", error: " + e.getMessage());
			}
		}

		// 处理规则
		if (statics.size() > 0 && checkRules != null) {
			for (Rule rule : checkRules.rules) {
				String val = statics.get(rule.name);
				if (StringKit.isEmpty(val)) {
					val = rule.default_;
				} else if (rule.crypted) {
					try {
						val = AES.decrypt(rule.passwd, val);
					} catch (Exception e) {
						if (fatalIfError) {
							throw new WrappedException("[ApplicationProperties] decrpty property failed: " + rule.name, e);
						} else {
							logger.error("[ApplicationProperties] decrpty property failed: " + rule.name + ", error: " + e.getMessage());
						}
					}
				}
				statics.put(rule.name, val);
			}
		}

	}

	// 静态配置处理
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		// 替换配置${...}
		if (!ignorePropertyPlaceholder) {

			final SimplePropertyResolver propertyResolver = new SimplePropertyResolver(statics, systemProperties, systemEnvironment);
			propertyResolver.setPlaceholderPrefix(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX);
			propertyResolver.setPlaceholderSuffix(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX);
			propertyResolver.setValueSeparator(PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR);

			StringValueResolver valueResolver = new StringValueResolver() {
				@Override
				public String resolveStringValue(String strVal) {
					String resolved = ignoreUnresolvablePlaceholder ? propertyResolver.resolvePlaceholders(strVal) : propertyResolver.resolveRequiredPlaceholders(strVal);
					return StringKit.isEmpty(resolved) ? null : resolved.trim();
				}
			};

			BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);
			String[] beanNames = beanFactory.getBeanDefinitionNames();
			for (String curName : beanNames) {
				if (!(curName.equals(this.beanName) && beanFactory.equals(this.beanFactory))) {
					BeanDefinition bd = beanFactory.getBeanDefinition(curName);
					try {
						visitor.visitBeanDefinition(bd);
					} catch (Exception ex) {
						throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage(), ex);
					}
				}
			}

			beanFactory.resolveAliases(valueResolver);
			beanFactory.addEmbeddedValueResolver(valueResolver);
		}

	}

	// 动态配置处理
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (service != null) {
			service.shutdownNow();
		}
		ApplicationContext appCtx = event.getApplicationContext();
		if (StringKit.isNotEmpty(dataSourceRef)) {
			dataSource = appCtx.getBean(dataSourceRef, DataSource.class);
		}
		if (StringKit.isNotEmpty(jedisPoolRef)) {
			jedisPool = appCtx.getBean(jedisPoolRef, JedisPool.class);
		}
		if (dataSource != null || jedisPool != null) {

			updateDynamicConfiguration(dataSource, jedisPool);

			if (timer > 0) {
				service = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setDaemon(true);
						return t;
					}
				});
				service.scheduleWithFixedDelay(new Runnable() {
					@Override
					public void run() {
						updateDynamicConfiguration(dataSource, jedisPool);
					}
				}, timer, timer, TimeUnit.SECONDS);
				service.shutdown();
			}
		} else {
			// check required at last...
			if (checkRules != null) {
				for (Rule rule : checkRules.rules) {
					String val = getNotDynamic(rule.name);
					if (rule.required && StringKit.isEmpty(val)) {
						if (fatalIfError) {
							throw new MessageException(ConfigErrno.SOURCE, ConfigErrno.PROPERTY_REQUIRED, "[ApplicationProperties] application property required: " + rule.name);
						} else {
							logger.error("[ApplicationProperties] application property required: " + rule.name);
						}
					}
				}
			}
		}

	}

	// manual invoke to updating...
	public void updateDynamicConfiguration() {
		if (this.dataSource != null || this.jedisPool != null) {
			this.updateDynamicConfiguration(this.dataSource, this.jedisPool);
		}
	}

	private void updateDynamicConfiguration(DataSource dataSource, JedisPool jedisPool) {

		logger.debug("[ApplicationProperties] update dynamic configuration...");

		Map<String, String> _dynamic = new HashMap<String, String>(); // 原始值

		boolean updated = false;
		Map<String, String> tmp;
		if ((tmp = loadDynamicConfigurationFromQuery(dataSource, query)) != null) {
			_dynamic.putAll(tmp);
			updated = true;
		}

		if ((tmp = loadDynamicConfigurationFromHash(jedisPool, hash)) != null) {
			_dynamic.putAll(tmp);
			updated = true;
		}

		// 只有版本变化才需要更新
		if (updated) {
			// process type and required in the rule
			if (checkRules != null) {
				for (Rule rule : checkRules.rules) {

					String val = _dynamic.get(rule.name);
					if (StringKit.isEmpty(val)) {
						val = getNotDynamic(rule.name);
						if (StringKit.isEmpty(val)) {
							if (StringKit.isNotEmpty(rule.default_)) {
								val = rule.default_;
							}
						}
					} else if (rule.crypted) {
						try {
							val = AES.decrypt(rule.passwd, val);
						} catch (Exception e) {
							if (fatalIfError) {
								throw new WrappedException("[ApplicationProperties] decrpty property failed: " + rule.name, e);
							} else {
								logger.error("[ApplicationProperties] decrpty property failed: " + rule.name + ", error: " + e.getMessage());
							}
						}
					}
					if (rule.required && StringKit.isEmpty(val)) {
						if (fatalIfError) {
							throw new MessageException(ConfigErrno.SOURCE, ConfigErrno.PROPERTY_REQUIRED, "[ApplicationProperties] application property required: " + rule.name);
						} else {
							logger.error("[ApplicationProperties] application property required: " + rule.name);
						}
					}

					_dynamic.put(rule.name, val);
				}
			}

			Set<String> set = new HashSet<String>();

			// 更新旧值并触发change事件
			set.addAll(_dynamic.keySet());
			set.retainAll(this.dynamic.keySet()); // 求同集
			for (String key : set) {
				String nval = _dynamic.get(key);
				String oval = this.dynamic.get(key);
				if (!StringKit.equals(nval, oval)) {
					List<PropertyChangeListener> listeners = propertyChangeListenerMap.get(key);
					if (listeners != null) {
						PropertyChangeEvent evt = new PropertyChangeEvent(this.dynamic, key, oval, nval);
						for (PropertyChangeListener listener : listeners) {
							listener.propertyChange(evt);
						}
					}
				}
			}

			// 添加新值
			set.addAll(_dynamic.keySet());
			set.removeAll(this.dynamic.keySet());
			for (String key : set) {
				this.dynamic.put(key, _dynamic.get(key));
			}
		}
	}

	private Map<String, String> loadDynamicConfigurationFromQuery(DataSource dataSource, String query) {

		if (dataSource == null) {
			return null;
		}

		if (StringKit.isEmpty(query)) {
			query = DEFAULT_QUERY;
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			Map<String, String> tmp = new HashMap<String, String>();
			while (rs.next()) {
				tmp.put(rs.getString(1), rs.getString(2)); // 固定第1,2个字段
			}

			// 版本发生变化才会更新
			if (!StringKit.equals(tmp.get(VERSION), dynamic.get(VERSION))) {
				return tmp;
			}
			return null;
		} catch (Exception e) {
			logger.error("[ApplicationProperties] load dynamic configuration from query failed: " + query + ", error: " + e.getMessage());
			return null;
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("[ApplicationProperties] close resultset failed" + ", error: " + e.getMessage());
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					logger.error("[ApplicationProperties] close statement failed" + ", error: " + e.getMessage());
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("[ApplicationProperties] close connection failed" + ", error: " + e.getMessage());
				}
			}
		}

	}

	private Map<String, String> loadDynamicConfigurationFromHash(JedisPool jedisPool, String hash) {

		if (jedisPool == null) {
			return null;
		}

		if (StringKit.isEmpty(hash)) {
			hash = DEFAULT_HASH;
		}

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Map<String, String> kvs = jedis.hgetAll(hash);
			if (kvs != null && !StringKit.equals(kvs.get(VERSION), dynamic.get(VERSION))) {
				return kvs;
			}
			return null;
		} catch (Exception e) {
			logger.error("[ApplicationProperties] load dynamic configuration from hash failed: " + hash + ", error: " + e.getMessage());
			return null;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		if (service != null) {
			service.shutdownNow();
		}
	}

	private String getNotDynamic(String key) {
		String val = statics.get(key);
		if (val != null) {
			return val;
		}
		val = systemProperties.getProperty(key);
		if (val != null) {
			return val;
		}
		return systemEnvironment.get(key);
	}

	public String get(String key) {
		String val = dynamic.get(key);
		if (val == null) {
			val = getNotDynamic(key);
		}
		return val;
	}

	public boolean containsNotDynamic(String key) {
		if (dynamic != null) {
			if (dynamic.containsKey(key)) {
				return true;
			}
		}
		if (statics.containsKey(key)) {
			return true;
		}
		if (systemProperties.containsKey(key)) {
			return true;
		}
		return systemEnvironment.containsKey(key);
	}

	public boolean contains(String key) {
		return dynamic.containsKey(key) || containsNotDynamic(key);
	}

}

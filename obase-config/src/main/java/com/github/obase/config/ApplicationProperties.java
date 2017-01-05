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
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import com.github.obase.env.Envs;
import com.github.obase.MessageException;
import com.github.obase.WrappedException;
import com.github.obase.crypto.AES;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * <pre>
# 静态配置

## app.properties, xx.properties
使用locations({DWPROJECTNO}/config/{DWENV}/app.propertes)加载的应用配置. **支持Spring通配符\***
## system.envs
使用System.getenv()加载的环境变量

## system.properties
使用System.getProperties()加载的系统配置

# 动态配置

## 来自jdbc query
使用dataSourceRef + query(默认SELECT * FROM `app.properties`)加载的配置项.

## 来自redis hash
* 使用jedisPoolRef + hash(默认app.properties)加载的配置项.

# 规则处理
根据rules规则处理加载的配置项,目前支持下述规则.

## type
- boolean
- byte
- char
- short
- int
- long
- float
- double
- string #默认#

- booleanArray
- byteArray
- charArray
- shorArray
- intArray
- longArray
- floatArray
- doubleArray
- stringArray

**array**只支持逗号分隔，如果值中包含逗号，请使用string自行分割.

## required 
**错误**只以log记录日志,不影响程序运转.

## default
如果值为空,自动使用默认值.

## crpyted + passwd
**敏感**配置加密存储,基于AES类进行加解密. 同时提供Windows及Linux下的AES工具:  
- windows: http://risedev.yy.com/schema/config/AES.exe

- linux:http://risedev.yy.com/schema/config/AES

**用法**
```
AES (-e|-d) (key) (content)
```
参数|作用
---|---
-e: |表示加密  
-d: |表示解密  
key: |表示密码  
content: |表示内容  

# 动态配置定期更新
使用timer(单位秒)启动schedule线程,定期更新动态配置(从数据表或缓存).

# 动态配置变更事件
使用addPropertyChangeLister()添加属性变更事件监听器,在值发生改变时触发事件处理.
 * 
 * </pre>
 */
public class ApplicationProperties implements BeanFactoryPostProcessor, BeanNameAware, BeanFactoryAware, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

	private static final Log logger = LogFactory.getLog(ApplicationProperties.class);

	public static final String DEFAULT_QUERY = "SELECT * FROM `app_properties`";
	public static final String DEFAULT_HASH = "app_properties";
	public static final String DEFAULT_ABSOLUTE_CONFIG_APP_PROPERTIES = Envs.APP_PROPERTIES_PATH;
	public static final String DEFAULT_RELATIVE_CONFIG_APP_PROPERTIES = "../config/" + Envs.DWENV + "/app.properties";

	static final Map<String, String> EMPTY_MAP = Collections.emptyMap();
	static final Properties EMPTY_PROPS = new Properties();

	// 静态配置
	String locations; // app配置路径,支持多值,用逗号分隔

	boolean ignoreSystemEnvironment; // 是否忽略System.getenv()变量
	boolean ignoreSystemProperties = !Envs.IS_DEV; // 是否忽略System.getProperties()变量
	boolean ignorePropertyPlaceholder; // 是否忽略PropertySourcePlaceholderResolver功能
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

	public void setService(ScheduledExecutorService service) {
		this.service = service;
	}

	public void setSystemEnvironment(Map<String, String> systemEnvironment) {
		this.systemEnvironment = systemEnvironment;
	}

	public void setSystemProperties(Properties systemProperties) {
		this.systemProperties = systemProperties;
	}

	public void setStatics(Map<String, String> statics) {
		this.statics = statics;
	}

	public void setObjects(Map<String, Object> objects) {
		this.objects = objects;
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
	Map<String, String> statics = EMPTY_MAP;
	// 动态配置，可能为空
	volatile Map<String, String> dynamic; // 存储动态配置string值
	volatile Map<String, Object> objects; // 存储动态配置根据rule转换后的值

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

	// 静态配置处理
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		// 先解析
		if (!StringUtils.isEmpty(rules)) {
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
		statics = new HashMap<String, String>();
		try {
			if (StringUtils.isEmpty(locations)) {
				FileSystemResource fsr = new FileSystemResource(DEFAULT_ABSOLUTE_CONFIG_APP_PROPERTIES);
				if (!fsr.exists() && Envs.IS_DEV) {
					fsr = new FileSystemResource(DEFAULT_RELATIVE_CONFIG_APP_PROPERTIES);
				}
				if (fsr.exists()) {
					for (Map.Entry<Object, Object> entry : PropertiesLoaderUtils.loadProperties(fsr).entrySet()) {
						statics.put((String) entry.getKey(), (String) entry.getValue());
					}
				}
			} else {
				String[] arr = StringUtils.tokenizeToStringArray(locations, ",");
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
				if (StringUtils.isEmpty(val)) {
					if (!StringUtils.isEmpty(rule.default_)) {
						statics.put(rule.name, val = rule.default_);
					}
				} else if (rule.crypted) {
					try {
						statics.put(rule.name, val = AES.decrypt(rule.passwd, val));
					} catch (Exception e) {
						if (fatalIfError) {
							throw new WrappedException("[ApplicationProperties] decrpty property failed: " + rule.name, e);
						} else {
							logger.error("[ApplicationProperties] decrpty property failed: " + rule.name + ", error: " + e.getMessage());
						}
					}
				}
			}
		}

		// 替换配置${...}
		if (!ignorePropertyPlaceholder) {

			final SimplePropertyResolver propertyResolver = new SimplePropertyResolver(statics, systemProperties, systemEnvironment);
			propertyResolver.setPlaceholderPrefix(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_PREFIX);
			propertyResolver.setPlaceholderSuffix(PlaceholderConfigurerSupport.DEFAULT_PLACEHOLDER_SUFFIX);
			propertyResolver.setValueSeparator(PlaceholderConfigurerSupport.DEFAULT_VALUE_SEPARATOR);

			StringValueResolver valueResolver = new StringValueResolver() {
				@Override
				public String resolveStringValue(String strVal) {
					String resolved = propertyResolver.resolvePlaceholders(strVal);
					return StringUtils.isEmpty(resolved) ? null : resolved.trim();
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
		if (!StringUtils.isEmpty(dataSourceRef)) {
			dataSource = appCtx.getBean(dataSourceRef, DataSource.class);
		}
		if (!StringUtils.isEmpty(jedisPoolRef)) {
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
			}
		} else {
			// check required at last...
			if (checkRules != null) {
				for (Rule rule : checkRules.rules) {
					String val = coalesceNotDynamic(rule.name);
					if (rule.required && StringUtils.isEmpty(val)) {
						if (fatalIfError) {
							throw new MessageException(ConfigErrno.SOURCE, ConfigErrno.PROPERTY_REQUIRED, "[ApplicationProperties] application property required: " + rule.name);
						} else {
							logger.error("[ApplicationProperties] application property required: " + rule.name);
						}
					}
					if (rule.type != Type.String && !StringUtils.isEmpty(val)) {
						try {
							Object obj = Type.parseType(rule.type, val);
							// initial
							if (objects == null) {
								objects = new HashMap<String, Object>();
							}
							objects.put(rule.name, obj);
						} catch (Exception e) {
							logger.error("[ApplicationProperties] parse type value failed: type=" + rule.type + ", val=" + val + ", error=" + e.getMessage());
							continue;
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

		Map<String, String> _dynamic = new HashMap<String, String>();
		Map<String, Object> _objects = new HashMap<String, Object>();

		if (dataSource != null) {
			loadDynamicConfigurationFromQuery(_dynamic, dataSource, query);
		}

		if (jedisPool != null) {
			loadDynamicConfigurationFromHash(_dynamic, jedisPool, hash);
		}

		// process type and required in the rule
		if (_dynamic.size() > 0 && checkRules != null) {
			for (Rule rule : checkRules.rules) {

				String val = _dynamic.get(rule.name);
				if (StringUtils.isEmpty(val)) {
					val = coalesceNotDynamic(rule.name);
					if (StringUtils.isEmpty(val)) {
						if (!StringUtils.isEmpty(rule.default_)) {
							_dynamic.put(rule.name, val = rule.default_);
						}
					} else {
						_dynamic.put(rule.name, val);
					}
				} else if (rule.crypted) {
					try {
						_dynamic.put(rule.name, val = AES.decrypt(rule.passwd, val));
					} catch (Exception e) {
						if (fatalIfError) {
							throw new WrappedException("[ApplicationProperties] decrpty property failed: " + rule.name, e);
						} else {
							logger.error("[ApplicationProperties] decrpty property failed: " + rule.name + ", error: " + e.getMessage());
						}
					}
				}
				if (rule.required && StringUtils.isEmpty(val)) {
					if (fatalIfError) {
						throw new MessageException(ConfigErrno.SOURCE, ConfigErrno.PROPERTY_REQUIRED, "[ApplicationProperties] application property required: " + rule.name);
					} else {
						logger.error("[ApplicationProperties] application property required: " + rule.name);
					}
				}
				if (rule.type != Type.String && !StringUtils.isEmpty(val)) {
					try {
						Object obj = Type.parseType(rule.type, val);
						_objects.put(rule.name, obj);
					} catch (Exception e) {
						logger.error("[ApplicationProperties] parse type value failed: type=" + rule.type + ", val=" + val + ", error=" + e.getMessage());
						continue;
					}
				}
			}
		}

		// replace the reference
		Map<String, String> oldmap = this.dynamic;
		this.dynamic = _dynamic;
		this.objects = _objects;

		// trigger change events
		if (propertyChangeListenerMap.size() > 0) {
			Set<String> allKeys = new HashSet<String>(Math.max(oldmap.size(), this.dynamic.size()) + 64);
			allKeys.addAll(oldmap.keySet());
			allKeys.addAll(this.dynamic.keySet());
			allKeys.containsAll(propertyChangeListenerMap.keySet());
			for (String key : allKeys) {
				String oldVal = oldmap.get(key);
				String newVal = _dynamic.get(key);
				if (oldVal == null || newVal == null || !oldVal.equals(newVal)) {
					PropertyChangeEvent evt = new PropertyChangeEvent(this.dynamic, key, oldVal, newVal);
					for (PropertyChangeListener listener : propertyChangeListenerMap.get(key)) {
						listener.propertyChange(evt);
					}
				}
			}
		}
	}

	private void loadDynamicConfigurationFromQuery(Map<String, String> props, DataSource dataSource, String query) {

		if (StringUtils.isEmpty(query)) {
			query = DEFAULT_QUERY;
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);

			while (rs.next()) {
				props.put(rs.getString(1), rs.getString(2)); // 固定第1,2个字段
			}

		} catch (Exception e) {
			logger.error("[ApplicationProperties] load dynamic configuration from query failed: " + query + ", error: " + e.getMessage());
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

	private void loadDynamicConfigurationFromHash(Map<String, String> props, JedisPool jedisPool, String hash) {
		if (StringUtils.isEmpty(hash)) {
			hash = DEFAULT_HASH;
		}

		Jedis jedis = null;
		try {
			jedis = jedisPool.getResource();
			Map<String, String> kvs = jedis.hgetAll(hash);
			if (kvs != null && kvs.size() > 0) {
				props.putAll(kvs);
			}
		} catch (Exception e) {
			logger.error("[ApplicationProperties] load dynamic configuration from hash failed: " + hash + ", error: " + e.getMessage());
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

	private String coalesceNotDynamic(String key) {
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

	public boolean contains(String key) {
		if (dynamic != null) {
			if (dynamic.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	// 提供类型转换等一些辅助性方法
	public String getString(String key) {
		if (dynamic != null) {
			String val = dynamic.get(key);
			if (val != null) {
				return val;
			}
		}
		return null;
	}

	public Boolean getBoolean(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Boolean) {
				return (Boolean) val;
			}
		}
		return null;
	}

	public Integer getInteger(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Integer) {
				return (Integer) val;
			}
		}
		return null;
	}

	public Long getLong(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Long) {
				return (Long) val;
			}
		}
		return null;
	}

	public Double getDouble(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Double) {
				return (Double) val;
			}
		}
		return null;
	}

	public String[] getStringArray(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof String[]) {
				return (String[]) val;
			}
		}
		return null;
	}

	public Boolean[] getBooleanArray(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Boolean[]) {
				return (Boolean[]) val;
			}
		}
		return null;
	}

	public Integer[] getIntegerArray(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Integer[]) {
				return (Integer[]) val;
			}
		}
		return null;
	}

	public Long[] getLongArray(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Integer[]) {
				return (Long[]) val;
			}
		}
		return null;
	}

	public Double[] getDoubleArray(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Integer[]) {
				return (Double[]) val;
			}
		}
		return null;
	}

	public boolean contains2(String key) {
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

	// 提供类型转换等一些辅助性方法
	public String getString2(String key) {
		if (dynamic != null) {
			String val = dynamic.get(key);
			if (val != null) {
				return val;
			}
		}
		return coalesceNotDynamic(key);
	}

	public Boolean getBoolean2(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Boolean) {
				return (Boolean) val;
			}
		}
		return (Boolean) Type.parseType(Type.Boolean, getString(key));
	}

	public Integer getInteger2(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Integer) {
				return (Integer) val;
			}
		}
		return (Integer) Type.parseType(Type.Integer, getString(key));
	}

	public Long getLong2(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Long) {
				return (Long) val;
			}
		}
		return (Long) Type.parseType(Type.Long, getString(key));
	}

	public Double getDouble2(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Double) {
				return (Double) val;
			}
		}
		return (Double) Type.parseType(Type.Double, getString(key));
	}

	public String[] getStringArray2(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof String[]) {
				return (String[]) val;
			}
		}
		return (String[]) Type.parseType(Type.StringArray, getString(key));
	}

	public Boolean[] getBooleanArray2(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Boolean[]) {
				return (Boolean[]) val;
			}
		}
		return (Boolean[]) Type.parseType(Type.BooleanArray, getString(key));
	}

	public Integer[] getIntegerArray2(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Integer[]) {
				return (Integer[]) val;
			}
		}
		return (Integer[]) Type.parseType(Type.IntegerArray, getString(key));
	}

	public Long[] getLongArray2(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Integer[]) {
				return (Long[]) val;
			}
		}
		return (Long[]) Type.parseType(Type.LongArray, getString(key));
	}

	public Double[] getDoubleArray2(String key) {
		if (objects != null) {
			Object val = objects.get(key);
			if (val instanceof Integer[]) {
				return (Double[]) val;
			}
		}
		return (Double[]) Type.parseType(Type.DoubleArray, getString(key));
	}

}

package com.github.obase.config;

import java.util.Collections;
import java.util.Map;

import org.springframework.core.env.AbstractPropertyResolver;
import org.springframework.util.ClassUtils;

import com.github.obase.WrappedException;

@SuppressWarnings({ "unchecked", "rawtypes" })
final class SimplePropertyResolver extends AbstractPropertyResolver {

	static final Map NULL = Collections.emptyMap();

	final Map statics;
	final Map systemProperties;
	final Map systemEnvironment;

	public SimplePropertyResolver(Map statics, Map systemProperties, Map systemEnvironment) {
		this.statics = statics == null ? NULL : statics;
		this.systemProperties = systemProperties == null ? NULL : systemProperties;
		this.systemEnvironment = systemEnvironment == null ? NULL : systemEnvironment;
	}

	@Override
	public boolean containsProperty(String key) {
		if (statics.containsKey(key)) {
			return true;
		} else if (systemProperties.containsKey(key)) {
			return true;
		} else if (systemEnvironment.containsKey(key)) {
			return true;
		}
		return false;
	}

	@Override
	public String getProperty(String key) {
		String val = (String) statics.get(key);
		if (val != null) {
			return val;
		}
		val = (String) systemProperties.get(key);
		if (val != null) {
			return val;
		}
		val = (String) systemEnvironment.get(key);
		return val;
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType) {
		String value = getProperty(key);
		if (value != null) {
			value = resolveNestedPlaceholders(value);

			if (!getConversionService().canConvert(String.class, targetType)) {
				throw new IllegalArgumentException(String.format("Cannot convert value [%s] from source type [%s] to target type [%s]", value, String.class.getSimpleName(), targetType.getSimpleName()));
			}
			return getConversionService().convert(value, targetType);
		}
		return null;
	}

	@Override
	public <T> Class<T> getPropertyAsClass(String key, Class<T> targetType) {

		String value = getProperty(key);
		if (value != null) {
			Class<?> clazz;
			try {
				clazz = ClassUtils.forName((String) value, null);
			} catch (Exception ex) {
				throw new WrappedException(ex);
			}
			return targetType.isAssignableFrom(clazz) ? (Class<T>) clazz : null;
		}
		return null;
	}

	@Override
	protected String getPropertyAsRawString(String key) {
		return getProperty(key);
	}

}

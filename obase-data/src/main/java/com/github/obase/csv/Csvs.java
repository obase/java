package com.github.obase.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

public final class Csvs {

	private Csvs() {
	}

	private static final TypeFactory TF = TypeFactory.defaultInstance();
	private static final CsvMapper OM = new CsvMapper();
	static {
		OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		OM.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		OM.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		OM.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
	}

	public static final JavaType Object = TF.constructParametricType(HashMap.class, String.class, Object.class);

	public static final JavaType ObjectList = TF.constructParametricType(List.class, Object);

	public static CsvMapper getCsvMapper() {
		return OM;
	}

	public static final Map<String, Object> readObject(String xml) {
		try {
			return OM.readValue(xml, Object);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final List<Map<String, Object>> readObjectList(String xml) {
		try {
			return OM.readValue(xml, ObjectList);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final <T> T readValue(String xml, Class<T> type) {
		try {
			return OM.readValue(xml, type);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final <T> T readGeneric(String xml, Class<?> parametrized, Class<?>... parameterClasses) {
		try {
			return OM.readValue(xml, TF.constructParametricType(parametrized, parameterClasses));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final Map<String, Object> readObject(byte[] xml) {
		try {
			return OM.readValue(xml, Object);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final List<Map<String, Object>> readObjectList(byte[] xml) {
		try {
			return OM.readValue(xml, ObjectList);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final <T> T readValue(byte[] xml, Class<T> type) {
		try {
			return OM.readValue(xml, type);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final <T> T readGeneric(byte[] xml, Class<?> parametrized, Class<?>... parameterClasses) {
		try {
			return OM.readValue(xml, TF.constructParametricType(parametrized, parameterClasses));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final Map<String, Object> readObject(Reader in) {
		try {
			return OM.readValue(in, Object);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final List<Map<String, Object>> readObjectList(Reader in) {
		try {
			return OM.readValue(in, ObjectList);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final <T> T readValue(Reader in, Class<T> type) {
		try {
			return OM.readValue(in, type);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final <T> T readGeneric(Reader in, Class<?> parametrized, Class<?>... parameterClasses) {
		try {
			return OM.readValue(in, TF.constructParametricType(parametrized, parameterClasses));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final List<Map<String, Object>> readObjectList(InputStream in) {
		try {
			return OM.readValue(in, ObjectList);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final <T> T readValue(InputStream in, Class<T> type) {
		try {
			return OM.readValue(in, type);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final <T> T readGeneric(InputStream in, Class<?> parametrized, Class<?>... parameterClasses) {
		try {
			return OM.readValue(in, TF.constructParametricType(parametrized, parameterClasses));
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final String writeAsString(Object value) {
		try {
			return OM.writeValueAsString(value);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final byte[] writeAsBytes(Object value) {
		try {
			return OM.writeValueAsBytes(value);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final void writeValue(Writer out, Object value) {
		try {
			OM.writeValue(out, value);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static final void writeValue(OutputStream out, Object value) {
		try {
			OM.writeValue(out, value);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

}

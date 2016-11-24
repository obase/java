package com.github.obase.config;

import org.springframework.util.StringUtils;

enum Type {

	String, StringArray, Integer, Long, Double, Boolean, IntegerArray, LongArray, DoubleArray, BooleanArray;

	public static final String ARRAY_VALUE_SEPARATOR = ",";

	public static Object parseType(Type type, String val) {

		switch (type) {
		case String:
			return val;
		case StringArray:
			return StringUtils.tokenizeToStringArray(val, ARRAY_VALUE_SEPARATOR);
		case Integer:
			return java.lang.Integer.valueOf(val);
		case Long:
			return java.lang.Long.valueOf(val);
		case Double:
			return java.lang.Double.valueOf(val);
		case Boolean:
			return java.lang.Boolean.valueOf(val);
		case IntegerArray:
			String[] arr = StringUtils.tokenizeToStringArray(val, ARRAY_VALUE_SEPARATOR);
			Integer[] ia = new Integer[arr.length];
			for (int i = 0; i < arr.length; i++) {
				ia[i] = java.lang.Integer.valueOf(arr[i]);
			}
			return ia;
		case LongArray:
			arr = StringUtils.tokenizeToStringArray(val, ARRAY_VALUE_SEPARATOR);
			Long[] la = new Long[arr.length];
			for (int i = 0; i < arr.length; i++) {
				la[i] = java.lang.Long.valueOf(arr[i]);
			}
			return la;
		case DoubleArray:
			arr = StringUtils.tokenizeToStringArray(val, ARRAY_VALUE_SEPARATOR);
			Double[] da = new Double[arr.length];
			for (int i = 0; i < arr.length; i++) {
				da[i] = java.lang.Double.valueOf(arr[i]);
			}
			return da;
		case BooleanArray:
			arr = StringUtils.tokenizeToStringArray(val, ARRAY_VALUE_SEPARATOR);
			Boolean[] ba = new Boolean[arr.length];
			for (int i = 0; i < arr.length; i++) {
				ba[i] = java.lang.Boolean.valueOf(arr[i]);
			}
			return ba;
		}

		return null;
	}
}

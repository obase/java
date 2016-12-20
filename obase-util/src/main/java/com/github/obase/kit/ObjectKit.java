package com.github.obase.kit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class ObjectKit {

	private ObjectKit() {
	}

	public static byte[] serialize(Object object) {
		if (object == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
		} catch (IOException ex) {
			throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
		}
		return baos.toByteArray();
	}

	public static Object deserialize(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
			return ois.readObject();
		} catch (IOException ex) {
			throw new IllegalArgumentException("Failed to deserialize object", ex);
		} catch (ClassNotFoundException ex) {
			throw new IllegalStateException("Failed to deserialize object type", ex);
		}
	}

	public static boolean equals(Object obj1, Object obj2) {
		return (obj1 == obj2) || (obj1 != null && obj1.equals(obj2));
	}

	public static boolean notEquals(Object obj1, Object obj2) {
		return (obj1 != obj2) && (obj1 == null || !obj1.equals(obj2));
	}

	public static <T> T ifnull(T obj, T def) {
		return obj == null ? def : obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T coalesce(T... objs) {
		for (T obj : objs) {
			if (obj != null) {
				return obj;
			}
		}
		return null;
	}

}

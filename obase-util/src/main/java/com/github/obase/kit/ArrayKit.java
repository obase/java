package com.github.obase.kit;

import java.util.Arrays;

public class ArrayKit {

	private ArrayKit() {
	}

	public static boolean isEmpty(boolean[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(byte[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(char[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(short[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(int[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(long[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(float[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(double[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(Object[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isNotEmpty(boolean[] arr) {
		return arr != null && arr.length > 0;
	}

	public static boolean isNotEmpty(byte[] arr) {
		return arr != null && arr.length > 0;
	}

	public static boolean isNotEmpty(char[] arr) {
		return arr != null && arr.length > 0;
	}

	public static boolean isNotEmpty(short[] arr) {
		return arr != null && arr.length > 0;
	}

	public static boolean isNotEmpty(int[] arr) {
		return arr != null && arr.length > 0;
	}

	public static boolean isNotEmpty(long[] arr) {
		return arr != null && arr.length > 0;
	}

	public static boolean isNotEmpty(float[] arr) {
		return arr != null && arr.length > 0;
	}

	public static boolean isNotEmpty(double[] arr) {
		return arr != null && arr.length > 0;
	}

	public static boolean isNotEmpty(Object[] arr) {
		return arr != null && arr.length > 0;
	}

	public static boolean[] append(boolean[] arr, boolean... news) {

		if (news == null || news.length == 0) {
			return arr;
		}

		boolean[] newArr = Arrays.copyOf(arr, arr.length + news.length);
		System.arraycopy(news, 0, newArr, arr.length, news.length);
		return newArr;
	}

	public static byte[] append(byte[] arr, byte... news) {

		if (news == null || news.length == 0) {
			return arr;
		}

		byte[] newArr = Arrays.copyOf(arr, arr.length + news.length);
		System.arraycopy(news, 0, newArr, arr.length, news.length);
		return newArr;
	}

	public static char[] append(char[] arr, char... news) {

		if (news == null || news.length == 0) {
			return arr;
		}

		char[] newArr = Arrays.copyOf(arr, arr.length + news.length);
		System.arraycopy(news, 0, newArr, arr.length, news.length);
		return newArr;
	}

	public static short[] append(short[] arr, short... news) {

		if (news == null || news.length == 0) {
			return arr;
		}

		short[] newArr = Arrays.copyOf(arr, arr.length + news.length);
		System.arraycopy(news, 0, newArr, arr.length, news.length);
		return newArr;
	}

	public static int[] append(int[] arr, int... news) {

		if (news == null || news.length == 0) {
			return arr;
		}

		int[] newArr = Arrays.copyOf(arr, arr.length + news.length);
		System.arraycopy(news, 0, newArr, arr.length, news.length);
		return newArr;
	}

	public static long[] append(long[] arr, long... news) {

		if (news == null || news.length == 0) {
			return arr;
		}

		long[] newArr = Arrays.copyOf(arr, arr.length + news.length);
		System.arraycopy(news, 0, newArr, arr.length, news.length);
		return newArr;
	}

	public static float[] append(float[] arr, float... news) {

		if (news == null || news.length == 0) {
			return arr;
		}

		float[] newArr = Arrays.copyOf(arr, arr.length + news.length);
		System.arraycopy(news, 0, newArr, arr.length, news.length);
		return newArr;
	}

	public static double[] append(double[] arr, double... news) {

		if (news == null || news.length == 0) {
			return arr;
		}

		double[] newArr = Arrays.copyOf(arr, arr.length + news.length);
		System.arraycopy(news, 0, newArr, arr.length, news.length);
		return newArr;
	}

	public static Object[] append(Object[] arr, Object... news) {

		if (news == null || news.length == 0) {
			return arr;
		}

		Object[] newArr = Arrays.copyOf(arr, arr.length + news.length);
		System.arraycopy(news, 0, newArr, arr.length, news.length);
		return newArr;
	}

}

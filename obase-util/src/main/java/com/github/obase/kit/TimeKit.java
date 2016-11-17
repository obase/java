package com.github.obase.kit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 该类是DateUtil的改进类, 专门用于处理Unix Time时间, 即格式为"yyyy-MM-dd HH:mm:ss".
 * 
 * @author HeZhaowu
 * 
 */
public final class TimeKit {

	static ThreadLocal<Calendar> CalendarLocal = new ThreadLocal<Calendar>() {

		@Override
		protected Calendar initialValue() {
			return Calendar.getInstance();
		}

	};

	/**
	 * 标记各个字段的起始位置;
	 * 
	 * @author HeZhaowu
	 * 
	 */
	static interface Index {
		/* yyyy-MM-dd HH:mm:ss */
		int[] YEAR = { 0, 4 };
		int[] MONTH = { 5, 7 };
		int[] DAY = { 8, 10 };
		int[] HOUR = { 11, 13 };
		int[] MINUTE = { 14, 16 };
		int[] SECOND = { 17, 19 };
	}

	/**
	 * 解析日期字串,返回各个时间段的数值. 依次是年, 月, 日, 时, 分, 钞.
	 * 
	 * @param val
	 *            , 日期字串, 固定格式为"yyyy-MM-dd HH:mm:ss"
	 * @return 解析后的各个时间段的数值数组.
	 */
	public static int[] extractUnixTime(String val) {
		int[] vals = new int[6];
		int len = val.length();
		if (len >= Index.YEAR[1]) {
			vals[0] = Integer.parseInt(val.substring(Index.YEAR[0], Index.YEAR[1]), 10);
		}
		if (len >= Index.MONTH[1]) {
			vals[1] = Integer.parseInt(val.substring(Index.MONTH[0], Index.MONTH[1]), 10) - 1;
		}
		if (len >= Index.DAY[1]) {
			vals[2] = Integer.parseInt(val.substring(Index.DAY[0], Index.DAY[1]), 10);
		}
		if (len >= Index.HOUR[1]) {
			vals[3] = Integer.parseInt(val.substring(Index.HOUR[0], Index.HOUR[1]), 10);
		}
		if (len >= Index.MINUTE[1]) {
			vals[4] = Integer.parseInt(val.substring(Index.MINUTE[0], Index.MINUTE[1]), 10);
		}
		if (len >= Index.SECOND[1]) {
			vals[5] = Integer.parseInt(val.substring(Index.SECOND[0], Index.SECOND[1]), 10);
		}
		return vals;
	}

	/**
	 * 解析日期对象,返回各个时间段的数值. 依次是年, 月, 日, 时, 分, 钞.
	 * 
	 * @param val
	 *            , 日期字串, 固定格式为"yyyy-MM-dd HH:mm:ss"
	 * @return 解析后的各个时间段的数值数组.
	 */
	public static int[] extractUnixTime(Date val) {
		int[] vals = new int[6];
		Calendar cal = CalendarLocal.get();
		cal.setTime(val);
		vals[0] = cal.get(Calendar.YEAR);
		vals[1] = cal.get(Calendar.MONTH);
		vals[2] = cal.get(Calendar.DAY_OF_MONTH);
		vals[3] = cal.get(Calendar.HOUR_OF_DAY);
		vals[4] = cal.get(Calendar.MINUTE);
		vals[5] = cal.get(Calendar.SECOND);
		return vals;
	}

	/**
	 * 解析日期字串, 返回日期对象.
	 * 
	 * @param val
	 * @return
	 */
	public static Date parseUnixTime(String val) {
		int[] vals = extractUnixTime(val);
		Calendar cal = CalendarLocal.get();
		cal.set(vals[0], vals[1], vals[2], vals[3], vals[4], vals[5]);
		return cal.getTime();
	}

	static ThreadLocal<SimpleDateFormat> DateFormatLocal = new ThreadLocal<SimpleDateFormat>() {

		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat();
		}

	};

	public static Date parse(String val, String pattern) throws ParseException {
		SimpleDateFormat sdf = DateFormatLocal.get();
		sdf.applyPattern(pattern);
		return sdf.parse(val);
	}

	public static String format(Date val, String pattern) throws ParseException {
		SimpleDateFormat sdf = DateFormatLocal.get();
		sdf.applyPattern(pattern);
		return sdf.format(val);
	}

	/**
	 * 格式日期对象.
	 * 
	 * @param date
	 * @return
	 */
	public static String formatUnixTime(Date date) {
		int[] vals = extractUnixTime(date);
		return String.format("%04d-%02d-%02d %02d:%02d:%02d", vals[0], vals[1] + 1, vals[2], vals[3], vals[4], vals[5]);
	}

	public static String formatUnixDate(Date date) {
		int[] vals = extractUnixTime(date);
		return String.format("%04d-%02d-%02d", vals[0], vals[1] + 1, vals[2]);
	}

	public static String yyyyMMddHHmm(String stime) {
		StringBuilder sb = new StringBuilder(stime);
		sb.delete(16, stime.length());
		sb.deleteCharAt(10);
		sb.deleteCharAt(12);
		sb.deleteCharAt(7);
		sb.deleteCharAt(4);
		return sb.toString();
	}

}

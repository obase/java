package com.huya.dbms.service.test;

import java.util.Arrays;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.obase.jedis.JedisClient;
import com.huya.dbms.service.BaseServiceTester;

public class WsidTester extends BaseServiceTester {

	public static final byte KEY_SEP = (byte) '\001';

	@Autowired
	JedisClient jedisClient;

	@Test
	public void test() {
		Set<byte[]> set = jedisClient.keys(keysPattern("dw_hezhaowu1"));
		for (byte[] bs : set) {
			System.out.println(new String(bs));
		}
	}

	public static byte[] keysPattern(String uid) {
		byte[] data = uid.getBytes();
		data = Arrays.copyOf(data, data.length + 2);
		data[data.length - 1] = '*';
		data[data.length - 2] = KEY_SEP;
		return data;
	}

	public static byte[] keysPattern(long uid) {
		byte[] data = new byte[8 + 2];
		for (int i = 7; i >= 0; i--) {
			data[i] = (byte) (uid & 0xFF);
			uid >>>= 8;
		}
		data[data.length - 1] = '*';
		data[data.length - 2] = KEY_SEP;
		return data;
	}

}

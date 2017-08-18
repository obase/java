package com.github.obase.webc.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.github.obase.kit.Spliter;
import com.github.obase.webc.yy.UserPrincipal;

public class Tester {

	public static void main(String[] args) throws ClassNotFoundException, IOException {

		int times = 100000;
		UserPrincipal up = new UserPrincipal();
		up.setYyuid("12345");
		up.setPassport("2fefae");
		up.setRealname("jason.he");
		up.setDeptname("SOFT");
		up.setNickname("afefe");
		up.setEmail("test@yy.com");

		long start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			text(up);
		}
		long end = System.currentTimeMillis();
		System.out.println("used: " + (end - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			binary(up);
		}
		end = System.currentTimeMillis();
		System.out.println("used: " + (end - start));

	}

	public static void binary(UserPrincipal obj) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		bos.close();

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		obj = (UserPrincipal) ois.readObject();
	}

	public static void text(UserPrincipal obj) {
		StringBuilder sb = new StringBuilder(1024);
		sb.append(obj.getYyuid()).append('\001');
		sb.append(obj.getPassport()).append('\001');
		sb.append(obj.getRealname()).append('\001');
		sb.append(obj.getNickname()).append('\001');
		sb.append(obj.getDeptname()).append('\001');
		sb.append(obj.getEmail()).append('\001');
		sb.append(obj.getPhone()).append('\001');
		sb.append(obj.getJobCode()).append('\001');
		sb.append(obj.getLevel());

		Spliter sp = new Spliter('\001', sb.toString());
		obj.setYyuid(sp.next());
		obj.setPassport(sp.next());
		obj.setRealname(sp.next());
		obj.setNickname(sp.next());
		obj.setDeptname(sp.next());
		obj.setEmail(sp.next());
		obj.setPhone(sp.next());
		obj.setJobCode(sp.next());
		obj.setLevel(Integer.parseInt(sp.next()));
	}

}

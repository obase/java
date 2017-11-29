package com.github.obase.mysql.test;

import org.springframework.asm.Type;

import com.github.obase.mysql.data.ClassMetaInfo;

public class TestMain {

	static ClassMetaInfo classMetaInfo = null;
	
	public static void main(String[] args) {
		System.out.println(Type.getReturnType("(II)V"));
	}

}

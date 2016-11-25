package com.demo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;

import com.github.obase.webc.Webc;
import com.github.obase.webc.annotation.ServletMethod;

@Controller
public class TestController {

	@ServletMethod(value = Webc.$, category = "TEST", summary = "测试根调用")
	public void index(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("index...");
	}

	@ServletMethod(method = HttpMethod.GET, category = "TEST", summary = "测试GET调用")
	public void get(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("get...");
	}

	@ServletMethod(method = HttpMethod.POST, category = "TEST", summary = "测试POST调用")
	public void post(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("post...");
	}

	@ServletMethod(method = HttpMethod.PUT, category = "TEST", summary = "测试PUT调用")
	public void put(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("put...");
	}

	@ServletMethod(method = HttpMethod.DELETE, category = "TEST", summary = "测试DELETE调用")
	public void delete(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("delete...");
	}

}

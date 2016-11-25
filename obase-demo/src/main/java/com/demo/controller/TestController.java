package com.demo.controller;

import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;

import com.github.obase.webc.Webc;
import com.github.obase.webc.annotation.ServletMethod;

@Controller
public class TestController {

	@ServletMethod(value = Webc.$, category = "TEST", summary = "测试根调用")
	public void index(HttpServletRequest request, HttpServletResponse response) {

	}

	@ServletMethod(method=,category = "TEST", summary = "测试根调用")
	public void get(HttpServletRequest request, HttpServletResponse response) {

	}

	public void post(HttpServletRequest request, HttpServletResponse response) {

	}

	public void put(HttpServletRequest request, HttpServletResponse response) {

	}

	public void delete(HttpServletRequest request, HttpServletResponse response) {

	}

}

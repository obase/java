package com.huya.dbms.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;

import com.github.obase.webc.Kits;
import com.github.obase.webc.Webc;
import com.github.obase.webc.annotation.ServletMethod;

@Controller
public class SummaryController {

	@ServletMethod(value = Webc.$)
	public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Kits.render(request, response, "/view/summary.jsp");
	}

}

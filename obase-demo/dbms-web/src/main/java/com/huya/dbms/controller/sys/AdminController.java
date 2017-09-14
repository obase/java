package com.huya.dbms.controller.sys;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;

import com.github.obase.Page;
import com.github.obase.webc.Kits;
import com.github.obase.webc.Webc;
import com.github.obase.webc.annotation.ServletMethod;
import com.huya.dbms.controller.BaseController;
import com.huya.dbms.entity.Admin;
import com.huya.dbms.model.UserPrincipal;
import com.huya.dbms.service.sys.AdminService;

@Controller
public class AdminController extends BaseController {

	@Autowired
	AdminService adminService;

	@ServletMethod(value = Webc.$, remark = "系统管理->管理员->主页")
	public void index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Kits.render(request, response, "/view/sys/admin/index.jsp");
	}

	@ServletMethod(method = HttpMethod.GET, remark = "系统管理->管理员->查询")
	public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Page<Admin> page = Kits.readPage(request, Admin.class);
		adminService.page(page, Kits.readQueryParam(request));
		Kits.writeJsonObject(response, page);
	}

	@ServletMethod(method = HttpMethod.POST, remark = "系统管理->管理员->添加", category = "ADMIN")
	public void add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		Admin admin = Kits.readJsonBody(request, Admin.class);
		adminService.add(user.getPassport(), admin);
		Kits.writeSuccessMessage(response, null);
	}

	@ServletMethod(method = HttpMethod.PUT, remark = "系统管理->管理员->修改", category = "ADMIN")
	public void update(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		Admin admin = Kits.readJsonBody(request, Admin.class);
		adminService.update(user.getPassport(), admin);
		Kits.writeSuccessMessage(response, null);
	}

	@ServletMethod(method = HttpMethod.DELETE, remark = "系统管理->管理员->删除", category = "ADMIN")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		adminService.delete(user.getPassport(), Kits.readQueryParam(request));
		Kits.writeSuccessMessage(response, null);
	}
}

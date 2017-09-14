package com.huya.dbms.controller.dsn;

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
import com.huya.dbms.entity.Project;
import com.huya.dbms.model.UserPrincipal;
import com.huya.dbms.service.dsn.ProjectService;

@Controller
public class ProjectController extends BaseController {

	@Autowired
	ProjectService projectService;

	@ServletMethod(value = Webc.$, remark = "数据源管理->项目->主页")
	public void index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Kits.render(request, response, "/view/dsn/project/index.jsp");
	}

	@ServletMethod(method = HttpMethod.GET, remark = "数据源管理->项目->查询")
	public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Page<Project> page = Kits.readPage(request, Project.class);
		projectService.page(page, Kits.readQueryParam(request));
		Kits.writeJsonObject(response, page);
	}

	@ServletMethod(method = HttpMethod.POST, remark = "数据源管理->项目->添加")
	public void add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		Project project = Kits.readJsonBody(request, Project.class);
		projectService.add(user.getPassport(), project);
		Kits.writeSuccessMessage(response, null);
	}

	@ServletMethod(method = HttpMethod.PUT, remark = "数据源管理->项目->修改")
	public void update(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		Project project = Kits.readJsonBody(request, Project.class);
		projectService.update(user.getPassport(), project);
		Kits.writeSuccessMessage(response, null);
	}

	@ServletMethod(method = HttpMethod.DELETE, remark = "数据源管理->项目->删除")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		projectService.delete(user.getPassport(), Kits.readQueryParam(request));
		Kits.writeSuccessMessage(response, null);
	}
}

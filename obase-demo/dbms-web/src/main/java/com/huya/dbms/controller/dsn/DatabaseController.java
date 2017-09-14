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
import com.huya.dbms.model.Database;
import com.huya.dbms.model.UserPrincipal;
import com.huya.dbms.service.dsn.DatabaseService;

@Controller
public class DatabaseController extends BaseController {

	@Autowired
	DatabaseService databaseService;

	@ServletMethod(value = Webc.$, remark = "数据源管理->数据库->主页")
	public void index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Kits.render(request, response, "/view/dsn/database/index.jsp");
	}

	@ServletMethod(method = HttpMethod.GET, remark = "数据源管理->数据库->查询")
	public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Page<Database> page = Kits.readPage(request, Database.class);
		databaseService.page(page, Kits.readQueryParam(request));
		Kits.writeJsonObject(response, page);
	}

	@ServletMethod(method = HttpMethod.POST, remark = "数据源管理->数据库->添加")
	public void add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		Database database = Kits.readJsonBody(request, Database.class);
		databaseService.add(user.getPassport(), database);
		Kits.writeSuccessMessage(response, null);
	}

	@ServletMethod(method = HttpMethod.PUT, remark = "数据源管理->数据库->修改")
	public void update(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		Database database = Kits.readJsonBody(request, Database.class);
		databaseService.update(user.getPassport(), database);
		Kits.writeSuccessMessage(response, null);
	}

	@ServletMethod(method = HttpMethod.DELETE, remark = "数据源管理->数据库->删除")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		databaseService.delete(user.getPassport(), Kits.readQueryParam(request));
		Kits.writeSuccessMessage(response, null);
	}

}

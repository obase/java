package com.huya.dbms.controller.dsn;

import java.util.Map;

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
import com.huya.dbms.model.InstanceExt;
import com.huya.dbms.model.UserPrincipal;
import com.huya.dbms.service.dsn.InstanceService;

@Controller
public class InstanceController extends BaseController {

	@Autowired
	InstanceService instanceService;

	@ServletMethod(value = Webc.$, remark = "数据源管理->实例->主页")
	public void index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Kits.render(request, response, "/view/dsn/instance/index.jsp");
	}

	@SuppressWarnings("rawtypes")
	@ServletMethod(method = HttpMethod.GET, remark = "数据源管理->实例->查询")
	public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Page<Map> page = Kits.readPage(request, Map.class);
		instanceService.page(page, Kits.readQueryParam(request));
		Kits.writeJsonObject(response, page);
	}

	@ServletMethod(method = HttpMethod.POST, remark = "数据源管理->实例->添加")
	public void add(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		InstanceExt instance = Kits.readJsonBody(request, InstanceExt.class);
		instanceService.add(user.getPassport(), instance);
		Kits.writeSuccessMessage(response, null);
	}

	@ServletMethod(method = HttpMethod.PUT, remark = "数据源管理->实例->修改")
	public void update(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		InstanceExt instance = Kits.readJsonBody(request, InstanceExt.class);
		instanceService.update(user.getPassport(), instance);
		Kits.writeSuccessMessage(response, null);
	}

	@ServletMethod(method = HttpMethod.DELETE, remark = "数据源管理->实例->删除")
	public void delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserPrincipal user = Kits.getPrincipal(request);
		instanceService.delete(user.getPassport(), Kits.readQueryParam(request));
		Kits.writeSuccessMessage(response, null);
	}
}

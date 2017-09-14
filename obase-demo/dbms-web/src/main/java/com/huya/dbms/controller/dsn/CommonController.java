package com.huya.dbms.controller.dsn;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;

import com.github.obase.webc.Kits;
import com.github.obase.webc.annotation.ServletMethod;
import com.huya.dbms.controller.BaseController;
import com.huya.dbms.service.dsn.CommonService;

@Controller
@SuppressWarnings("rawtypes")
public class CommonController extends BaseController {

	@Autowired
	CommonService commonService;

	@ServletMethod(method = HttpMethod.GET, remark = "数据源管理->通用->查询项目信息")
	public void projectInfoList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Map> ret = commonService.projectInfoList();
		Kits.writeSuccessMessage(response, ret);
	}

	@ServletMethod(method = HttpMethod.GET, remark = "数据源管理->通用->查询实例信息")
	public void instanceInfoList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Map> ret = commonService.instanceInfoList();
		Kits.writeSuccessMessage(response, ret);
	}
}

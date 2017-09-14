package com.huya.dbms.service.dsn;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.huya.dbms.service.BaseService;

@Service
@SuppressWarnings("rawtypes")
public class CommonService extends BaseService {

	public List<Map> projectInfoList() throws SQLException {
		return mysqlClient.query("Common.projectInfoList", Map.class, null);
	}

	public List<Map> instanceInfoList() throws SQLException {
		return mysqlClient.query("Common.instanceInfoList", Map.class, null);
	}
}

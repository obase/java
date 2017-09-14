<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<link href="/css/bui/dpl-min.css" rel="stylesheet" />
<link href="/css/bui/bui-min.css" rel="stylesheet" />
<link href="/css/app.css" rel="stylesheet" />
<style type="text/css">
.bui-grid-bbar {
	width: 100%;
}

.well {
	margin-bottom: 0px;
	padding: 8px;
}
</style>
</head>
<body>

	<div id="search" class="search-div">
		<form id="searchForm" class="well ">
			<label class="control-label">名称:</label>
			<input type="text" id="name" name="name" placeholder="实例名称/备注/描述关键词" class="control-text search-query" />
			<label class="control-label">地址:</label>
			<input type="text" id="ip" name="ip" placeholder="实例地址/主机/VIP关键词" class="control-text search-query" />
			<label class="control-label">端口:</label>
			<input type="text" id="portStart" name="portStart" placeholder="起始端口" class="control-text search-query" style="width: 50px" />
			<i>--</i>
			<input type="text" id="portEnd" name="portEnd" placeholder="结束端口" class="control-text search-query" style="width: 50px" />
			<br />
			<label class="control-label">主从:</label>
			<select id="role" name="role" class="input-normal control-select">
				<option value="">请选择</option>
				<option value="M">[M] master</option>
				<option value="S">[S] slave</option>
			</select>
			<label class="control-label">类型:</label>
			<select id="type" name="type" class="input-normal control-select">
				<option value="">请选择</option>
				<option value="D">[D] dbms</option>
				<option value="E">[E] etc</option>
				<option value="S">[S] sandbox</option>
				<option value="C">[C] cloudmysql</option>
			</select>
			<label class="control-label">DBA:</label>
			<input type="text" id="dba" name="dba" placeholder="DBA关键词" class="control-text search-query" />

			<button id="searchButton" type="button" class="button button-small button-info">
				<i class="icon-white icon-search"></i>
				搜索
			</button>
		</form>
	</div>
	<div id="mygrid"></div>

	<div id="popwin" class="hide">
		<%@ include file="./popup.jsp"%>
	</div>

	<script src="/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="/js/jquery.placeholder.min.js" type="text/javascript"></script>
	<script src="/js/bui/bui-min.js" type="text/javascript"></script>
	<script src="/js/app.js" type="text/javascript"></script>
	<script src="/view/dsn/instance/index.js" type="text/javascript"></script>
</body>
</html>
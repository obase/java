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
			<label class="control-label">项目:</label>
			<input type="text" id="projectname" name="projectname" placeholder="项目名称关键词" class="control-text search-query" />
			<label class="control-label">实例:</label>
			<input type="text" id="intsancename" name="intsancename" placeholder="实例名称关键词" class="control-text search-query" style="width: 200px" />
			<label class="control-label">数据库:</label>
			<input type="text" id="dbname" name="dbname" placeholder="数据库名称关键词" class="control-text search-query" style="width: 200px" />
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
	<script src="/view/dsn/database/index.js" type="text/javascript"></script>
</body>
</html>
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
			<input type="text" id="search" name="search" placeholder="搜索内容" class="control-text search-query" />
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
	<script src="/view/sys/admin/index.js" type="text/javascript"></script>
</body>
</html>
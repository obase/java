<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>数据源管理系统</title>

<link rel="icon" href="/img/icon.png" type="image/png">
<link href="/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
<link href="/css/ionicons.min.css" rel="stylesheet" type="text/css" />
<link href="/css/frameset.css" rel="stylesheet" type="text/css" />
<style type="text/css">
html, body { overflow: hidden; }
</style>
</head>
<body class="skin-blue fixed">

	<div class="header">
		<a href="#" class="logo">
			<!-- Add the class icon to your logo image or logo icon to add the margining -->
			数据源管理系统
		</a>
		<!-- Header Navbar: style can be found in header.less -->
		<div class="navbar navbar-static-top" role="navigation">
			<!-- Sidebar toggle button-->
			<a href="#" class="navbar-btn sidebar-toggle" data-toggle="offcanvas" role="button">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</a>
			<div class="navbar-right">
				<div class="pull-right" style="font-size: 14px; color: #fff; padding: 15px 15px;">
					欢迎您,${$_PRINCIPAL.passport}
					<a href="/console/logout" style="color: #ffa; font-size: 12px;">[退出]</a>
				</div>
			</div>
		</div>
	</div>
	<div class="wrapper row-offcanvas row-offcanvas-left">
		<div class="left-side sidebar-offcanvas">
			<div class="sidebar">
				<ul class="sidebar-menu" id="SideBarMenu">
					<li class="treeview">
						<a href="summary" id="indexPage">
							<i border="0" class="fa fa-dashboard"></i>
							数据源概览
						</a>
					</li>

					<li class="treeview">
						<a href="#">
							<i border="0" class="fa fa-database"></i>
							数据源管理
							<i class="fa pull-right fa-angle-left"></i>
						</a>
						<ul class="treeview-menu">

							<li>
								<a href="dsn/project" style="margin-left: 10px">
									<i class="fa fa-angle-double-right"></i>
									项目
								</a>
							</li>

							<li>
								<a href="dsn/instance" style="margin-left: 10px">
									<i class="fa fa-angle-double-right"></i>
									实例
								</a>
							</li>
							
							<li>
								<a href="dsn/database" style="margin-left: 10px">
									<i class="fa fa-angle-double-right"></i>
									数据库
								</a>
							</li>
						</ul>
					</li>

					<li class="treeview">
						<a href="#">
							<i border="0" class="fa fa-server"></i>
							系统管理
							<i class="fa pull-right fa-angle-left"></i>
						</a>
						<ul class="treeview-menu">

							<li>
								<a href="sys/admin" style="margin-left: 10px">
									<i class="fa fa-angle-double-right"></i>
									管理员
								</a>
							</li>

						</ul>
					</li>
				</ul>
			</div>
		</div>
		<div id="Content" class="right-side" style="display: block;">
			<div class="content-header" id="ContentHeader" style="color: #3c8dbc;">用户概览</div>
			<!-- Main content -->
			<div id="ContentBody" class="content">
				<iframe id="ContentFrame" src="" width="100%" scrolling="auto" frameborder="0" style="margin: 0px; padding: 0px;">
					<!-- Inititial is the first menu -->
				</iframe>
			</div>
		</div>
	</div>

	<!--[if lt IE 9]>
    <script src="/js/html5shiv.js"></script>
    <script src="/js/respond.min.js"></script>
    <![endif]-->
	<script src="/js/jquery-1.8.1.min.js" type="text/javascript"></script>
	<script src="/js/bui/seed-min.js" type="text/javascript"></script>
	<script src="/js/bootstrap.min.js" type="text/javascript"></script>
	<script src="/js/frameset.js" type="text/javascript"></script>
	<script src="/js/treeview.js" type="text/javascript"></script>
</body>
</html>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'dbmanager_view.jsp' starting page</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<plattag:resources restype="css" loadres="bootstrap-checkbox">
</plattag:resources>
</head>

<body >
<div style="text-align: center;padding-top: 100px;">
	<i style="font-size: 204px; color: rgb(15, 167, 79);"
		class="fa fa-search"></i>
	<div
		style="font-weight: bold; font-size: 24px; color: rgb(15, 167, 79); margin-top: 20px;">
		点击左侧具体流程事项进行数据查询</div>
</div>
</body>
</html>
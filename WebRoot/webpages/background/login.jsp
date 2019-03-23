<%@page import="com.stooges.core.util.PlatPropUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.stooges.core.util.PlatAppUtil"%>
<%@ page language="java" import="com.stooges.core.util.PlatLogUtil"%>
<%@ page language="java" import="org.apache.shiro.SecurityUtils"%>
<%@ page language="java" import="org.apache.commons.lang3.StringUtils"%>
<%@ page language="java" import="org.apache.shiro.subject.Subject"%>
<%@ page language="java" import="java.io.PrintWriter"%>
<%@ page language="java" import="com.stooges.platform.system.service.GlobalConfigService"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://"
            + request.getServerName() + ":" + request.getServerPort()
            + path + "/";
    if (PlatAppUtil.getRequest() != null) {
        Map<String, Object> sysUser = PlatAppUtil
                .getBackPlatLoginUser();
        if (sysUser != null) {
            response.sendRedirect(basePath
                    + "framework/ViewController.do?main");
        }
    }
    GlobalConfigService globalConfigService = (GlobalConfigService)PlatAppUtil.getBean("globalConfigService");
    Map<String,Object> globalConfig = globalConfigService.getFirstConfigMap();
    String backloginValidCode = (String) globalConfig.get("CONFIG_BACKVALIDCODE");
	request.setAttribute("backloginValidCode", backloginValidCode);
%>


<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>欢迎登录${sessionScope.globalProjectName}</title>
    <meta name="keywords" content="java 工作流开源，java工作流引擎 开源，快速开发平台,可视化流程，JBPM流程引擎，自定义表单，即时通讯，软件傻瓜开发平台，JAVA快速开发" />
    <meta name="description" content="臭皮匠软件快速开发平台，它本着灵活、快捷开发、高性能、高协作性、高稳定性、高可用性、人性化的操作体验为设计宗旨历经2年研发成功适用于搭建 OA、ERP、CRM、HR、HIS 等所有的企业信息管理系统。是全国性价比最高的零编程软件快速开发平台。" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="icon" href="webpages/website/official/images/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="webpages/website/official/images/favicon.ico" type="image/x-icon" />
	<plattag:resources restype="css"
		loadres="bootstrap-checkbox,jquery-ui,plat-ui,layer,nicevalid">
	</plattag:resources>
	<plattag:resources restype="js" loadres="jquery-ui,plat-util,layer,nicevalid,cryptojs">
	</plattag:resources>
	<!-- CSS -->
	<link rel="stylesheet" type="text/css" href="webpages/background/login/css/aos.css">
	<link rel="stylesheet" type="text/css" href="webpages/background/login/css/eui.css">
	<!-- JS -->
    <script type="text/javascript" src="webpages/background/login/js/aos.js"></script>
	<script type="text/javascript">
	  //获取浏览器地址
	  var href = window.top.location.href;
	  if(href.indexOf("login.jsp")==-1&&
			  href.indexOf("backLogoff.do")==-1&&href.indexOf("goBackLogin.do")==-1){
		  //alert("会话过期，请重新登录!");
		  window.top.location.href = __ctxPath+"/webpages/background/login.jsp";
	  }
	</script>

  <script>"undefined"==typeof CODE_LIVE&&(!function(e){var t={nonSecure:"8123",secure:"8124"},c={nonSecure:"http://",secure:"https://"},r={nonSecure:"127.0.0.1",secure:"gapdebug.local.genuitec.com"},n="https:"===window.location.protocol?"secure":"nonSecure";script=e.createElement("script"),script.type="text/javascript",script.async=!0,script.src=c[n]+r[n]+":"+t[n]+"/codelive-assets/bundle.js",e.getElementsByTagName("head")[0].appendChild(script)}(document),CODE_LIVE=!0);</script></head>
  
  <body data-genuitec-lp-enabled="false" data-genuitec-file-id="wc1-718" data-genuitec-path="/stooges2/WebRoot/webpages/background/login.jsp">
  <div class="eui-logLeft" data-genuitec-lp-enabled="false" data-genuitec-file-id="wc1-718" data-genuitec-path="/stooges2/WebRoot/webpages/background/login.jsp">
  <div class="eui-logo"></div>
	</div>
	<form action="security/LoginController/backLogin.do?service=${service}" id="loginForm" method="post">
	<div class="eui-logMain">
		<p class="eui-logName"><input type="text" name="USERNAME" value="test" maxlength="30" placeholder="请输入用户名"></p>
		<p class="eui-logPsd"><input type="password" name="PASSWORD" value="123456" maxlength="30" placeholder="请输入密码"/></p>
		<c:if test="${backloginValidCode=='1'}">
		<p class="eui-logYzm"><input type="text" name="jcaptcha"  maxlength="6" placeholder="请输入验证码" style="width: 154px;"> 
		<!-- <img src="webpages/background/login/images/yzm.png" class="rfloat"> -->
		<img title="点击图片换一张" onclick="changeRandPic();" class="rfloat" style="cursor: pointer;" id="randpic" src="<%=basePath%>jcaptcha/ImageCaptchaController/image.do">
		</p>
		</c:if>
		<a href="javascript:void(0);" onclick="submitLoginForm();">登录</a>
	</div>
	 </form>
  <div style="display: none;">
		<script src="https://s19.cnzz.com/z_stat.php?id=1264324396&web_id=1264324396" language="JavaScript"></script>
	</div>
  </body>
</html>
<script type="text/javascript">
function submitLoginForm(){
	var USERNAME = $("input[name='USERNAME']").val();
	if(USERNAME==null||USERNAME==""){
		layer.alert("请输入用户名",{icon: 2,resize:false});
		return; 
	}
	var PASSWORD = $("input[name='PASSWORD']").val();
	if(PASSWORD==null||PASSWORD==""){
		layer.alert("请输入密码",{icon: 2,resize:false});
		return; 
	}
	if("${backloginValidCode}"=="true"){
		var jcaptcha = $("input[name='jcaptcha']").val();
		if(jcaptcha==null||jcaptcha==""){
			layer.alert("请输入验证码",{icon: 2,resize:false});
			return; 
		}
	}
	var url = $("#loginForm").attr("action");
	var formData = PlatUtil.getFormEleData("loginForm");
	formData.PASSWORD = PlatUtil.getSha256Encode(PASSWORD);
	PlatUtil.ajaxProgress({
		url:url,
		params : formData,
		callback : function(resultJson) {
			if (resultJson.success) {
				if(resultJson.redirecturl){
					window.top.location.href = resultJson.redirecturl;
				}else{
					window.top.location.href = __ctxPath+"/framework/ViewController.do?main";
				}
			} else {
				changeRandPic();
				layer.alert(resultJson.msg,{icon: 2,resize:false});
			}
		}
	});
}


$(function () {
	// 动画
	AOS.init({
	  offset: 0,
	  duration: 1000,
	});

});

$("body").keydown(function(event) {
    if (event.keyCode == "13") {//keyCode=13是回车键
    	submitLoginForm();
    }
});  

function changeRandPic(){
	$("#randpic").attr({
          "src": "<%=basePath%>jcaptcha/ImageCaptchaController/image.do?"+Math.random()
     });
}

</script>

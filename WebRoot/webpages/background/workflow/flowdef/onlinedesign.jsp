<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML>
<html>
<head>
<base href="<%=basePath%>">

<title>流程在线设计</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<plattag:resources restype="css" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,layer,nicevalid"></plattag:resources>
 <plattag:resources restype="js" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,layer,nicevalid"></plattag:resources>
<link type="text/css" href="webpages/background/workflow/flowdef/css/design.css" rel="stylesheet">
<script type="text/javascript" src="plug-in/gojs-1.0/go.js"></script>
<script type="text/javascript" src="plug-in/gojs-1.0/LinkLabelDraggingTool.js"></script>

</head>

<body >
		<div class="plat-directlayout" style="height:100%" platundragable="true" compcode="direct_layout" 
		layoutsize="&quot;north__size&quot;:76,&quot;west__size&quot;:100,&quot;east__size&quot;:300">
		   <!-- 开始编写中间绘图区域 -->
		   <div class="ui-layout-center" platundragable="true" compcode="direct_layout" style="overflow-y:auto;">
		   		  <div class="panel-Title">
					<h5 id="daytrend_datatablepaneltitle">绘图区</h5>
				  </div>
				  <div style="  position: absolute;top: 32px;bottom: 0px;left: 0px;right: 0px;">
		   		 	 <div id="myDiagram" style="height:100%;"></div>
		   		  </div>
        		  <div style="position: absolute; top:40px; left:10px;"><img src="webpages/background/workflow/flowdef/images/flownode_colors.png"></div>
		   </div>
		   <!-- 结束编写中间绘图区域 -->
		   
		   <!-- 开始编写头部工具栏 -->
		   <div class="ui-layout-north" platundragable="true" compcode="direct_layout" style="background: #fff;">
		   		<div class="panel-Title">
					<h5 id="daytrend_datatablepaneltitle">工具栏</h5>
				</div>
				<div style="padding: 6px;">
					<button class="btn btn-outline btn-primary btn-sm" type="button" onclick="showDesignCode('1');" platreskey="">
						<i class="fa fa-search"></i>
						 预览设计代码
					</button>
					<button class="btn btn-outline btn-info btn-sm" type="button" onclick="showDesignCode('2');" platreskey="">
						<i class="fa fa-clipboard"></i>
						 导入设计代码
					</button>
					<button class="btn btn-outline btn-warning btn-sm" type="button" onclick="nodeAlign('v');" platreskey="">
						<i class="fa fa-text-height"></i>
						 垂直对齐
					</button>
					<button class="btn btn-outline btn-success btn-sm" type="button" onclick="nodeAlign('h');" platreskey="">
						<i class="fa fa-text-width"></i>
						 水平对齐
					</button>
		   		</div>
		   </div>
		   <!-- 结束编写头部工具栏 -->
		   
		   <!-- 开始编写左侧图元区工具栏 -->
		   <div class="ui-layout-west" platundragable="true" compcode="direct_layout">
		   		<div class="panel-Title">
					<h5 id="daytrend_datatablepaneltitle">图元区</h5>
				</div>
		   		<div id="myPalette" style="height: 720px;"></div>
		   </div>
		   <!-- 结束编写左侧图元区工具栏 -->
		   
		   <!-- 开始编写基本信息 -->
		   <div class="ui-layout-east" platundragable="true" compcode="direct_layout" style="background: #fff;">
		   		<div class="panel-Title">
					<h5 id="daytrend_datatablepaneltitle">基本信息</h5>
				</div>
		   		<form name="FLowDesignForm" class="form-horizontal" id="FLowDesignForm" action="workflow/FlowDefController.do?saveOrUpdate">
				     <input type="hidden" name="FLOWDEF_JSON" id="FLOWDEF_JSON" value="${flowDef.FLOWDEF_JSON}">
				     <input type="hidden" name="FLOWDEF_XML" id="FLOWDEF_XML" >
				     <input type="hidden" name="FLOWDEF_ID" value="${flowDef.FLOWDEF_ID}">
				     <div class="form-group">
				          <plattag:input name="FLOWDEF_NAME" auth_type="write" allowblank="false" 
				          datarule="required;" value="${flowDef.FLOWDEF_NAME}"
				          placeholder="请输入流程名称" comp_col_num="8" label_col_num="4" label_value="名称"></plattag:input>
			         </div>
			         <div class="hr-line-dashed"></div>
			       	 <div class="form-group">
			       	       <plattag:input name="FLOWDEF_CODE" allowblank="false" 
			       	       auth_type="${flowDef.FLOWDEF_ID!=null?'readonly':'write'}" value="${flowDef.FLOWDEF_CODE}"
				          datarule="${flowDef.FLOWDEF_ID==null?'required;onlyLetterNumberUnderLine;remote[common/baseController.do?checkUnique&VALID_TABLENAME=JBPM6_FLOWDEF&VALID_FIELDLABLE=流程编码&VALID_FIELDNAME=FLOWDEF_CODE]':''}"
				          placeholder="请输入流程编码" comp_col_num="8" label_col_num="4" label_value="编码"></plattag:input>
			         </div>
			         <div class="hr-line-dashed"></div>
			         <div class="form-group">
			       	       <plattag:select istree="false" allowblank="false" comp_col_num="6" label_col_num="4"
			       	        label_value="类别" value="${flowDef.FLOWTYPE_ID}" placeholder="请选择流程类别"
			       	        dyna_interface="flowTypeService.findForSelect" dyna_param=""
					        auth_type="write" name="FLOWTYPE_ID" ></plattag:select>
			         </div>
			         <div class="hr-line-dashed"></div>
			         <div class="form-group" style="margin-top: 30px;text-align: center;">
						 <!-- <p class="fix_height clearfix">
						    <input type="button" onclick="submitFlowDef();" value="部署流程" class="checkout-btn">
						 </p> -->
						 <button class="btn btn-primary btn-sm"  style="width: 90%;" type="button" onclick="submitFlowDef();" platreskey="">
							<i class="fa fa-sitemap"></i>
							 部署流程
						</button>
					 </div>
					</form>
		   </div>
		   <!-- 结束编写基本信息 -->
		</div>
</body>
</html>

<script type="text/javascript" src="webpages/background/workflow/flowdef/js/design.js"></script>
<script type="text/javascript">
$(function() {
	initFlowDesign(false, true);
	//初始化UI控件
	PlatUtil.initUIComp();
});
</script>

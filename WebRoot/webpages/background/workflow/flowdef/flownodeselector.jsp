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
<plattag:resources restype="css" loadres="layer"></plattag:resources>
<plattag:resources restype="js" loadres="layer,plat-util"></plattag:resources>
<link type="text/css" href="webpages/background/workflow/flowdef/css/design.css" rel="stylesheet">
<script type="text/javascript" src="plug-in/gojs-1.0/go.js"></script>
<script type="text/javascript" src="plug-in/gojs-1.0/LinkLabelDraggingTool.js"></script>

</head>

<body>
   <input type="hidden" name="FLOWDEF_JSON" id="FLOWDEF_JSON" value="${flowDef.FLOWDEF_JSON}">
   <input type="hidden" name="FLOWDEF_XML" id="FLOWDEF_XML" >
   <input type="hidden" name="FLOWDEF_ID" value="${flowDef.FLOWDEF_ID}">
   <div class="plat-directlayout" style="height:100%" platundragable="true" compcode="direct_layout">	
      <div class="ui-layout-center" platundragable="true" compcode="direct_layout" style="overflow-y:auto;">
         <div id="myDiagram" style="height: 700px;"></div>
      </div>
      <div class="ui-layout-south" platundragable="true" compcode="direct_layout">
		   <div class="row" style="height:40px; margin-top: -6px;padding-top: 4px; background: #e5e5e5;" platundragable="true" compcode="buttontoolbar">
		     <div class="col-sm-12 text-right">
				<button type="button" onclick="submitBusForm();" platreskey="" class="btn btn-outline btn-primary btn-sm">
					<i class="fa fa-check"></i> 确定
				</button>
				<button type="button" onclick="PlatUtil.closeWindow();" platreskey="" class="btn btn-outline btn-danger btn-sm">
					<i class="fa fa-times"></i> 关闭
				</button>
		     </div>
			</div>
	  </div>
   </div>

</body>
</html>

<script type="text/javascript" src="webpages/background/workflow/flowdef/js/design.js"></script>
<script type="text/javascript">
$(function() {
	//初始化UI控件
	PlatUtil.initUIComp();
	initFlowDesign(true,false,null,function(data){
		var nodeType = data.nodeType;
		if(nodeType=="task"||nodeType=="start"||nodeType=="end"||nodeType=="decision"){
			var nodeKey = data.key;
			var nodeName = data.text;
			var color = data.color;
			var nodedata = myDiagram.model.findNodeDataForKey(nodeKey);
			if(color!="red"){
				nodedata.color = "red";
			}else{
				nodedata.color = "#8C8C8C";
			}
			myDiagram.model.removeNodeData(nodedata);
			myDiagram.model.addNodeData(nodedata);
		}
		
	});
	//获取初始化配置
	var selectorConfig = PlatUtil.getData(PlatUtil.WIN_SELECTOR_CONFIG);
	var needCheckIds = selectorConfig.needCheckIds;
	if(needCheckIds){
		var checkKeys = needCheckIds.split(",");
		$.each(checkKeys,function(index,value){
			var nodedata = myDiagram.model.findNodeDataForKey(value);
			nodedata.color = "red";
			myDiagram.model.removeNodeData(nodedata);
			myDiagram.model.addNodeData(nodedata);
		});
	}
});

function submitBusForm(){
	var selectorConfig = PlatUtil.getData(PlatUtil.WIN_SELECTOR_CONFIG);
	var maxselect = selectorConfig.maxselect;
	var minselect = selectorConfig.minselect;
	var checkRecords = [];
	var checkIds = "";
	var checkNames = "";
	$.each(myDiagram.model.nodeDataArray,function(index,obj){
		var id = obj.key;
		var name = obj.text;
		var color = obj.color;
		if(color=="red"){
			checkIds+=id+",";
			checkNames+=name+",";
			checkRecords.push(id);
		}
	});
	if (checkRecords.length >= 1) {
		checkIds = checkIds.substring(0, checkIds.length - 1);
		checkNames = checkNames.substring(0, checkNames.length - 1);
	}
	if(maxselect!=0){
		if(checkRecords.length>maxselect){
			parent.layer.msg("最多只能选择"+maxselect+"条记录!", {icon: 2});
			return;
		}
	}
	if(checkRecords.length<minselect){
		parent.layer.msg("至少需要选择"+minselect+"条记录!", {icon: 2});
		return;
	}
	PlatUtil.removeData(PlatUtil.WIN_SELECTOR_CONFIG);
	PlatUtil.setData(PlatUtil.WIN_SELECTOR_RECORDS,{
		selectSuccess:true,
		checkIds:checkIds,
		checkNames:checkNames
	});
	PlatUtil.closeWindow();
}


</script>

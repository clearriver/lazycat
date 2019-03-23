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
	<plattag:resources restype="css" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,ztree,bootswitch,plat-ui,tipsy,autocomplete,webuploader">
	</plattag:resources>
  </head>
  
  <body>
	    <div class="ui-layout-west" >
	        <plattag:treepanel panel_title="流程事项树" id="flowDefTree" opertip="false"  >
	        </plattag:treepanel>
		</div>
		<div class="ui-layout-center" >
  		    <iframe height="100%" id="flowDataIframe" 
  		    name="flowDataIframe" frameborder="0" width="100%" src="workflow/ExecutionController.do?goDataQueryTip">
  		    </iframe>
  		</div>
   
  </body>
</html>
<plattag:resources restype="js" loadres="jquery-ui,jqgrid,jedate,select2,ztree,jquery-layout,bootswitch,plat-util,tipsy,autocomplete,pinyin,webuploader">
</plattag:resources>
<script type="text/javascript">

/**
 * 点击树形节点触发的事件
 */
function onModuleTreeClick(event, treeId, treeNode, clickFlag) {
	if(event.target.tagName=="SPAN"){
		var FLOWDEF_ID = treeNode.FLOWDEF_ID;
		if(FLOWDEF_ID){
			var href = "workflow/FlowDefController.do?goFlowDataQuery&FLOWDEF_ID="+FLOWDEF_ID;
			document.getElementById("flowDataIframe").contentWindow.location.href = href;
		}
	}
}

$(function(){
	  $("body").layout({resizable:true, west__size:280,west__closable:false});
	  PlatUtil.initUIComp();
	  PlatUtil.initZtree({
		  treeId:"flowDefTree",
		  //自动补全匹配方式1:客户端匹配(客户端匹配则一次性加载所有数据) 2:服务端匹配(如果是服务端匹配则需要传入)
		  //如果需要自定义服务端url,请传入autoServerUrl
		  autoCompleteType:"1",
		  autoServerUrl:"workflow/FlowTypeController.do?autoTypeAndDef",
		  edit : {
				enable : false
		  },
		  callback: {
			  onClick:onModuleTreeClick
		  },
		  async : {
			url:"workflow/FlowTypeController.do?typeAndDefs",
			otherParam : {
				//最先读取的根节点的值
				"loadRootId" : "0",
				//需要回填的值
				"needCheckIds":"",
				//是否显示树形标题
				"isShowTreeTitle" : "true",
				//树形标题
				"treeTitle":"流程事项树"
			}
		  }
	  });
	  
});
</script>

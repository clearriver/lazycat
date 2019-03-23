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
    
    <title>${design.DESIGN_NAME}-在线设计开发</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<plattag:resources restype="css" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,ztree,bootswitch,plat-ui,tipsy,autocomplete,layer">
	</plattag:resources>
  </head>
  
  <body>
      <div class="plat-directlayout " style="height: 100%;" layoutsize="&quot;west__size&quot;:230" >
	    <div class="ui-layout-west" >
	        <input type="hidden" id="DESIGN_ID" name="DESIGN_ID" value="${design.DESIGN_ID}">
	        <plattag:treepanel panel_title="组件树" id="formControlTree"
             right_menu="[新增,fa-plus,showFormControlWin][修改,fa-pencil,editModuleWin][删除,fa-trash-o,delFormControl]"	        
	        >
	        </plattag:treepanel>
		</div>
  		<div class="ui-layout-center" >
  		    <iframe height="100%" id="designFormIframe" 
  		    name="designFormIframe" frameborder="0" width="100%" src="appmodel/DesignController.do?goGenUiView&DESIGN_ID=${design.DESIGN_ID}">
  		    </iframe>
  		</div>
 	   </div>
  </body>
</html>
<plattag:resources restype="js" loadres="jquery-ui,jqgrid,jedate,select2,ztree,jquery-layout,bootswitch,plat-util,tipsy,autocomplete,pinyin,layer">
</plattag:resources>
<script type="text/javascript">

function delFormControl(){
	PlatUtil.deleteZtreeNode({
		treeId:"formControlTree",
		url:"appmodel/FormcontrolController.do?delRecord"
	});
}


function editModuleWin(){
	PlatUtil.hideZtreeRightMenu();
	var selectFormControl = $.fn.zTree.getZTreeObj("formControlTree").getSelectedNodes()[0];
	var FORMCONTROL_ID = selectFormControl.id;
	if(FORMCONTROL_ID=="0"){
		parent.layer.alert("根节点不能进行编辑!",{icon: 2,resize:false});
	}else{
		showFormControlWin(FORMCONTROL_ID);
	}
}

function showFormControlWin(FORMCONTROL_ID) {
	var DESIGN_ID = $("#DESIGN_ID").val();
	var url = "appmodel/FormcontrolController.do?goForm&DESIGN_ID="+DESIGN_ID;
	var title = "新增组件配置";
	if(FORMCONTROL_ID){
		title = "编辑组件配置";
		url+=("&FORMCONTROL_ID="+FORMCONTROL_ID);
	}else{
		PlatUtil.hideZtreeRightMenu();
		var selectTreeNode = $.fn.zTree.getZTreeObj("formControlTree").getSelectedNodes()[0];
		if(selectTreeNode.id=="0"&&selectTreeNode.children.length>0){
			parent.layer.alert("最多只能创建一个根控件!",{icon: 2,resize:false});
			return;
		}
		var PARENT_ID = selectTreeNode.id;
		url+=("&PARENT_ID="+PARENT_ID);
	}
	PlatUtil.openWindow({
		title:title,
		area: ["100%","100%"],
		content: url,
		end:function(){
		  window.frames[0].location.href = "appmodel/DesignController.do?goGenUiView&DESIGN_ID=${design.DESIGN_ID}";
		  $.fn.zTree.getZTreeObj("formControlTree").reAsyncChildNodes(null, "refresh");
		}
	});
}

/**
 * 点击树形节点触发的事件
 */
function onModuleTreeClick(event, treeId, treeNode, clickFlag) {
	if(event.target.tagName=="SPAN"){
		if(treeNode.id=="0"){
			$("input[name='Q_T.DESIGN_MODULEID_EQ']").val("");
			$("#design_paneltitle").text("可视化设计列表");
		}else{
			$("input[name='Q_T.DESIGN_MODULEID_EQ']").val(treeNode.id);
			$("#design_paneltitle").text(treeNode.name+"-->可视化设计列表");
			$("#design_datatable").jqGrid("sortableRows");
		}
		PlatUtil.tableDoSearch("design_datatable","design_search");
	}
}

$(function(){
	PlatUtil.initUIComp();
    PlatUtil.initZtree({
	  treeId:"formControlTree",
	  //自动补全匹配方式1:客户端匹配(客户端匹配则一次性加载所有数据) 2:服务端匹配(如果是服务端匹配则需要传入)
	  //如果需要自定义服务端url,请传入autoServerUrl
	  autoCompleteType:"1",
	  edit : {
			enable : false
	  },
	  callback: {
		  onRightClick: PlatUtil.onZtreeRightClick,
		  onClick:onModuleTreeClick
	  },
	  async : {
		url:"common/baseController.do?tree",
		otherParam : {
			//树形表名称
			"tableName" : "PLAT_APPMODEL_FORMCONTROL",
			//键和值的列名称
			"idAndNameCol" : "FORMCONTROL_ID,FORMCONTROL_NAME",
			//查询其它部门列名称
			"targetCols" : "FORMCONTROL_COMPCODE,FORMCONTROL_PARENTID",
			"Q_FORMCONTROL_DESIGN_ID_EQ":"${design.DESIGN_ID}",
			//最先读取的根节点的值
			"loadRootId" : "0",
			//需要回填的值
			"needCheckIds":"",
			//是否显示树形标题
			"isShowTreeTitle" : "true",
			//树形标题
			"treeTitle":"组件配置树"
		}
	  }
  	});
});
</script>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<input type="hidden" name="FLOWDEF_JSON" id="FLOWDEF_JSON" value="${FLOWDEF_JSON}">
<div id="myDiagram" style="height: 1000px;"></div>
<div style="position: absolute; top:30px; left:10px;"><img src="webpages/background/workflow/flowdef/images/flownode_colors.png"></div>
<script type="text/javascript">

$(function() {
	var jbpmFlowInfo = PlatUtil.getJbpmFlowInfo();
	//初始化UI控件
	initFlowDesign(true,false,function(data){
		if(jbpmFlowInfo){
			var nodeKey = data.key;
			var nodeText = data.text;
			var nodeType = data.nodeType;
			var nodeColor = data.color;
			var jbpmExeId = jbpmFlowInfo.jbpmExeId;
			if(nodeType=="start"||nodeType=="task"){
				if(nodeColor!="red"){
					PlatUtil.ajaxProgress({
						url:"workflow/JbpmTaskController.do?getLatestTaskId",
						params : {
							jbpmExeId:jbpmExeId,
							nodeKey:nodeKey
						},
						callback : function(resultJson) {
							if (resultJson.taskId) {
								PlatUtil.showExecutionWindow(jbpmExeId,nodeText+"【表单明细】","true",resultJson.taskId);
							}else{
								parent.layer.alert("未查询到该环节表单数据!",{icon: 2,resize:false});
							}
						}
					});
				}
			}else if(nodeType=="subprocess"){
				PlatUtil.ajaxProgress({
					url:"workflow/JbpmTaskController.do?getSubFlowTaskInfo",
					params : {
						jbpmExeId:jbpmExeId,
						nodeKey:nodeKey
					},
					callback : function(resultJson) {
						if (resultJson.taskId) {
							var subFlowExeId = resultJson.subFlowExeId;
							PlatUtil.showExecutionWindow(subFlowExeId,nodeText+"【表单明细】","true",resultJson.taskId);
						}else{
							parent.layer.alert("未查询到该环节表单数据!",{icon: 2,resize:false});
						}
					}
				});
			}
		}
		
	});
});


</script>

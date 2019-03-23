<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<input type="hidden" name="FLOWDEF_JSON" id="FLOWDEF_JSON" value="${FLOWDEF_JSON}">
<div id="myDiagram" style="height: 700px;"></div>
<script type="text/javascript">

$(function() {
	//初始化UI控件
	initFlowDesign(true,false,function(data){
	    var nodeType = data.nodeType;
	    var url = "workflow/FlowNodeController.do?goFlowNodeConfig&FLOWDEF_ID=${FLOWDEF_ID}&FLOWDEF_VERSION=${FLOWDEF_VERSION}&NODE_KEY="+data.key+"&NODE_TYPE="+data.nodeType;
		if(nodeType=="subprocess"){
			url+="&IS_SUBPROCESS=true";
		}else{
			url+="&IS_SUBPROCESS=false";
		}
	    PlatUtil.openWindow({
			title:"配置环节["+data.text+"],节点KEY["+data.key+"]",
			area: ["80%","90%"],
			content:url, 
			end:function(){
				
			}
		});
	});
});


</script>

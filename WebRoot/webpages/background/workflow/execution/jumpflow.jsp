<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
    String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
%>

<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">
<title>My JSP 'uipreview.jsp' starting page</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<plattag:resources restype="css"
	loadres="bootstrap-checkbox,jquery-ui,jedate,select2,nicevalid"></plattag:resources>
<plattag:resources restype="js"
	loadres="bootstrap-checkbox,jquery-ui,jedate,select2,nicevalid"></plattag:resources>
</head>

<body>
	<script type="text/javascript">
		$(function() {
			PlatUtil.initUIComp();
		});
	</script>
	<div class="plat-directlayout" style="height:100%"
		platundragable="true" compcode="direct_layout">
		<div class="ui-layout-center" platundragable="true"
			compcode="direct_layout" style="overflow-y:auto;">
			<form method="post" class="form-horizontal" compcode="formcontainer"
				action="" id="FlowSubmitForm" >
				<input type="hidden" name="EXECUTION_ID" value="${EXECUTION_ID}">
				<div class="form-group plat-form-title" compcode="formtitle" id="">
					<span class="plat-current">可跳转环节列表</span>
				</div>
				<c:forEach items="${taskList}" var="task" varStatus="varStatus" >
				<div class="form-group" compcode="formgroup">
				    <input type="hidden" name="TASK_ID_${varStatus.index}" value="${task.TASK_ID}" >
					<plattag:input name="TASK_NODENAME_${varStatus.index}" allowblank="false" 
						auth_type="readonly" value="${task.TASK_NODENAME}"
						datarule="required;" label_value="当前环节" placeholder="请输入环节名称"
						comp_col_num="2" label_col_num="1">
					</plattag:input>
					<plattag:input name="TASK_HANDLENAMES_${varStatus.index}" allowblank="false" 
						auth_type="readonly" value="${task.TASK_HANDLENAMES}"
						datarule="required;" label_value="当前办理人" placeholder="请选择办理人"
						comp_col_num="2" label_col_num="1">
					</plattag:input> 
					 <plattag:winselector name="JUMP_NODEKEY_${varStatus.index}" allowblank="true" 
					 auth_type="write" 
					 selectorurl="workflow/FlowNodeController.do?goSelectNodes&FLOWDEF_ID=${FLOWDEF_ID}&FLOWDEF_VERSION=${FLOWDEF_VERSION}"
					  title="流程节点选择" width="100%" height="100%" selectedlabels="" 
					   maxselect="1" minselect="1" label_value="跳转环节" 
					  placeholder="请选择流程环节" comp_col_num="2" label_col_num="1">
					</plattag:winselector>
					<plattag:winselector name="JUMP_RECEIVERIDS_${varStatus.index}" allowblank="true" 
					 auth_type="write" 
					 selectorurl="system/RoleController.do?goUserGrant"
					  title="接收人选择" width="100%" height="100%" selectedlabels="" 
					  maxselect="0" minselect="1" label_value="接收人" 
					  placeholder="请选择接收人" comp_col_num="2" label_col_num="1">
					</plattag:winselector>
				</div>
				<div class="hr-line-dashed"></div>
				</c:forEach>
			</form>
		</div>
		<div class="ui-layout-south" platundragable="true"
			compcode="direct_layout">
			<div class="row"
				style="height:40px; margin-top: -6px;padding-top: 4px; background: #e5e5e5;"
				platundragable="true" compcode="buttontoolbar">
				<div class="col-sm-12 text-right">
					<button type="button" onclick="submitBusForm();" id=""
						class="btn btn-outline btn-primary btn-sm">
						<i class="fa fa-check"></i> 提交
					</button>
					<button type="button" onclick="closeWindow();" id=""
						class="btn btn-outline btn-danger btn-sm">
						<i class="fa fa-times"></i> 关闭
					</button>
				</div>
			</div>

		</div>
	</div>
</body>
</html>

<script type="text/javascript">
	function submitBusForm() {
		if (PlatUtil.isFormValid("#FlowSubmitForm")) {
			var url = $("#FlowSubmitForm").attr("action");
			var jumpAssignList = [];
			$("[name^='TASK_ID']").each(function(index){
	            //获取元素的类型
			    var tagName = $(this).get(0).tagName;
	            var nextNodeKeyName = $(this).attr("name");
	            var jumpAssign = {};
	            var TASK_ID = $(this).val();
	            var JUMP_NODEKEY = $("input[name='JUMP_NODEKEY_"+index+"']").val();
	            var JUMP_NODENAME = $("input[name='JUMP_NODEKEY_"+index+"_LABELS']").val();
	            var JUMP_RECEIVERIDS = $("input[name='JUMP_RECEIVERIDS_"+index+"']").val();
	            if(JUMP_NODEKEY&&JUMP_RECEIVERIDS){
	            	jumpAssign.TASK_ID = TASK_ID;
		            jumpAssign.JUMP_NODEKEY = JUMP_NODEKEY;
		            jumpAssign.JUMP_NODENAME = JUMP_NODENAME;
		            jumpAssign.JUMP_RECEIVERIDS = JUMP_RECEIVERIDS;
		            jumpAssignList.push(jumpAssign);
	            }
			});
			if(jumpAssignList.length>0){
				var jumpAssignJson = JSON.stringify(jumpAssignList);
				PlatUtil.ajaxProgress({
					url:"workflow/JbpmTaskController.do?jump",
					params :{
						jumpAssignJson:jumpAssignJson,
						EXECUTION_ID:"${EXECUTION_ID}"
					},
					callback : function(resultJson) {
						if(resultJson.success){
							parent.layer.msg(PlatUtil.SUCCESS_MSG, {icon: 1});
							PlatUtil.setData("submitSuccess",true);
							PlatUtil.closeWindow();
						}else{
							parent.layer.alert(resultJson.OPER_MSG,{icon: 2,resize:false});
						}
					}
				});
			}else{
				parent.layer.alert("请选择跳转环节和接受人!",{icon: 2,resize:false});
			}
		}
	}
	function closeWindow() {
		PlatUtil.closeWindow();
	}
</script>


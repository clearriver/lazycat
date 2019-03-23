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
				action="workflow/ExecutionController.do?exeFlow" id="FlowSubmitForm"
				style="">
				<input type="hidden" name="flowToken" value="${flowToken}">
				<div class="form-group plat-form-title" compcode="formtitle" id="">
					<span class="plat-current">退回环节信息</span>
				</div>
				<div class="form-group" compcode="formgroup">
				    <plattag:select istree="false" allowblank="false" 
					label_col_num="2" label_value="环节名称" placeholder="请选择环节" 
					dyna_interface="jbpmTaskService.getSelectBackStep" dyna_param="${flowToken}"
					comp_col_num="4" auth_type="write" name="nextNodeKey_0">
					</plattag:select>
					<input type="hidden" name="nextNodeAssignerIds_0" >
					<plattag:input name="nextNodeAssignerNames_0" allowblank="false" 
						auth_type="readonly" 
						datarule="required;" label_value="办理人" placeholder="请输入办理人"
						comp_col_num="4" label_col_num="2">
					</plattag:input> 
					
				</div>
				<div class="hr-line-dashed"></div>
				<div class="form-group plat-form-title" compcode="formtitle" id="">
					<span class="plat-current">退回意见</span>
				</div>
				<div class="form-group" compcode="formgroup">
				    <plattag:textarea name="HANDLE_OPINION" auth_type="write" allowblank="false"
				     comp_col_num="10" label_col_num="2" label_value="退回意见"  maxlength="1000"></plattag:textarea>
				</div>
				<div class="hr-line-dashed"></div>
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
	$(function(){
		$("select[name^='nextNodeKey']").change(function() {
			var nextNodeKeyName = $(this).attr("name");
			var assignerIds = PlatUtil.getSelectAttValue(nextNodeKeyName,"assignerIds");
			var assignerNames = PlatUtil.getSelectAttValue(nextNodeKeyName,"assignerNames");
			$("input[name='nextNodeAssignerIds_0']").val(assignerIds);
			$("input[name='nextNodeAssignerNames_0']").val(assignerNames);
		});
	});

	function submitBusForm() {
		if (PlatUtil.isFormValid("#FlowSubmitForm")) {
			var url = $("#FlowSubmitForm").attr("action");
			var flowAssignJson = PlatUtil.getFlowNextAssignJson();
			var formData =  {};
			formData.jbpmHandleOpinion=  $("textarea[name='HANDLE_OPINION']").val();
			formData.jbpmAssignJson = flowAssignJson;
			formData.jbpmIsTempSave = "false";
			formData.flowToken = "${flowToken}";
			formData.jbpmHandleTaskStatus = "3";
			PlatUtil.ajaxProgress({
				url:"workflow/ExecutionController.do?exeFlow",
				params :formData,
				callback : function(resultJson) {
					if(resultJson.OPER_SUCCESS){
						var index = parent.layer.alert(resultJson.OPER_MSG, {
							icon : 1,
							resize : false,
							closeBtn : 0
						}, function() {
							PlatUtil.setData("submitSuccess",true);
							parent.layer.close(index); 
							PlatUtil.closeWindow();
						});
					}else{
						parent.layer.alert(resultJson.OPER_MSG,{icon: 2,resize:false});
					}
				}
			});
		}
	}
	function closeWindow() {
		PlatUtil.closeWindow();
	}
</script>


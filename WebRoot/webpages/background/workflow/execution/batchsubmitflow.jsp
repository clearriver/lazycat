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
				action="workflow/ExecutionController.do?batchExeFlow" id="FlowSubmitForm"
				style="">
				<input type="hidden" name="jbpmDefCode" value="${jbpmDefCode}">
				<div class="form-group plat-form-title" compcode="formtitle" id="">
					<span class="plat-current">待提交列表</span>
				</div>
				<div class="gridPanel">
					<table id="flowBatchTable"
						class="table table-bordered table-hover platedittable">
						<thead>
							<tr class="active">
								<th style="width:2%;">序号</th>
								<th style="width:20%;">标题</th>
								<th style="width:30%;">审核意见</th>
							</tr>
						</thead>
						<tbody id="flowBatchTableTbody">
						   <c:forEach items="${flowHandleList}" var="flowHandle" varStatus="varStatus">
							<tr id="flowBatchTable_${varStatus.index+1}">
								<td>${varStatus.index+1}
								<input type="hidden" name="flowExeId_${varStatus.index+1}" unsubmit="true" value="${flowHandle.flowExeId}">
								<input type="hidden" name="flowTaskId_${varStatus.index+1}" unsubmit="true" value="${flowHandle.flowTaskId}">
								<input type="hidden" name="flowMainRecordId_${varStatus.index+1}" unsubmit="true" value="${flowHandle.flowMainRecordId}">
								</td>
								<td>
								${flowHandle.flowTitle}
								</td>
								<td>
								<plattag:input name="flowHandleOpinion_${varStatus.index+1}" auth_type="write" maxlength="256"
									allowblank="true" placeholder="请输入审核意见"  comp_col_num="0"
									datarule="" attach_props="unsubmit='true'" >
								</plattag:input>
								</td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
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
			var formData = PlatUtil.getFormEleData("FlowSubmitForm");
			var flowHandleJson = PlatUtil.getEditTableAllRecordJson("flowBatchTable");
			formData.flowHandleJson = flowHandleJson;
			PlatUtil.ajaxProgress({
				url:url,
				params :formData,
				callback : function(resultJson) {
					if(resultJson.success){
						var index = parent.layer.alert(PlatUtil.SUCCESS_MSG, {
							icon : 1,
							resize : false,
							closeBtn : 0
						}, function() {
							PlatUtil.setData("submitSuccess",true);
							parent.layer.close(index); 
							PlatUtil.closeWindow();
						});
					}else{
						parent.layer.alert(PlatUtil.FAIL_MSG,{icon: 2,resize:false});
					}
				}
			});
		}
	}
	function closeWindow() {
		PlatUtil.closeWindow();
	}
</script>


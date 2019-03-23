<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>


<div class="alert alert-info" role="alert">
	<strong>操作说明:</strong>当前操作环节<b>【${flowVars.jbpmOperingNodeName}】</b>,下一环节审批类型
	<b id="nextAuditType">
	<c:if test="${nextAuditType=='1'}">【单人任务】</c:if>
	<c:if test="${nextAuditType=='2'}">【多人任务】</c:if>
	<c:if test="${nextAuditType=='3'}">【自由任务】</c:if>
	<c:if test="${nextAuditType=='4'}">【并行任务】</c:if>
	</b>
</div>
<div class="form-group plat-form-title" compcode="formtitle" id="">
	<span class="plat-current">下一环节信息</span>
</div>
<c:forEach items="${nextStepList}" var="nextStep" varStatus="varStatus">
    <%--处理非分支流程的情况 --%>
	<c:if test="${nextStep.isBranch=='-1'}">
	<div class="form-group" compcode="formgroup">
	    <input type="hidden" name="nextNodeKey_${varStatus.index}" 
	    isordertask="${nextStep.nodeAssignerList[0].isOrder}" 
	    handlernature="${nextStep.nodeAssignerList[0].handlerNature}"
	    value="${nextStep.nodeKeys}"
	    >
		<plattag:input name="nextNodeName_${varStatus.index}" allowblank="false" 
			auth_type="readonly" value="${nextStep.nodeNames}"
			datarule="required;" label_value="环节名称" placeholder="请输入环节名称"
			comp_col_num="4" label_col_num="2">
		</plattag:input>
		<c:if test="${nextStep.nodeAssignerList[0].handlerType=='1'}">
		<input type="hidden" name="nextNodeAssignerIds_${varStatus.index}" value="${nextStep.nodeAssignerList[0].assignerIds}">
		<plattag:input name="nextNodeAssignerNames_${varStatus.index}" allowblank="false" 
			auth_type="readonly" value="${nextStep.nodeAssignerList[0].assignerNames}"
			datarule="required;" label_value="办理人" placeholder="请选择办理人"
			comp_col_num="4" label_col_num="2">
		</plattag:input>
		</c:if>
		<c:if test="${nextStep.nodeAssignerList[0].handlerType=='2'}">
		<plattag:winselector placeholder="请选择办理人" minselect="1" maxselect="30"
		   allowblank="false" height="${nextStep.nodeAssignerList[0].handlerHeight}" 
		   comp_col_num="4" auth_type="write" selectedlabels="${nextStep.nodeAssignerList[0].assignerNames}"
		   selectorurl="${nextStep.nodeAssignerList[0].handlerUrl}&flowToken=${flowToken}&nextNodeKey=${nextStep.nodeKeys}" 
		   label_col_num="2" label_value="办理人" value="${nextStep.nodeAssignerList[0].assignerIds}"
		    width="${nextStep.nodeAssignerList[0].handlerWidth}" title="选择办理人" name="nextNodeAssignerIds_${varStatus.index}">
		</plattag:winselector>
		</c:if>
		
	</div>
	</c:if>
	<%--处理多分支流程的情况 --%>
	<c:if test="${nextStep.isBranch=='1'}">
	<div class="form-group" compcode="formgroup">
		<plattag:select istree="false" allowblank="false" value="${nextStep.branchSelectKey}"
		label_col_num="2" label_value="环节名称" placeholder="请选择环节" 
		dyna_interface="jbpmService.getNextStepBranch" dyna_param="${flowStepToken}"
		comp_col_num="4" auth_type="write" name="nextNodeKey_${varStatus.index}"></plattag:select>
		<div id="branchNextHandlerDiv">
		<c:if test="${nextStep.nodeAssignerList[0].handlerType=='1'}">
		<input type="hidden" name="nextNodeAssignerIds_${varStatus.index}" value="${nextStep.nodeAssignerList[0].assignerIds}">
		<plattag:input name="nextNodeAssignerNames_${varStatus.index}" allowblank="false" 
			auth_type="readonly" value="${nextStep.nodeAssignerList[0].assignerNames}"
			datarule="required;" label_value="办理人" placeholder="请选择办理人"
			comp_col_num="4" label_col_num="2">
		</plattag:input>
		</c:if>
		<c:if test="${nextStep.nodeAssignerList[0].handlerType=='2'}">
		<plattag:winselector placeholder="请选择办理人" minselect="1" maxselect="30"
		   allowblank="false" height="${nextStep.nodeAssignerList[0].handlerHeight}" 
		   comp_col_num="4" auth_type="write" selectedlabels="${nextStep.nodeAssignerList[0].assignerNames}"
		   selectorurl="${nextStep.nodeAssignerList[0].handlerUrl}&flowToken=${flowToken}&nextNodeKey=${nextStep.branchSelectKey}" 
		   label_col_num="2" label_value="办理人" value="${nextStep.nodeAssignerList[0].assignerIds}"
		    width="${nextStep.nodeAssignerList[0].handlerWidth}" title="选择办理人" name="nextNodeAssignerIds_${varStatus.index}">
		</plattag:winselector>
		</c:if>
		</div>
	</div>
	</c:if>
<div class="hr-line-dashed"></div>
</c:forEach>

<script type="text/javascript">
    $(function(){
    	$("select[name^='nextNodeKey']").change(function() {
    		var nextNodeKeyName = $(this).attr("name");
    		var nextNodeKey = $(this).val();
    		var handlertype = PlatUtil.getSelectAttValue(nextNodeKeyName,"handlertype");
    		var assignerIds = PlatUtil.getSelectAttValue(nextNodeKeyName,"assignerIds");
    		var assignerNames = PlatUtil.getSelectAttValue(nextNodeKeyName,"assignerNames");
    		$("#branchNextHandlerDiv").html("");
    		var params = {};
    		params.handlertype = handlertype;
    		if(assignerIds&&assignerIds!="null"){
    			params.assignerIds = assignerIds;
        		params.assignerNames = assignerNames;
    		}
    		params.flowToken = "${flowToken}";
    		params.nextNodeKey = nextNodeKey;
    		if(handlertype=="2"){
    			params.handlerUrl = PlatUtil.getSelectAttValue(nextNodeKeyName,"handlerUrl");
    			params.handlerWidth = PlatUtil.getSelectAttValue(nextNodeKeyName,"handlerWidth");
    			params.handlerHeight = PlatUtil.getSelectAttValue(nextNodeKeyName,"handlerHeight");
    			params.handlerHeight = PlatUtil.getSelectAttValue(nextNodeKeyName,"handlerHeight");
    		}
    		PlatUtil.ajaxProgress({
				url : "workflow/NodeAssignerController.do?getStepHandler",
				params : params,
				callback : function(resultText) {
					$("#branchNextHandlerDiv").html(resultText);
				}
			});
    	});
    });
  
</script>
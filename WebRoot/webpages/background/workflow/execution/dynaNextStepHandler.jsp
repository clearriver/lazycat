<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<c:if test="${stepHandler.handlertype=='1'}">
<input type="hidden" name="nextNodeAssignerIds_0" value="${stepHandler.assignerIds}">
<plattag:input name="nextNodeAssignerNames_0" allowblank="false" 
	auth_type="readonly" value="${stepHandler.assignerNames}"
	datarule="required;" label_value="办理人" placeholder="请选择办理人"
	comp_col_num="4" label_col_num="2">
</plattag:input>
</c:if>

<c:if test="${stepHandler.handlertype=='2'}">
<plattag:winselector placeholder="请选择办理人" minselect="1" maxselect="30"
   allowblank="false" height="${stepHandler.handlerHeight}" 
   comp_col_num="4" auth_type="write" selectedlabels="${stepHandler.assignerNames}"
   selectorurl="${stepHandler.handlerUrl}&flowToken=${stepHandler.flowToken}&nextNodeKey=${stepHandler.nextNodeKey}" 
   label_col_num="2" label_value="办理人" value="${stepHandler.assignerIds}"
    width="${stepHandler.handlerWidth}" title="选择办理人" name="nextNodeAssignerIds_0">
</plattag:winselector>
</c:if>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.stooges.core.util.PlatAppUtil"%>
<%@ page language="java" import="com.stooges.core.util.PlatFileUtil"%>
<%@ page language="java" import="com.stooges.platform.appmodel.service.DesignService"%>
<%@ page language="java" import="org.apache.commons.lang3.StringUtils"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
%>

<!-- 开始引入基本信息界面 -->
<jsp:include page='<%="/appmodel/DesignController/includeUI.do"%>' >
    <jsp:param value="jqtreegridbaseconfig" name="DESIGN_CODE"/>
</jsp:include>
<!-- 结束引入基本信息界面 -->

<script type="text/javascript">

$(function(){
	PlatUtil.initUIComp("#BaseConfigForm");
	$("select[name='TREE_TABLENAME']").change(function() {
		var tableName = $(this).val();
		PlatUtil.reloadSelect("ID_ANDNAME",{
			dyna_param:tableName
	    });
		PlatUtil.reloadSelect("TARGET_COLS",{
			dyna_param:tableName
	    });
	});
	
});
</script>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.stooges.core.util.PlatAppUtil"%>
<%@ page language="java" import="com.stooges.platform.appmodel.service.DesignService"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>


<div class="cpj-tpItem" id="${textPictureMatter.TEXTPICTURE_ID}">
	<c:if test="${IS_FIRST=='true'}">
		<div class="cpj-fibox clearfix"  onclick="selectItem(this);">
		<div class="nocpj-tpsimg">
	</c:if>
	<c:if test="${IS_FIRST=='false'}">
		<div class="cpj-fibox1 clearfix"  onclick="selectItem(this);">
		<div class="cpj-tpsimg">
	</c:if>
			<img src="" />
		</div>
		<h4></h4>
	</div>
	<div class="cpj-editBtn">
		<a href="javascript:void(0)" class="upClass" onclick="upItem(this);"><em class="fa fa-long-arrow-up" ></em></a>
		<a href="javascript:void(0)" class="downClass" onclick="downItem(this);"><em class="fa fa-long-arrow-down" ></em></a>
		<a href="javascript:void(0)" class="pull-right" onclick="delItem(this);"><em class="fa fa-trash-o"></em></a>
	</div>
</div>






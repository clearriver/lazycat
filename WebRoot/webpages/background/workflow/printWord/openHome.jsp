<%@page import="com.zhuozhengsoft.pageoffice.PageOfficeLink"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String docPath = request.getAttribute("docPath")+"";
String docReadOnly = request.getAttribute("docReadOnly")+"";
String docPrint = request.getAttribute("docPrint")+"";
String docSave = request.getAttribute("docSave")+"";
String callBackMethod = request.getAttribute("callBackMethod")+"";
%>

<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    <title>测试材料列表</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <plattag:resources restype="css" loadres="jquery-ui"></plattag:resources>
    <plattag:resources restype="js" loadres="jquery-ui"></plattag:resources>
  	<script type="text/javascript">
 window.onload = function(){ 
  
  if(!PO_checkPageOffice()){
   var poCheck = document.getElementById("po_check"); // 显示信息，提示客户安装PageOffice
   	poCheck.innerHTML = "<span style='font-size:12px;color:red;'>请先安装<a href=\"posetup.exe\" style=\"border:solid 1px #0473b3; color:#0473b3; padding:1px;\">PageOffice客户端</a></span>";
  }

 }
 $(function(){
	 if(PO_checkPageOffice()){
	 	$("#openDom>p").trigger("click"); 
	 }
 });
 function PO_checkPageOffice() {
	    var bodyHtml = document.body.innerHTML;
	    if (bodyHtml.indexOf("EC852C85-C2FC-4c86-8D6B-E4E97C92F821") < 0) {
	        var poObjectStr = "";
	        var explorer = window.navigator.userAgent;
	        //ie
	        //if (explorer.indexOf("MSIE") >= 0) {
	            //poObjectStr = "<div style=\"background-color:green;width:1px; height:1px;\">" + "\r\n"
				//+ "<object id=\"PageOfficeCtrl1\" height=\"100%\" width=\"100%\" classid=\"clsid:EC852C85-C2FC-4c86-8D6B-E4E97C92F821\">"
				//+ "</object></div>"
	        //}
	        //else {
	            poObjectStr = "<div style=\"background-color:green;width:1px; height:1px;\">" + "\r\n"
				+ "<object id=\"PageOfficeCtrl1\" height=\"100%\" width=\"100%\" type=\"application/x-pageoffice-plugin\" clsid=\"{EC852C85-C2FC-4c86-8D6B-E4E97C92F821}\">"
				+ "</object></div>"
	       // }

	        $(document.body).append(poObjectStr);
	    } 
	    
		try {
			var sCap = document.getElementById("PageOfficeCtrl1").Caption;
			if (sCap == null) {
				return false;
			}
			else {
				return true;
			}
		}
		catch (e) {console.log(e);return false; }
	}
 
   function setParam(){
	   /* $.ajax({
		   type: 'POST',
		   url: "workflow/WordBindController.do?setParam",
		   data: {docPath:"${docPath}",docReadOnly:"${docReadOnly}",docPrint:"${docPrint}",docSave:"${docSave}"},
		   success: function(result){
			   
		   }
		 }); */
		 setTimeout("PlatUtil.closeWindow();",1000);
   }     
</script>
  </head>
  
  <body>
    <div id="po_check"></div>
    <a href="<%=PageOfficeLink.openWindow(request,basePath+"workflow/WordBindController.do?printView&docPath="+docPath+"&docReadOnly="+docReadOnly+"&docPrint="+docPrint+"&docSave="+docSave+"&callBackMethod="+callBackMethod,"width=0;")%>" 
    id="openDom" onclick="setParam();"  style="display: none;"><p>打开</p></a>
  </body>
</html>



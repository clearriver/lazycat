<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@ page import="com.zhuozhengsoft.pageoffice.*,com.zhuozhengsoft.pageoffice.wordwriter.*"%>
<%@page import="com.stooges.core.util.PlatPropUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>
<%@ taglib uri="http://java.pageoffice.cn" prefix="po" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String attachFilePath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFilePath");
PageOfficeCtrl poCtrl1 = new PageOfficeCtrl(request);
WordDocument wordDoc = new WordDocument();
poCtrl1.setWriter(wordDoc);
poCtrl1.setCustomToolbar(true); //false的话则不出现pageoffice自己的工具栏
Object docReadOnly = request.getAttribute("docReadOnly");
if(docReadOnly!=null&&docReadOnly.toString().equals("true")){
    poCtrl1.setOfficeToolbars(false); //是否显示Office工具栏。 
    poCtrl1.setAllowCopy(false);
}else{
    poCtrl1.setOfficeToolbars(true);
}
Object docPrint = request.getAttribute("docPrint");
if(docPrint!=null&&docPrint.toString().equals("true")){
    poCtrl1.addCustomToolButton("打印", "ShowDialog_2()", 6);
}
Object docSave = request.getAttribute("docSave");
if(docSave!=null&&docSave.toString().equals("true")){
    poCtrl1.addCustomToolButton("保存", "Save", 1);
}
poCtrl1.addCustomToolButton("导出", "OtherSave", 1);
poCtrl1.addCustomToolButton("全屏/还原", "IsFullScreen", 4);
poCtrl1.addCustomToolButton("关闭", "closeDoc", 10);
poCtrl1.setServerPage(path + "/poserver.zz"); //此行必须
String filepath =null;
Object docPath  = request.getAttribute("docPath");
if(docPath!=null&&StringUtils.isNotEmpty(docPath.toString())){
    filepath = docPath.toString(); 
    Object callBackMethod  = request.getAttribute("callBackMethod");
    if(callBackMethod!=null&&StringUtils.isNotEmpty(callBackMethod.toString())){
        poCtrl1.setSaveFilePage(request.getContextPath() +  "/workflow/WordBindController/saveWordFile.do?docPath="+filepath+"&callBackMethod="+callBackMethod.toString());
    }else{
        poCtrl1.setSaveFilePage(request.getContextPath() +  "/workflow/WordBindController/saveWordFile.do?docPath="+filepath);  
    }
    
}
poCtrl1.setTitlebar(false);
poCtrl1.setMenubar(false);
poCtrl1.setOfficeVendor(OfficeVendorType.AutoSelect);
if(filepath!=null&&!filepath.equals("")){
    if(docReadOnly!=null&&docReadOnly.toString().equals("true")){
        poCtrl1.webOpen(attachFilePath+filepath, OpenModeType.docReadOnly, "evecom");
    }else{
        poCtrl1.webOpen(attachFilePath+filepath, OpenModeType.docNormalEdit, "evecom");
    }
}else{
    poCtrl1.webCreateNew("evecom",DocumentVersion.Word2003);
}
poCtrl1.setTagId("PageOfficeCtrl1"); //此行必须
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
  	//保存
    function Save(){
        document.getElementById("PageOfficeCtrl1").WebSave();
    	//window.external.close();
    }
  	//全屏/还原
    function IsFullScreen() {
        document.getElementById("PageOfficeCtrl1").FullScreen = !document
                .getElementById("PageOfficeCtrl1").FullScreen;
    }
  	//关闭
    function closeDoc() {
        if(document.getElementById("PageOfficeCtrl1").IsDirty){
            var r = window.confirm("数据未保存，是否确认关闭？")
            if (r==true){
            	window.external.close();
              }else{
                  return false; 
              }
        }else{
        	window.external.close();
        }
    }
  	//页面打印
    function ShowDialog_2() {
   	 	document.getElementById("PageOfficeCtrl1").WebSave();
        document.getElementById("PageOfficeCtrl1").ShowDialog(4);
    }
  	//另存为
    function OtherSave(){
    	document.getElementById("PageOfficeCtrl1").ShowDialog(3);
    }
    </script>
  </head>
  
  <body>
  		<po:PageOfficeCtrl id="PageOfficeCtrl1"></po:PageOfficeCtrl>
  </body>
</html>



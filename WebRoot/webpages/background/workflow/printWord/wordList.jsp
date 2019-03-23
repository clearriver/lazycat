<%@page import="com.stooges.core.util.PlatPropUtil"%>
<%@page import="com.stooges.platform.workflow.service.WordBindService"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.stooges.core.util.PlatStringUtil"%>
<%@page import="com.stooges.core.util.PlatAppUtil"%>
<%@ page language="java" import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
WordBindService wordBindService = (WordBindService)PlatAppUtil.getBean("wordBindService");
String TASK_ID = request.getParameter("TASK_ID");
String EXECUTION_ID = request.getParameter("EXECUTION_ID");
String FLOWDEF_ID = request.getParameter("FLOWDEF_ID");
Map<String,Object> map = new HashMap<String,Object>();
map.put("TASK_ID", TASK_ID);
map.put("EXECUTION_ID", EXECUTION_ID);
map.put("FLOWDEF_ID", FLOWDEF_ID);
List<Map<String, Object>> busWordList = wordBindService.findFilesList(map);
request.setAttribute("busWordList",busWordList);
Properties properties = PlatPropUtil.readProperties("conf/config.properties");
String PIC_AttachFileUrl = properties.getProperty("attachFileUrl");
request.setAttribute("PIC_AttachFileUrl", PIC_AttachFileUrl);
String webSocketBasePath = "ws://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
request.setAttribute("webSocketBasePath", webSocketBasePath);
%>
<style>
.busWordList-table>thead>tr>th,.busWordList-table>tbody>tr>td{
	text-align: center;
	vertical-align:middle;
}
.busWordListFileUploadDiv .webuploader-pick {
    background: #fff none repeat scroll 0 0 !important;
    border: 1px solid #ccc !important;
    border-radius: 4px !important;
    color: #333 !important;
    cursor: pointer !important;
    display: inline-block !important;
    overflow: hidden !important;
    padding: 5px 15px !important;
    position: relative !important;
    text-align: center !important;
}
.busWordListDisplayNone{
	display: none;
}
</style>
<div id="busWordListList_Div">
	<table class="table  table-bordered busWordList-table" >
			<thead>
				<tr class="active">
					<th style="width: 10%;">序号</th>
					<th style="width: 60%;">文书名称</th>
					<th style="width: 30%;">操作</th>
				</tr>
			</thead>
			<tbody>
				<c:if test="${busWordList!= null&&fn:length(busWordList) != 0}">
					<c:forEach items="${busWordList}" var="busWord" varStatus="varStatus">
						<tr id="${busWord.WORDBIND_ID}" data-filepath="${busWord.FILE_PATH}"  data-filename="${busWord.FILE_NAME}" data-tplcode="${busWord.TPL_CODE}" data-tplname="${busWord.TPL_NAME}">
							<td>${varStatus.index+1}</td>
							<td>${busWord.TPL_NAME}</td>
							<td>
								<div id="${busWord.WORDBIND_ID}_BEFOREUPLOADDIV" 
								 <c:if test="${busWord.AlreadyExists==true}">class="busWordListFileUploadDiv busWordListDisplayNone"</c:if>
								 <c:if test="${busWord.AlreadyExists!=true}">class="busWordListFileUploadDiv"</c:if>
								 style="height: 31px;overflow: hidden;">
									<c:if test="${busWord.WORDBIND_SUBTYPE=='1'||busWord.WORDBIND_SUBTYPE=='3'}">
										<span class="btn btn-outline btn-info btn-sm" style="margin-bottom: 22px;" type="button" onclick="onlineWordEdit('${busWord.WORDBIND_ID}','${busWord.TPL_CODE}')" >
											<i class="fa fa-pencil"></i>
											 在线编辑
										</span>
									</c:if>
									<c:if test="${busWord.WORDBIND_SUBTYPE=='2'||busWord.WORDBIND_SUBTYPE=='3'}">
										<span id="${busWord.WORDBIND_ID}_UPLOADWORDID" ><i class="fa fa-cloud-upload"></i>&nbsp;上传</span>
									</c:if>
								</div>
								<div id="${busWord.WORDBIND_ID}_AFTERUPLOADDIV" 
								 <c:if test="${busWord.AlreadyExists==true}">class=""</c:if>
								 <c:if test="${busWord.AlreadyExists!=true}">class="busWordListDisplayNone"</c:if>
								>
									<button class="btn btn-outline btn-primary btn-sm" type="button" onclick="showWord('${busWord.WORDBIND_ID}');">
										<i class="fa fa-search"></i>
										 查看
									</button>
									<button class="btn btn-outline btn-danger btn-sm" type="button" onclick="delWord('${busWord.WORDBIND_ID}');">
										<i class="fa fa-trash"></i>
										 删除
									</button>
								</div>
							</td>
						</tr>
					</c:forEach>
				</c:if>
				<c:if test="${busWordList== null||fn:length(busWordList) == 0}">
					<tr>
						<th colspan="3" style="text-align: center;">无可编辑文书</th>
					</tr>
				</c:if>
			</tbody>
	</table>
</div>
<script type="text/javascript">
	$(function(){
		//initCreateUploadWordButton();
	});
	
	function onlineWordEdit(bindId,tplCode){
		 var jbpmFlowInfo = PlatUtil.getJbpmFlowInfo();
		$("form[id]").each(function(index,obj){
			var formId = $(obj).attr("id");
			var formData = PlatUtil.getFormEleData(formId);
			jbpmFlowInfo = PlatUtil.mergeObject(jbpmFlowInfo,formData);
		}); 
		PlatUtil.ajaxProgress({
			url : "workflow/ExecutionController.do?cacheFlowInfo",
			params : jbpmFlowInfo,
			callback : function(resultJson) { 
				var flowToken = resultJson.flowToken;
				var url = "workflow/WordBindController.do?onlineEdit&clientId="+websocketUUID+"&bindId="+bindId;
				url  += "&tplCode="+tplCode+"&flowToken="+flowToken;
		    	PlatUtil.openWindow({
		    		title:"在线编辑",
		    		area: ["100%","100%"],
		    		content: url,
		    		end:function(){
		    		  
		    		}
		    	});
			 }		
		}); 
		
	}
	
	function createUploadWordButton(bindId){
		if($("#"+bindId+"_BEFOREUPLOADDIV").find(".webuploader-pick").length > 0){
			
		}else{
			var GUID = WebUploader.Base.guid();
			var initConfig = {
					// swf文件路径
				    swf: "plug-in/webuploader-0.1.5/dist/Uploader.swf",
				    // 文件接收服务端。
				    server: "system/FileAttachController/upload.do",
				    // 内部根据当前运行是创建，可能是input元素，也可能是flash.
				    accept:{
				        title: "Files",
				        extensions:'gif,jpg,jpeg,bmp,png,doc,docx,pdf,xls,xlsx,rar',
				    },
				    pick:{
				    	id:"#"+bindId+"_UPLOADWORDID"
				    },
				    // 不压缩image, 默认如果是jpeg，文件上传前会压缩一把再上传！
				    resize: false,
				    auto:true,
				    prepareNextFile:true,
				    chunked:true,
				    //默认20M进行分块
				    chunkSize:20971520,
				    //允许并发上传的文件个数
				    threads: 1,
				    duplicate:true,
				    fileSingleSizeLimit:20971520,
				    formData:{
				    	guid:GUID,
				    	uploadRootFolder:"uploadWord"
				    }
				};
			WebUploader.create(initConfig).on("uploadSuccess", function( file,response) {
				 var dbfilepath = response.dbfilepath;
				 var operBtnHtml = "";
			    $("#"+bindId).attr("data-filepath",response.dbfilepath);
			    $("#"+bindId).attr("data-filename",response.originalfilename);
			    if(!$("#"+bindId+"_BEFOREUPLOADDIV").hasClass("busWordListDisplayNone")){
					$("#"+bindId+"_BEFOREUPLOADDIV").addClass("busWordListDisplayNone");
				 }
			    if($("#"+bindId+"_AFTERUPLOADDIV").hasClass("busWordListDisplayNone")){
					$("#"+bindId+"_AFTERUPLOADDIV").removeClass("busWordListDisplayNone");
				 }
				parent.layer.close(initConfig.progressWinIndex);
			}).on("error",function(errortype){
				PlatUtil.webuploadErrorEvent(errortype,initConfig);
			}).on("startUpload",function(){
				PlatUtil.webuploadStartUploadEvent(initConfig);
			}).on("fileQueued",function(file){
				
			}).on("uploadProgress",function(file, percentage){
				
			}).on("uploadError",function(file){
				parent.layer.alert("上传失败！",{icon: 2,resize:false});
			});
		}
	}
	
	function showWord(bindId){
		var filePath = $("#"+bindId).attr("data-filepath");
		var fileName = $("#"+bindId).attr("data-filename");
		if(checkUploadFileExt(filePath,["jpg","jpeg","png"])){
			window.open("${PIC_AttachFileUrl}"+filePath);
		}else if(checkUploadFileExt(filePath,["docx","doc"])){
			onlineEditWord(filePath,false);
		}else{
			PlatUtil.downloadFile(filePath,fileName);
		}
	}
	
	function delWord(bindId){
	    var layerIndex = parent.layer.confirm("您确定要删除该文件吗?", {
		    resize :false
		}, function(){
			if($("#"+bindId+"_BEFOREUPLOADDIV").hasClass("busWordListDisplayNone")){
				$("#"+bindId+"_BEFOREUPLOADDIV").removeClass("busWordListDisplayNone");
			 }
		    if(!$("#"+bindId+"_AFTERUPLOADDIV").hasClass("busWordListDisplayNone")){
				$("#"+bindId+"_AFTERUPLOADDIV").addClass("busWordListDisplayNone");
			 }
		    $("#"+bindId).attr("data-filepath","");
		    $("#"+bindId).attr("data-filename","");
			parent.layer.close(layerIndex);
		}, function(){
			
		});
	}
	
	function checkUploadFileExt(filename,arr){
		 var flag = false; //状态
		 //取出上传文件的扩展名
		 var index = filename.lastIndexOf(".");
		 var ext = filename.substr(index+1);
		 //循环比较
		 for(var i=0;i<arr.length;i++){
			 if(ext == arr[i])
				 {
				  flag = true; //一旦找到合适的，立即退出循环
				  break;
				 }
		 }
		 return flag;
	}
	
	function onlineEditWord(filePath,docReadOnly){
		var url = "workflow/WordBindController.do?openHome&docPath="+filePath;
		if(docReadOnly){
			url+="&docReadOnly=true&docPrint=true&docSave=false&callBackMethod=";
		}else{
			url+="&docReadOnly=false&docPrint=true&docSave=true&callBackMethod=";
		}
    	PlatUtil.openWindow({
    		title:"在线编辑",
    		area: ["100%","100%"],
    		content: url,
    		end:function(){
    		  
    		}
    	});
	}
	
	function getWordUploadJson(){
		var wordUploadList = [];
		$("[id$='_UPLOADWORDID']").each(function(){
			var bindId = $(this).attr("id").replace("_UPLOADWORDID","");
			var filePath = $("#"+bindId).attr("data-filepath");
			var fileName = $("#"+bindId).attr("data-filename");
			var tplcode = $("#"+bindId).attr("data-tplcode");
			//if(PlatUtil.trim(filePath)!=""){
				var wordUpload = {};
				wordUpload.ATTACH_KEY = tplcode;
				wordUpload.FILE_NAME=fileName;
				wordUpload.FILE_PATH=filePath;
			 	wordUploadList.push(wordUpload);
			//}
		});
		return JSON.stringify(wordUploadList);
	}
	var websocketUUID = guid();
	var websocket = null;
	//判断当前浏览器是否支持WebSocket
	if ('WebSocket' in window) {
		websocket = new WebSocket("${webSocketBasePath}websocket");
	}else {
		alert('当前浏览器 Not support websocket');
	}
	//连接发生错误的回调方法
	websocket.onerror = function () {
	};
	//连接成功建立的回调方法
	websocket.onopen = function () {
		websocket.send("{UUID:'"+websocketUUID+"',TYPE:'1'}");
	}
	//接收到消息的回调方法
	websocket.onmessage = function (event) {
		var result = JSON.parse(event.data);
		var bindId = result.bindId;
		var filePath = result.filePath;
		$("#"+bindId).attr("data-filepath",filePath);
	    $("#"+bindId).attr("data-filename",$("#"+bindId).attr("data-tplname"));
	    if(!$("#"+bindId+"_BEFOREUPLOADDIV").hasClass("busWordListDisplayNone")){
			$("#"+bindId+"_BEFOREUPLOADDIV").addClass("busWordListDisplayNone");
		 }
	    if($("#"+bindId+"_AFTERUPLOADDIV").hasClass("busWordListDisplayNone")){
			$("#"+bindId+"_AFTERUPLOADDIV").removeClass("busWordListDisplayNone");
		 }
	}
	//连接关闭的回调方法
	websocket.onclose = function () {
	
	}
	//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
	window.onbeforeunload = function () {
		closeWebSocket();
	}
	//关闭WebSocket连接
	function closeWebSocket() {
		websocket.send("{UUID:'"+websocketUUID+"',TYPE:'2'}");
		websocket.close();
	}
	function guid() {
	    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
	        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
	        return v.toString(16);
	    });
	}
	
	function initCreateUploadWordButton(){
		$("[id$='_UPLOADWORDID']").each(function(){
			var id = $(this).attr("id");
			createUploadWordButton(id.replace("_UPLOADWORDID",""));
		});
	}
</script>



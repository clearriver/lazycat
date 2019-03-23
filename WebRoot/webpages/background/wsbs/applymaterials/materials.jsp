<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.stooges.core.util.PlatStringUtil"%>
<%@page import="com.stooges.platform.wsbs.service.MaterialsService"%>
<%@page import="com.stooges.core.util.PlatAppUtil"%>
<%@ page language="java" import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
MaterialsService materialsService = (MaterialsService)PlatAppUtil.getBean("materialsService");
String SERITEM_ID = request.getParameter("SERITEM_ID");
String EXECUTION_ID = request.getParameter("EXECUTION_ID");
String auth_type = request.getParameter("auth_type");
Map<String,Object> map = new HashMap<String,Object>();
map.put("SERITEM_ID", SERITEM_ID);
map.put("EXECUTION_ID", EXECUTION_ID);
List<Map<String, Object>> materialsList = materialsService.findFilesList(map);
String materialsJson = PlatStringUtil.toJsonString(materialsList,new String[]{"uploadFiles"},true);
request.setAttribute("materialsJson", StringEscapeUtils.escapeHtml3(materialsJson));
request.setAttribute("materialsList",materialsList);
if(StringUtils.isEmpty(auth_type)){
    auth_type = "write";
}
request.setAttribute("auth_type", auth_type);
%>
<style>
.materials-table>thead>tr>th,.materials-table>tbody>tr>td{
	text-align: center;
	vertical-align:middle;
}
.materialsFileUploadDiv .webuploader-pick {
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
.materialsDisplayNone{
	display: none;
}
</style>
<div id="materialsList_Div">
	<input type="hidden" name="materialsJson" value="${materialsJson}"/>
	<table class="table  table-bordered materials-table" >
			<thead>
				<tr class="active">
					<th style="width: 5%;">序号</th>
					<th style="width: 30%;">材料名称</th>
					<th style="width: 10%;">材料说明</th>
					<th style="width: 35%;">附件</th>
					<th style="width: 10%;">
						提交方式<br/>
						<plattag:select istree="false" allowblank="true" comp_col_num="12" auth_type="${auth_type}" name="allmaterialselect"
						static_values="纸制收取:2,电子档上传:1,未提交:3" placeholder="" value=""
						></plattag:select>
					</th>
					<th style="width: 10%;">文件操作</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${materialsList}" var="applyMater" varStatus="varStatus">
					<c:if test="${!empty applyMater.MATER_TYPENAME_TOP}">
					<tr>
						<td colspan="6" style="text-align: left;font-weight:bold;color: #0C83D3;">${applyMater.MATER_TYPENAME_TOP}</td>
					</tr>
					</c:if>
					<tr id="material_${applyMater.SERMATER_ID}" >
						<td>${applyMater.MATER_INDEX}</td>
						<td>
						<c:if test="${applyMater.SERMATER_ISNEED=='1'}">
							<font color="#ff0101">*</font>
						</c:if>
						${applyMater.SERMATER_NAME}
						</td>
						<td>${applyMater.SERMATER_DESC}</td>
						<td>
							<div id="UploadedFiles_${applyMater.SERMATER_ID}" class="materialsDisplayNone">
								<c:if test="${applyMater.FILE_UPLOADTYPE=='1'}">
									<c:forEach var="uploadFile" items="${applyMater.uploadFiles}">	
												<p id="${uploadFile.FILE_ID}" filename="${uploadFile.FILE_NAME}" filepath="${uploadFile.FILE_PATH}">
										            <a title="${uploadFile.FILE_NAME}" onclick="PlatUtil.downloadFile('${uploadFile.FILE_PATH}','${uploadFile.FILE_NAME}');" 
										            href="javascript:void(0);">${uploadFile.FILE_NAME}</a>
										            <c:if test="${auth_type=='write'}">
													<a title="删除" style="color:red;" onclick="deleteMaterialsFileUpload('${uploadFile.FILE_ID}');" href="javascript:void(0);">
														<i class="fa fa-trash-o"></i>
													</a>
													</c:if>
										        </p>
									</c:forEach>
								</c:if>
							</div>
						</td>
						<td>
							<plattag:select istree="false" allowblank="false" comp_col_num="12" auth_type="${auth_type}" name="${applyMater.SERMATER_ID}_materialselect"
							static_values="纸制收取:2,电子档上传:1,未提交:3" placeholder="" value="${applyMater.FILE_UPLOADTYPE}"
							></plattag:select>
						</td>
						<td>
							<div class="materialsFileUploadDiv materialsDisplayNone" id="${applyMater.SERMATER_ID}_UPLOADID_DIV" 
							SERMATER_CODE="${applyMater.SERMATER_ID}" SERMATER_TYPE="${applyMater.SERMATER_TYPE}" 
							SERMATER_SIZE="${applyMater.SERMATER_SIZE}">
							    <span id="${applyMater.SERMATER_ID}_UPLOADID" ><i class="fa fa-cloud-upload"></i>&nbsp;添加附件</span>
							</div>
						</td>
					</tr>
				</c:forEach>
			</tbody>
	</table>
</div>

<script type="text/javascript">
$(function(){
	$("select[name='allmaterialselect']").change(function(){ 
		var allSelectVal = $(this).val();
		if(allSelectVal!=null){
			$("select[name$='_materialselect']").each(function(){
				$(this).val(allSelectVal).trigger("change");
			});
		}
		
	});
	$("select[name$='_materialselect']").change(function(){ 
		 var name = $(this).attr("name");
		 var selValue = $(this).val();
		 var SERMATER_ID = name.replace("_materialselect","");
		 var UploadId = SERMATER_ID+"_UPLOADID_DIV";
		 var UploadedFilesId = "UploadedFiles_"+SERMATER_ID;
		 if(selValue=="1"){
			 if($("#"+UploadId).hasClass("materialsDisplayNone")){
				 $("#"+UploadId).removeClass("materialsDisplayNone");
			 }
			 if($("#"+UploadedFilesId).hasClass("materialsDisplayNone")){
				 $("#"+UploadedFilesId).removeClass("materialsDisplayNone");
			 }
			 createApplyUploadMaterials(SERMATER_ID);
		 }else{
			if(!$("#"+UploadId).hasClass("materialsDisplayNone")){
				$("#"+UploadId).addClass("materialsDisplayNone");
			 }
			if(!$("#"+UploadedFilesId).hasClass("materialsDisplayNone")){
				$("#"+UploadedFilesId).addClass("materialsDisplayNone");
			 }
		 }
	  });
	initAfterApplyUploadMaterials();
});

function initAfterApplyUploadMaterials(){
	$("select[name$='_materialselect']").each(function(){
		var name = $(this).attr("name");
		var selValue = $(this).val();
		var SERMATER_ID = name.replace("_materialselect","");
		 var UploadId = SERMATER_ID+"_UPLOADID_DIV";
		 var UploadedFilesId = "UploadedFiles_"+SERMATER_ID;
		if(selValue=="1"){
			 if($("#"+UploadId).hasClass("materialsDisplayNone")){
				 $("#"+UploadId).removeClass("materialsDisplayNone");
			 }
			 if($("#"+UploadedFilesId).hasClass("materialsDisplayNone")){
				 $("#"+UploadedFilesId).removeClass("materialsDisplayNone");
			 }
			 createApplyUploadMaterials(SERMATER_ID);
		 }else{
			if(!$("#"+UploadId).hasClass("materialsDisplayNone")){
				$("#"+UploadId).addClass("materialsDisplayNone");
			 }
			if(!$("#"+UploadedFilesId).hasClass("materialsDisplayNone")){
				$("#"+UploadedFilesId).addClass("materialsDisplayNone");
			 }
		 }
		if("${auth_type}"!="write"){
			if(!$("#"+UploadId).hasClass("materialsDisplayNone")){
				$("#"+UploadId).addClass("materialsDisplayNone");
			 }
		}
	});
}



function createApplyUploadMaterials(id){
	if($("#"+id+"_UPLOADID_DIV").find(".webuploader-pick").length > 0){
		
	}else{
		//获取材料编码
		var SERMATER_CODE = $("#"+id+"_UPLOADID_DIV").attr("SERMATER_CODE");
		//获取允许上传文件类型
		var SERMATER_TYPE = $("#"+id+"_UPLOADID_DIV").attr("SERMATER_TYPE");
		//获取允许上传文件大小
		var SERMATER_SIZE = $("#"+id+"_UPLOADID_DIV").attr("SERMATER_SIZE");
		var SERMATER_ID = id;
		var GUID = WebUploader.Base.guid();
		var initConfig = {
			// swf文件路径
		    swf: "plug-in/webuploader-0.1.5/dist/Uploader.swf",
		    // 文件接收服务端。
		    server: "system/FileAttachController/upload.do",
		    // 内部根据当前运行是创建，可能是input元素，也可能是flash.
		    accept:{
		        title: "Files",
		        extensions:SERMATER_TYPE,
		    },
		    pick:{
		    	id:"#"+SERMATER_ID+"_UPLOADID"
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
		    fileSingleSizeLimit:SERMATER_SIZE,
		    formData:{
		    	guid:GUID,
		    	uploadRootFolder:"materials"
		    }
		};
		WebUploader.create(initConfig).on("uploadSuccess", function( file,response) {
			 var dbfilepath = response.dbfilepath;
			 var operBtnHtml = "";
		    operBtnHtml = "<p id=\"" + file.id + "\" filename=\""+file.name+"\" filepath=\""+dbfilepath+"\"><a title=\""+file.name+"\" onclick=\"PlatUtil.downloadFile('"+dbfilepath+"','"+file.name+"');\" href=\"javascript:void(0);\" >"+file.name+"</a>";
		    operBtnHtml += "<a title=\"删除\" style=\"color:red;\" onclick=\"deleteMaterialsFileUpload('"+file.id+"');\" href=\"javascript:void(0);\" ><i class=\"fa fa-trash-o\"></i></a></p>";
		    $("#UploadedFiles_"+id).append(operBtnHtml);
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

function deleteMaterialsFileUpload(fileId){
	var layerIndex = parent.layer.confirm("您确定删除该文件吗?", {
		icon: 7,
	    resize :false
	}, function(){
		$("#"+fileId).remove(); 
		parent.layer.close(layerIndex);
	}, function(){
		
	});	
}

function getMaterialUploadJson(){
	var result = {};
	var submitMaterList = [];
	var materialsJson = $("input[name='materialsJson']").val();
	var materialsObj = $.parseJSON(materialsJson);
	//定义是否上传了必须上传的材料
	var isUploadMustFile = true;
	for(var index in materialsObj){
		//获取材料编码
		var SERMATER_CODE = materialsObj[index].SERMATER_CODE;
		var SERMATER_ID = materialsObj[index].SERMATER_ID;
		//获取是否必须上传
		var SERMATER_ISNEED = materialsObj[index].SERMATER_ISNEED;
		var selectVal = $("select[name='"+SERMATER_ID+"_materialselect']").val();
		if(SERMATER_ISNEED=="1"){
			if(selectVal=="3"){
				isUploadMustFile = false;
				break;
			}else if(selectVal=="1"){
				var leftSpanSize = $("#UploadedFiles_"+SERMATER_ID).children("p").length;
				if(leftSpanSize==0){
					isUploadMustFile = false;
					break;
				}
			}
		}
	 }
	if(!isUploadMustFile){
		 //alert("材料列表中带*号的必填项目不能设置为“未收取”，请检查!");
		 parent.layer.alert("带*号的项目必须提交，请检查!",{icon: 2,resize:false});
		 result.validate = false;
		 return result;
	 }else{
	 	
	 }
	for(var index in materialsObj){
		var SERMATER_ID = materialsObj[index].SERMATER_ID;
		var SERMATER_CODE = materialsObj[index].SERMATER_CODE;
		var selectVal = $("select[name='"+SERMATER_ID+"_materialselect']").val();
		if(selectVal=="1"){
			var uploadedFilesSpans = $("div[id^='UploadedFiles_"+SERMATER_ID+"'] > p[id]");
			if(uploadedFilesSpans.length>0){
				 $.each(uploadedFilesSpans,function(index,span) { 
				 	 var submitMater = {};
				 	 submitMater.ATTACH_KEY = SERMATER_CODE;
				 	 submitMater.RECEIVE_METHOD = "1";
				 	 submitMater.FILE_NAME=$(this).attr("filename");
				 	 submitMater.FILE_PATH=$(this).attr("filepath");
				 	 submitMaterList.push(submitMater);
		         }); 
			 }else{
				 var submitMater = {};
			 	 submitMater.ATTACH_KEY = SERMATER_CODE;
			 	 submitMater.RECEIVE_METHOD = "3";
			 	 submitMater.FILE_NAME="";
			 	 submitMater.FILE_PATH="";
			 	 submitMaterList.push(submitMater);
			 }
		}else if(selectVal=="2"){
			 var submitMater = {};
		 	 submitMater.ATTACH_KEY = SERMATER_CODE;
		 	 submitMater.RECEIVE_METHOD = "2";
		 	 submitMater.FILE_NAME="";
		 	 submitMater.FILE_PATH="";
		 	 submitMaterList.push(submitMater);
		}else if(selectVal=="3"){
			 var submitMater = {};
		 	 submitMater.ATTACH_KEY = SERMATER_CODE;
		 	 submitMater.RECEIVE_METHOD = "3";
		 	 submitMater.FILE_NAME="";
		 	 submitMater.FILE_PATH="";
		 	 submitMaterList.push(submitMater);
		}
	}
	var submitMaterFileJson = JSON.stringify(submitMaterList);
	result.submitMaterFileJson =submitMaterFileJson;
	result.validate = true
	return result;
}
</script>


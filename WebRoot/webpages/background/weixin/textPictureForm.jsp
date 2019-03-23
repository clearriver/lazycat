<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    <title>图文素材表单</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <plattag:resources restype="css" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid,ueditor"></plattag:resources>
    <plattag:resources restype="js" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid,ueditor"></plattag:resources>
  	<link rel="stylesheet" type="text/css" href="webpages/background/weixin/css/index.css">
  	<style type="text/css">
  	.cpj-fibox + .cpj-editBtn   .upClass{
  		display:none;
  	}
  	.cpj-fibox + .cpj-editBtn   .pull-right{
  		display:none;
  	}
  	
  	</style>
  </head>
  
  <body style="background: #fff;">
  		<input type="hidden" name="PUBLIC_ID" value="${PUBLIC_ID}" />
  		 <div class="plat-directlayout" style="height:100%" platundragable="true" compcode="direct_layout"  layoutsize="&quot;west__size&quot;:280,&quot;east__size&quot;:200">
			    <div class="ui-layout-west" platundragable="true" compcode="direct_layout" style="overflow-y:auto;">		
			   		<div class="panel-Title">
						<h5 id="baselist_datatablepaneltitle">图文列表</h5>
					</div>
			   		<div class="cpj-tpList">
						<div id="titleDiv">
							<c:forEach items="${titelItem}" var="textPictureMatter" varStatus="status">
								<div class="cpj-tpItem" id="${textPictureMatter.TEXTPICTURE_ID}">
									<c:if test="${status.index==0}">
										<div class="cpj-fibox clearfix"  onclick="selectItem(this);">
										<div class="nocpj-tpsimg">
									</c:if>
									<c:if test="${status.index!=0}">
										<div class="cpj-fibox1 clearfix"  onclick="selectItem(this);">
										<div class="cpj-tpsimg">
									</c:if>
											<img src="${textPictureMatter.TEXTPICTURE_COVER}" />
										</div>
										<h4>${textPictureMatter.TEXTPICTURE_TITLE}</h4>
									</div>
									<div class="cpj-editBtn">
										<a href="javascript:void(0)" class="upClass" onclick="upItem(this);"><em class="fa fa-long-arrow-up" ></em></a>
										<a href="javascript:void(0)" class="downClass" onclick="downItem(this);"><em class="fa fa-long-arrow-down" ></em></a>
										<a href="javascript:void(0)" class="pull-right" onclick="delItem(this);"><em class="fa fa-trash-o"></em></a>
									</div>
								</div>
							
							</c:forEach>
						</div>
						<div class="cpj-tpAdd">
							<i class="cpj-tpAicon"></i>
							<a href="javascript:void(0)" onclick="addNewTextPicture();">
								<em class="fa fa-pencil-square-o"></em>
								<strong>自建图文</strong>
							</a>
							<!-- <a href="javascript:void(0)">
								<em class="fa fa-share-alt">2</em>
								<strong>分享图文</strong>
							</a> -->
						</div>
					</div>
			</div>
			<div class="ui-layout-center" platundragable="true" compcode="direct_layout" style="overflow-y:auto;">
					<div class="panel-Title">
						<h5 id="baselist_datatablepaneltitle">图文内容</h5>
					</div>
					<form method="post" class="form-horizontal" compcode="formcontainer" action="" id="baseform" style="">
						<input type="hidden" name="TEXTPICTURE_ID" value="">
						<div class="form-group" compcode="formgroup">
							<plattag:input name="TEXTPICTURE_TITLE"  allowblank="false" auth_type="write" value="" datarule="required;" maxlength="64" label_value="标题" placeholder="请输入标题" comp_col_num="10" label_col_num="2">
   							</plattag:input>
   						</div>
   						<div class="hr-line-dashed"></div>
   						<div class="form-group" compcode="formgroup">
							<plattag:input name="TEXTPICTURE_AUTHOR" allowblank="false" auth_type="write" value="" datarule="required;" maxlength="8" label_value="作者" placeholder="请输入作者" comp_col_num="10" label_col_num="2">
   							</plattag:input>
   						</div>
						<div class="hr-line-dashed"></div>
						<div class="form-group" compcode="formgroup">
							<label class="col-sm-2 control-label">
								文章内容：
							</label>
							<div class="col-sm-10">
							   <script id="TEXTPICTURE_CONTENT" name="TEXTPICTURE_CONTENT" type="text/plain"></script>
							</div>
						</div>
						<div class="hr-line-dashed"></div>
						<div class="form-group" compcode="formgroup">
							<plattag:checkbox name="TEXTPICTURE_ORIGINALTEXT" auth_type="write" allowblank="true" 
							is_horizontal="true" comp_col_num="10" static_values="有:1" label_value="是否有原文地址" label_col_num="2"></plattag:checkbox>
							
						</div>
						<div class="form-group" compcode="formgroup" id="ORIGINALTEXTURL_DIV">
							<plattag:input name="TEXTPICTURE_ORIGINALTEXTURL"  allowblank="true" auth_type="write" value="" datarule="" maxlength="64" label_value="原文地址" placeholder="请输入原文地址" comp_col_num="10" label_col_num="2">
   							</plattag:input>
   						</div>
   						<div class="hr-line-dashed"></div>
						<div class="form-group" compcode="formgroup">
							<plattag:checkbox name="TEXTPICTURE_LEAVINGMES" auth_type="write" allowblank="true" 
							is_horizontal="true" comp_col_num="10" static_values="是:1" label_value="是否支持留言" label_col_num="2"></plattag:checkbox>
							
						</div>
						<div class="form-group" compcode="formgroup" id="LEAVINGMES_DIV">
							<plattag:radio name="TEXTPICTURE_LEAVINGMESAUTH" auth_type="write" select_first="true" allowblank="false" is_horizontal="true" 
							comp_col_num="10" static_values="所有人可留言:1,仅关注后可留言:2" label_value="可留言类型" label_col_num="2"></plattag:radio>
   						</div>
						<!-- <div class="hr-line-dashed"></div>
   						<div class="form-group" compcode="formgroup">
   							<label class="col-sm-2 control-label">
								原创：
							</label>
							<div class="col-sm-10">
								<span id="YCSM">未声明</span>
								<button type="button" onclick="OriginalStatement();"   class="btn btn-outline btn-info btn-sm">
										原创声明
								</button>
							</div>
   						</div> -->
   						<div class="hr-line-dashed"></div>
   						<div class="form-group" compcode="formgroup">
   							<label class="col-sm-2 control-label">
								封面：
							</label>
							<div class="col-sm-10">
								<button type="button" onclick="selectCoverPhotoFromHtml();"   class="btn btn-outline btn-default btn-sm">
										从正文选择
								</button>
								<button type="button" onclick="selectCoverPhotoFromSystem();"   class="btn btn-outline btn-default btn-sm">
										从图片库选择
								</button>
							</div>
   						</div>
   						<div class="form-group" compcode="formgroup" id="COVER_PHOTO_DIV" style="display: none;">
   							<label class="col-sm-2 control-label">
   							<input type="hidden" name="TEXTPICTURE_COVER" value="">
							</label>
   							<div class="col-sm-10" >
								<img id="COVER_PHOTO"
								src="" 
								style="height: 117px;">
							</div>
   						</div>
						<div class="hr-line-dashed"></div>
						<div class="form-group" compcode="formgroup">
							   <plattag:textarea name="TEXTPICTURE_SUMMARY" allowblank="true" auth_type="write" value="" maxlength="120" label_value="摘要" placeholder="选填，如果不填写会默认抓取正文前54个字" comp_col_num="10" label_col_num="2">
							   </plattag:textarea>
						</div>
					</form>
				
			</div>
			<div class="ui-layout-east" platundragable="true" compcode="direct_layout" style="overflow-y:auto;">		
				<div class="panel-Title">
						<h5 id="baselist_datatablepaneltitle">多媒体</h5>
				</div>
				<div class="list-group">
					  <a href="javascript:void(0);" onclick="insertPicture();" class="list-group-item">
					    <i class="fa fa-picture-o"></i> 图片
					  </a>
					  <a href="javascript:void(0);" onclick="insertVideo();" class="list-group-item">
					    <i class="fa fa-video-camera"></i> 视频
					  </a>
				</div>
				<div id="imgDiv" style="display: none;">
					<div class="panel-Title">
						<h5 id="baselist_datatablepaneltitle">图片属性</h5>
					</div>
					<div class="form-horizontal">
						<input type="hidden" name="PICTURE_ID" val=""> 
						<input type="hidden" name="OLD_PICTURE_IS_AUTO" val=""> 
						<div class="form-group" >
							 <plattag:radio name="PICTURE_IS_AUTO" auth_type="write"  select_first="false"
							  allowblank="false" is_horizontal="false" 
							  comp_col_num="6" static_values="是:1,否:0" value=""
							  label_col_num="6"  label_value="是否自适应手机屏幕宽度"></plattag:radio>
						</div>
						<div class="hr-line-dashed"></div>
						<div class="form-group" >
								<div class="col-sm-12 text-center">
										<button type="button" onclick="savePictureData();"   class="btn btn-outline btn-info btn-sm">
											<i class="fa fa-check"></i>&nbsp;应用
										</button>
								</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="ui-layout-south" platundragable="true" compcode="direct_layout">
				 <div class="row" style="height:40px; margin-top: -6px;padding-top: 4px; background: #e5e5e5;" platundragable="true" compcode="buttontoolbar">
				 		<div class="col-sm-12 text-center">
							<button type="button" onclick="saveData();"   class="btn btn-outline btn-info btn-sm">
								<i class="fa fa-check"></i>&nbsp;保存
							</button>
							<button type="button" onclick="closeWindow();" platreskey="" class="btn btn-outline btn-danger btn-sm">
								<i class="fa fa-times"></i> 关闭
							</button>
					     </div>
				 </div>
			</div>
		</div>
  </body>
</html>

<script type="text/javascript">
	$(function(){
		  PlatUtil.initUIComp();
		  
		  var editor = UE.getEditor('TEXTPICTURE_CONTENT', {
			    autoHeightEnabled: false,
			    autoFloatEnabled: true,
			    enableAutoSave :false,
			    fullscreen:false,
			    saveInterval:10000000000000000,
			    maximumWords:50000,
			    initialFrameWidth:"99.8%",
			    initialFrameHeight:400,
			    //imageScaleEnabled:false,
			    toolbars: [
	               ['undo', 'redo', 'fontsize','blockquote',
	                'removeformat','formatmatch', 
	                'link','unlink','italic','underline',
	                'forecolor','backcolor','indent', 'justifyleft',
	                'justifycenter','justifyright','justifyjustify',
	                'rowspacingtop','rowspacingbottom',
	                'lineheight','insertorderedlist','insertunorderedlist',
	                'imagenone','imageleft','imageright','imagecenter',
	                'horizontal','bold']
	            ]
		            
			});
		  
		  editor.addListener( 'ready', function( editor ) {
			  	 //编辑器渲染完成之后选中第一个
				  if($("#titleDiv").find(".cpj-tpItem").length>0){
					  $($("#titleDiv").find(".cpj-tpItem").first().children("div").get(0)).trigger("click"); 
				  }
			 } );
		  
		  $("input[name='TEXTPICTURE_TITLE']").bind("input",function(){
			  $(".cpj-nowTp").find("h4").text($(this).val());
		  });
		  
		  $("input[name='TEXTPICTURE_ORIGINALTEXT']").bind("click",function(){
		  		changeOriginalText();
		  });
		  changeOriginalText();
		  
		  $("input[name='TEXTPICTURE_LEAVINGMES']").bind("click",function(){
		  		changeLeavingMes();
		  });
		  changeLeavingMes();
	});
	//是否有原文地址
	function changeOriginalText(){
		var v = $("input[name='TEXTPICTURE_ORIGINALTEXT']:checked").val();
		if(v=="1"){
			$("#ORIGINALTEXTURL_DIV").show();
		}else{
			$("#ORIGINALTEXTURL_DIV").hide();
		}
	}
	
	//是否可以留言
	function changeLeavingMes(){
		var v = $("input[name='TEXTPICTURE_LEAVINGMES']:checked").val();
		if(v=="1"){
			$("#LEAVINGMES_DIV").show();
		}else{
			$("#LEAVINGMES_DIV").hide();
		}
	}
	
	
	//选择要编辑的
	function selectItem(event){
		$(".cpj-tpList").find(".cpj-tpItem").each(function(){
			$(this).removeClass("cpj-nowTp");
		});
		
		$(event).closest(".cpj-tpItem").addClass("cpj-nowTp");
		var id = $(event).closest(".cpj-tpItem").attr("id");
		//回填数据
		backFillContent(id);
	}
	//数据回填
	function backFillContent(TEXTPICTURE_ID){
		$("input[name='TEXTPICTURE_ID']").val(TEXTPICTURE_ID);
		PlatUtil.ajaxProgress({
			url:"weixin/TextPictureMatterController.do?getContent",
			async:"-1",
			showProgress:false,
			params :{
				TEXTPICTURE_ID:TEXTPICTURE_ID
		    },
			callback : function(resultJson) {
				$("input[name='TEXTPICTURE_TITLE']").val(resultJson.TEXTPICTURE_TITLE);
				$("input[name='TEXTPICTURE_AUTHOR']").val(resultJson.TEXTPICTURE_AUTHOR);
				if(resultJson.TEXTPICTURE_CONTENT){
					UE.getEditor('TEXTPICTURE_CONTENT').setContent(resultJson.TEXTPICTURE_CONTENT); 
				}else{
					UE.getEditor('TEXTPICTURE_CONTENT').execCommand('cleardoc');
				}
				if(resultJson.TEXTPICTURE_ORIGINALTEXT){
					$("input[name='TEXTPICTURE_ORIGINALTEXT'][value='1']").prop("checked",true);
				}else{
					$("input[name='TEXTPICTURE_ORIGINALTEXT'][value='1']").prop("checked",false);
				}
				changeOriginalText();
				if(resultJson.TEXTPICTURE_LEAVINGMES){
					$("input[name='TEXTPICTURE_LEAVINGMES'][value='1']").prop("checked",true);
				}else{
					$("input[name='TEXTPICTURE_LEAVINGMES'][value='1']").prop("checked",false);
				}
			    changeLeavingMes();
			    $("input[name='TEXTPICTURE_LEAVINGMESAUTH'][value='"+resultJson.TEXTPICTURE_LEAVINGMESAUTH+"']").prop("checked",true);
			    $("input[name='TEXTPICTURE_ORIGINALTEXTURL']").val(resultJson.TEXTPICTURE_ORIGINALTEXTURL);
			    $("input[name='TEXTPICTURE_COVER']").val(resultJson.TEXTPICTURE_COVER);
			    if(resultJson.TEXTPICTURE_COVER){
			    	$("#COVER_PHOTO").attr("src",resultJson.TEXTPICTURE_COVER);
			    	$("#COVER_PHOTO_DIV").show();
			    }else{
			    	$("#COVER_PHOTO_DIV").hide();
			    }
			    $("textarea[name='TEXTPICTURE_SUMMARY']").val(resultJson.TEXTPICTURE_SUMMARY);
			}
		});

	}
	
	
	//插入图片
	function insertPicture(){
		 UE.getEditor('TEXTPICTURE_CONTENT').focus();
		 var publicId =  $("input[name='PUBLIC_ID']").val();
		 PlatUtil.openWindow({
			  title:"选择图片",
			  area: ["70%","70%"],
			  content: "weixin/TextPictureMatterController.do?goImgSelectForm&allowCount=0&publicId="+publicId,
			  end:function(){
				  var selectImgItem = PlatUtil.getData("selectImgItem");
			     if(selectImgItem){
			    		PlatUtil.removeData("selectImgItem");
			    		for(j = 0; j < selectImgItem.length; j++) {
			    			var uuid = PlatUtil.getUUID();
			    			UE.getEditor('TEXTPICTURE_CONTENT').execCommand('insertHtml', '<p><img id="'+uuid+'" onclick="parent.clickImg(this);"    src="'+selectImgItem[j].wxsrc+'"></p>');
			    		} 
			      }
			  }
		 });
	}
	//插入视频
	function insertVideo(){
		 UE.getEditor('TEXTPICTURE_CONTENT').focus();
		 var publicId =  $("input[name='PUBLIC_ID']").val();
		 PlatUtil.openWindow({
			  title:"选择视频",
			  area: ["70%","70%"],
			  content: "weixin/TextPictureMatterController.do?goVideoSelectForm&allowCount=1&publicId="+publicId,
			  end:function(){
				  var selectImgItem = PlatUtil.getData("selectImgItem");
			     if(selectImgItem){
			    		PlatUtil.removeData("selectImgItem");
			    		for(j = 0; j < selectImgItem.length; j++) {
			    			var uuid = PlatUtil.getUUID();
			    			UE.getEditor('TEXTPICTURE_CONTENT').execCommand('insertHtml', '<iframe frameborder="0" class="res_iframe js_editor_audio audio_iframe" src="https://mp.weixin.qq.com/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=小刀会进行曲&amp;play_length=04:56" isaac2="1" low_size="517.1" source_size="517.1" high_size="2316.57" name="小刀会进行曲" play_length="296000" voice_encode_fileid="mU35X6htuBvUi6rNDXXA28U9Fhodyf0TLJgtwPh0-yI"></iframe>');
			    			//UE.getEditor('TEXTPICTURE_CONTENT').execCommand('insertHtml', '<p> <a href="'+selectImgItem[j].wxsrc+'">'+selectImgItem[j].vname+'</a></p>');
			    		} 
			      }
			  }
		 });
	}
	//点击图片触发事件
	function clickImg(event){
		var id=$(event).attr("id");
		$("input[name='PICTURE_ID']").val(id);
		var imgStyle = $(event).attr("style");
		if(imgStyle=="width:100%;height:auto;"){
			$("input[name='OLD_PICTURE_IS_AUTO']").val("1");
			$("input[name='PICTURE_IS_AUTO'][value='1']").prop('checked', 'checked');
		}else{
			$("input[name='OLD_PICTURE_IS_AUTO']").val("0");
			$("input[name='PICTURE_IS_AUTO'][value='0']").prop('checked', 'checked');
		}
		$("#imgDiv").show();
	}
	//图片应用保存
	function savePictureData(){
		var elId = $("input[name='PICTURE_ID']").val();
		var iframeId = $("#TEXTPICTURE_CONTENT").find("iframe").attr("id");
		var node = $("#"+iframeId).contents().find("#"+elId);
		var oldVal = $("input[name='OLD_PICTURE_IS_AUTO']").val();
		var newVal = $("input[name='PICTURE_IS_AUTO']:checked").val();
		if(oldVal==newVal){
			$("#imgDiv").hide();
		}else{
			if(newVal=="1"){
				node.attr("style","width:100%;height:auto;");
			}else if(newVal=="0"){
				node.attr("style","");
			}
			$("#imgDiv").hide();
		}
	}
	//从图库选择封面
	function selectCoverPhotoFromSystem(){
		 var publicId =  $("input[name='PUBLIC_ID']").val();
		 PlatUtil.openWindow({
			  title:"选择图片",
			  area: ["70%","70%"],
			  content: "weixin/TextPictureMatterController.do?goImgSelectForm&allowCount=1&publicId="+publicId,
			  end:function(){
				  var selectImgItem = PlatUtil.getData("selectImgItem");
			     if(selectImgItem){
			    		PlatUtil.removeData("selectImgItem");
			    		for(j = 0; j < selectImgItem.length; j++) {
			    			$("#COVER_PHOTO").attr("src",selectImgItem[j].wxsrc);
			    			$("input[name='TEXTPICTURE_COVER']").val(selectImgItem[j].imgsrc);
			    			$(".cpj-nowTp").find("img").attr("src",selectImgItem[j].wxsrc);
							$("#COVER_PHOTO_DIV").show();			    			
			    		} 
			      }
			  }
		 });
	}
	//从文字内容选择封面
	function selectCoverPhotoFromHtml(){
		var allSrc = "";
		var elId = $("input[name='PICTURE_ID']").val();
		var iframeId = $("#TEXTPICTURE_CONTENT").find("iframe").attr("id");
		$("#"+iframeId).contents().find("img").not("[wxsrc]").each(function(){
			allSrc += $(this).attr("src")+";";
		});
		if(allSrc!=""){
			PlatUtil.openWindow({
				  title:"选择图片",
				  area: ["70%","70%"],
				  content: "weixin/TextPictureMatterController.do?goImgEasySelectForm&allowCount=1&allSrc="+allSrc,
				  end:function(){
					  var selectImgItem = PlatUtil.getData("selectImgItem");
				     if(selectImgItem){
				    		PlatUtil.removeData("selectImgItem");
				    		for(j = 0; j < selectImgItem.length; j++) {
				    			$("#COVER_PHOTO").attr("src",selectImgItem[j].imgsrc);
				    			$("input[name='TEXTPICTURE_COVER']").val(selectImgItem[j].imgsrc);
				    			$(".cpj-nowTp").find("img").attr("src",selectImgItem[j].imgsrc);
								$("#COVER_PHOTO_DIV").show();			    			
				    		} 
				      }
				  }
			 });
		}else{
			parent.layer.alert("正文无图片！",{icon: 2,resize:false});
		}
		
	}
	//保存
	function saveData(){
		var iframeId = $("#TEXTPICTURE_CONTENT").find("iframe").attr("id");
		$("#"+iframeId).contents().find("img").not("[wxsrc]").each(function(){
			$(this).removeAttr("onclick");
		});
		var formData = PlatUtil.getFormEleData("baseform");
		var TEXTPICTURE_WXCONTENT = UE.getEditor('TEXTPICTURE_CONTENT').getContent();
		formData.TEXTPICTURE_WXCONTENT = TEXTPICTURE_WXCONTENT;
		try{
			var TEXTPICTURE_CONTENT = formData.TEXTPICTURE_CONTENT;
			UE.getEditor('TEXTPICTURE_CONTENT').setContent(TEXTPICTURE_CONTENT); 
		}catch (e) {
			
		}
		PlatUtil.ajaxProgress({
			url:"weixin/TextPictureMatterController.do?saveOrUpdate",
			async:"-1",
			showProgress:false,
			params :formData,
			callback : function(resultJson) {
				if (resultJson.success) {
					parent.layer.alert(PlatUtil.SUCCESS_MSG,{icon: 1,resize:false});
				} else {
					parent.layer.alert(PlatUtil.FAIL_MSG,{icon: 2,resize:false});
				}
				
			}
		});
	}
	//取消
	function closeWindow(){
	  PlatUtil.closeWindow();
	}
	
	
	//上移
	function upItem(event){
		var $item = $(event).closest(".cpj-tpItem"); 
		if ($item.index() > 1) { 
			$item.prev().before($item); 
		}else if ($item.index() == 1) { 
			$item.prev().find(".cpj-fibox").removeClass("cpj-fibox").addClass("cpj-fibox1");
			$item.find(".cpj-fibox1").removeClass("cpj-fibox1").addClass("cpj-fibox");
			$item.prev().find(".nocpj-tpsimg").removeClass("nocpj-tpsimg").addClass("cpj-tpsimg");
			$item.find(".cpj-tpsimg").removeClass("cpj-tpsimg").addClass("nocpj-tpsimg");
			$item.prev().before($item); 
		}
	}
	//下移
	function downItem(event){
		var itemLength = $(".cpj-tpItem").length; 
		var $item = $(event).closest(".cpj-tpItem"); 
		if ($item.index() == (itemLength-1)) { 
			parent.layer.alert("已经是最后一个，无法再往下移！",{icon: 2,resize:false});
		}else if ($item.index() ==0) {
			$item.find(".cpj-fibox").removeClass("cpj-fibox").addClass("cpj-fibox1");
			$item.next().find(".cpj-fibox1").removeClass("cpj-fibox1").addClass("cpj-fibox");
			$item.find(".nocpj-tpsimg").removeClass("nocpj-tpsimg").addClass("cpj-tpsimg");
			$item.next().find(".cpj-tpsimg").removeClass("cpj-tpsimg").addClass("nocpj-tpsimg");
			$item.next().after($item); 
		}else{
			$item.next().after($item); 
		}
	}
	//删除
	function delItem(event){
		var TEXTPICTURE_ID = $(event).closest(".cpj-tpItem").attr("id"); 
		parent.layer.confirm("您确定删除该素材吗?", {
		    resize :false
		}, function(){
			PlatUtil.ajaxProgress({
				url:"weixin/TextPictureMatterController.do?delItem",
				async:"-1",
				showProgress:false,
				params :{
					selectColValues:TEXTPICTURE_ID
			    },
				callback : function(resultJson) {
					if (resultJson.success) {
						if($(event).closest(".cpj-tpItem").hasClass("cpj-nowTp")){
							$("#"+TEXTPICTURE_ID).remove();
							if($("#titleDiv").find(".cpj-tpItem").length>0){
								  $($("#titleDiv").find(".cpj-tpItem").first().children("div").get(0)).trigger("click"); 
							  }
						}else{
							$("#"+TEXTPICTURE_ID).remove();
						}
						
					} else {
						parent.layer.alert(PlatUtil.FAIL_MSG,{icon: 2,resize:false});
					}
					
				}
			});
		});
	}
	
	//添加新的素材
	function addNewTextPicture(){
		 var publicId =  $("input[name='PUBLIC_ID']").val();
		PlatUtil.ajaxProgress({
			url:"weixin/TextPictureMatterController.do?addItem",
			async:"-1",
			showProgress:false,
			params :{
				publicId:publicId
		    },
			callback : function(resultHtml) {
				if(resultHtml&&resultHtml!=""){
					$("#titleDiv").append(resultHtml);
					$($("#titleDiv").find(".cpj-tpItem").last().children("div").get(0)).trigger("click");
				}
				
			}
			});
	}
</script>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.stooges.core.util.PlatPropUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String attachFileUrl = PlatPropUtil.getPropertyValue(
        "conf/config.properties", "attachFileUrl");
request.setAttribute("attachFileUrl", attachFileUrl);
%>

<!DOCTYPE html>
<html>
  <head>
    <base href="<%=basePath%>">
    <title>图片配置</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <plattag:resources restype="css" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid"></plattag:resources>
    <plattag:resources restype="js" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid"></plattag:resources>
  	<style type="text/css">
  		ul, ol {
		    list-style-type: none;
		    padding-left: 0;
		}
  		.img_pick {
		    padding: 20px;
		    text-align: center;
		}
		.img_pick .img_item {
		    margin-bottom: 10px;
		    margin-right: 11px;
		    position: relative;
		    
		}
		.img_pick .img_item {
		    float: left;
		    text-align: center;
		}
		.img_pick .img_item .pic_box {
		    height: 117px;
		    overflow: hidden;
		    position: relative;
		    width: 117px;
		}
		.img_pick .img_item .pic {
		    left: 50%;
		    position: absolute;
		    top: 50%;
		    transform: translate(-50%, -50%);
		}
		.img_pick .img_item .cover {
		    background-position: center center;
		    background-repeat: no-repeat;
		    background-size: cover;
		    display: block;
		}
		.img_pick .img_item .lbl_content {
		    display: block;
		    padding: 0 9px;
		}
		.img_pick .img_item .lbl_content .icon_original {
		    display: inline-block;
		    height: 18px;
		    vertical-align: -4px;
		    width: 34px;
		}
		.img_pick .img_item_bd {
		    border: 1px solid #e7e7eb;
		    margin: 0;
		    width: 117px;
		}
		.img_pick .img_item_bd.selected .selected_mask {
		    height: 100%;
		    left: 0;
		    position: absolute;
		    top: 0;
		    width: 100%;
		}
		.img_pick .img_item_bd.selected .selected_mask_inner {
		    background-color: #000;
		    height: 118px;
		    opacity: 0.6;
		    width: 118px;
		}
		.img_pick .img_item_bd.selected .selected_mask_icon {
		    background: transparent url("webpages/background/weixin/images/icon_card_selected3a7b38.png") no-repeat scroll 50% 50%;
		    display: inline-block;
		    height: 117px;
		    left: 0;
		    position: absolute;
		    top: 0;
		    vertical-align: middle;
		    width: 117px;
		}
  	
  	</style>
  </head>
  
  <body>
    <script type="text/javascript">
	$(function(){
		  PlatUtil.initUIComp();
	});
	</script>
    <div class="plat-directlayout" style="height:100%" platundragable="true" compcode="direct_layout">
   <div class="ui-layout-center" platundragable="true" compcode="direct_layout" style="overflow-y:auto;">
   		<input type="hidden" name="allowCount" value="${allowCount}">
   		<div class="img_pick">
	   		<ul class="group js_list img_list">
	   			<c:forEach  items="${imgList}" var="imgItem">
	   			<li class="img_item js_imageitem" onclick="selectItem(this);">
	   				<label class="frm_checkbox_label img_item_bd"  data-wxsrc="${imgItem.MEDIAMATTER_MEDIAURL}" data-imgsrc="${attachFileUrl}${imgItem.FILE_PATH}">
	   					<div class="pic_box">
	   						<img class="pic js_pic" src="${attachFileUrl}${imgItem.FILE_PATH}" style="height: 117px;">
	   					</div>
	   					<span class="lbl_content"> ${imgItem.MEDIAMATTER_NAME} </span>
	   					<div class="selected_mask">
							<div class="selected_mask_inner"></div>
							<div class="selected_mask_icon"></div>
						</div>
	   				</label>
	   			</li>
	   			</c:forEach>
	   		</ul>
   		</div>
   
   </div>
   <div class="ui-layout-south" platundragable="true" compcode="direct_layout">
   <div class="row" style="height:40px; margin-top: -6px;padding-top: 4px; background: #e5e5e5;" platundragable="true" compcode="buttontoolbar">
     <div class="col-sm-12 text-right">
     	已选<span class="js_selected">0</span>个，可选${fn:length(imgList)}个</span>
		<button type="button" onclick="submitBusForm();" platreskey="" class="btn btn-outline btn-primary btn-sm">
			<i class="fa fa-check"></i>&nbsp;提交
		</button>
		<button type="button" onclick="closeWindow();" platreskey="" class="btn btn-outline btn-danger btn-sm">
			<i class="fa fa-times"></i>&nbsp;关闭
		</button>
     </div>
</div>

<script type="text/javascript">
function submitBusForm(){
	var allowCount = $("input[name='allowCount']").val();
	var len = $(" .frm_checkbox_label.selected").length;
	if(len==0){
		parent.layer.alert("请至少选择一项！",{icon: 2,resize:false});
	}else{
			if((len>allowCount)&&allowCount!=0){
				parent.layer.alert("最多只能选择"+allowCount+"条记录!",{icon: 2,resize:false});
				return;
			}else{
			var srcArr = [];
			$(" .frm_checkbox_label.selected").each(function(){
				var srcItem = {};
				var wxsrc = $(this).attr("data-wxsrc");
				var imgsrc = $(this).attr("data-imgsrc");
				srcItem.wxsrc = wxsrc;
				srcItem.imgsrc = imgsrc;
				srcArr.push(srcItem);
			});
			PlatUtil.setData("selectImgItem",srcArr);
			PlatUtil.closeWindow();
		} 
	}
}
function closeWindow(){
  PlatUtil.closeWindow();
}

function selectItem(event){
	$(event).find(".frm_checkbox_label").toggleClass("selected");
	$(".js_selected").html($(" .frm_checkbox_label.selected").length);
}
</script>

</div>
</div>
  </body>
</html>

<script type="text/javascript">

</script>

<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>

<%
    String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
%>

<!DOCTYPE html>
<html>
<head>
<base href="<%=basePath%>">
<title>测试材料列表</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<plattag:resources restype="css"
	loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid"></plattag:resources>
<plattag:resources restype="js"
	loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid"></plattag:resources>
<link rel="stylesheet" href="plug-in/layui.layim-3.6.0/css/layui.css"
	media="all">
<script src="plug-in/layui.layim-3.6.0/layui.js"></script>
<script src="plug-in/laypage-1.3.0/laypage.js"></script>
<style type="text/css">
.user-image {
    border-radius: 50%;
    height: 50px;
    width: 50px;
}
.card-box {
	width: 237px;
}
.card-box-img {
	cursor: pointer;
	background-color: #fff !important;
	border-right: 0px solid #ccc;
}
.card-box-content {
	width: 175px;
}
.card-box-content p {
	width: 170px;
}
.card-box-content p span{
	float: left;
}
</style>
</head>

<body>
	<script type="text/javascript">
		$(function() {
			PlatUtil.initUIComp();
		});
	</script>
	<div class="plat-directlayout" style="height:100%"
							platundragable="true" compcode="direct_layout">
							<div class="ui-layout-center" platundragable="true"
								compcode="direct_layout">
								<c:if test="${type=='edit'}">
								<div class="card-search" style="text-align: right;">
										<c:if test="${IS_ADMIN!=true}">
											<button class="btn btn-outline btn-danger btn-sm" type="button" onclick="removeGroup('${id}');" >
												<i class="fa fa-trash"></i>
												 退出群
											</button>
										</c:if>
										<button class="btn btn-outline btn-info btn-sm" type="button" onclick="invitationGroup('${id}');" >
												<i class="fa fa-users"></i>
												 邀请好友入群
										</button>
										<c:if test="${IS_ADMIN==true}">
											<button class="btn btn-outline btn-warning btn-sm" type="button" onclick="editGroupInfo('${id}');" >
												<i class="fa fa-pencil"></i>
												 编辑群信息
											</button>
											<button class="btn btn-outline btn-primary btn-sm" type="button" onclick="assignmentGroup('${id}');" >
												<i class="fa fa-universal-access"></i>
												 群转让
											</button>
											<button class="btn btn-outline btn-danger btn-sm" type="button" onclick="removeAllGroup('${id}');" >
												<i class="fa fa-trash"></i>
												 解散群
											</button>
										</c:if>
								</div>	
								</c:if>
								<div class="plat-form-title" >
									<span class="plat-current">群主</span>
								</div>
								<div>
									<div class="card-box">
										<div class="card-box-img" >
											<img class="user-image" src="${map.createMap.avatar}">
										</div>
										<div class="card-box-content">
											<p title="${map.createMap.username}">${map.createMap.username}</p>
											<p title="${map.createMap.sign}" style="font-size: 11px;min-height: 16px;">${map.createMap.sign}</p>
										</div>
									</div>
								</div>
								<div  class="plat-form-title" style="clear: both;">
									<span class="plat-current">群成员</span>
								</div>
								<div id="qcydiv">
									<c:forEach var="qxy" items="${map.list}">
										<div class="card-box" id="${qxy.id}">
											<div class="card-box-img" >
												<img class="user-image" src="${qxy.avatar}">
											</div>
											<div class="card-box-content">
												<p title="${qxy.username}">${qxy.username}</p>
												<p title="${qxy.sign}" style="font-size: 11px;min-height: 16px;">${qxy.sign}
												</p>
												<p style="text-align: right;padding: 2px;">
													<c:if test="${IS_ADMIN==true}">
													<button class="btn btn-outline btn-danger btn-xs" onclick="getOutGroup('${id}','${qxy.id}')" type="button"><i class="fa fa-plus"></i>踢出群</button>
													</c:if>
												</p>
												
											</div>
										</div>
									
									</c:forEach>
								</div>
							</div>
						</div>
</body>
</html>
<script type="text/javascript">
function removeGroup(id){
	PlatUtil.ajaxProgress({
		   url:"chatonline/ChatOnlineController.do?removeGroup",
		   params:{
			   id:id
		   },
		   callback:function(resultJson){
			   if (resultJson.success) {
				    PlatUtil.setData("submitSuccess",true);
					parent.layer.msg(resultJson.msg, {icon: 1});
					PlatUtil.closeWindow();
				} else {
					parent.layer.alert(resultJson.msg,{icon: 2,resize:false});
				}
		   }
		});
}
function getOutGroup(id,userid){
	PlatUtil.ajaxProgress({
		   url:"chatonline/ChatOnlineController.do?getOutGroup",
		   params:{
			   id:id,
			   userid:userid
		   },
		   callback:function(resultJson){
			   if (resultJson.success) {
				   	PlatUtil.setData("submitSuccess",true);
					parent.layer.msg(resultJson.msg, {icon: 1});
					personId.removeByValue(userid);
					$("#"+userid).remove();
				} else {
					parent.layer.alert(resultJson.msg,{icon: 2,resize:false});
				}
		   }
		});
}
function removeAllGroup(id){
	PlatUtil.ajaxProgress({
		   url:"chatonline/ChatOnlineController.do?removeAllGroup",
		   params:{
			   id:id
		   },
		   callback:function(resultJson){
			   if (resultJson.success) {
				    PlatUtil.setData("submitSuccess",true);
					parent.layer.msg(resultJson.msg, {icon: 1});
					PlatUtil.closeWindow();
				} else {
					parent.layer.alert(resultJson.msg,{icon: 2,resize:false});
				}
		   }
		});
}
Array.prototype.removeByValue = function(val) {
	  for(var i=0; i<this.length; i++) {
	    if(this[i] == val) {
	      this.splice(i, 1);
	      break;
	    }
	  }
}
var personId = "${personId}".split(",");
function invitationGroup(id){
    PlatUtil.setData(PlatUtil.WIN_SELECTOR_CONFIG,{
    	//
    	personId:personId,
		//0标识不控制选择的条数
		maxselect:100,
		//最少需要选择的条数
		minselect:1
		//选中时的级联方式
	});
	PlatUtil.openWindow({
		title:"邀请用户",
		area: ["90%","92%"],
		content: "appmodel/DesignController.do?goGenUiView&DESIGN_CODE=invitationGroup",
		end:function(){
          var selectedRecords = PlatUtil.getData(PlatUtil.WIN_SELECTOR_RECORDS);
          if(selectedRecords&&selectedRecords.selectSuccess){
                var checkUserIds = selectedRecords.checkIds;
				PlatUtil.removeData(PlatUtil.WIN_SELECTOR_RECORDS);
                PlatUtil.ajaxProgress({
                    url:"chatonline/ChatOnlineController.do?invitationGroup",
                    params : {
                       id:id,
                       checkUserIds:checkUserIds
                    },
                    callback : function(resultJson) {
                        if (resultJson.success) {
                            parent.layer.msg(PlatUtil.SUCCESS_MSG, {icon: 1});
                            var list = resultJson.list;
                            for(var i=0;i<list.length;i++){
                            	var appendHtml = '';
                            	appendHtml +='<div class="card-box" id="'+list[i].id+'">';
                            	appendHtml +='<div class="card-box-img" ><img class="user-image" src="'+list[i].avatar+'"></div>';
                            	appendHtml +='<div class="card-box-content">';
                            	appendHtml +='<p title="'+list[i].username+'">'+list[i].username+'</p>';
                            	if(list[i].sign==null){
                            		appendHtml +='<p title="" style="font-size: 11px;min-height: 16px;"></p>';
                            	}else{
                            		appendHtml +='<p title="'+list[i].sign+'" style="font-size: 11px;">'+list[i].sign+'</p>';
                            	}
                            	appendHtml +='<p style="text-align: right;padding: 2px;">';
								if("${IS_ADMIN}"=="true"){
									appendHtml +='<button class="btn btn-outline btn-danger btn-xs" onclick="getOutGroup(\''+id+'\',\''+list[i].id+'\')" type="button"><i class="fa fa-plus"></i>踢出群</button>';
								}
								appendHtml +='</p>';
								appendHtml +='</div>';
								appendHtml +='</div>';
								$("#qcydiv").append(appendHtml);
								personId.push(list[i].id);
                            }
                        } else {
                            parent.layer.alert(PlatUtil.FAIL_MSG,{icon: 2,resize:false});
                        }
                    }
                });
                
		  }
		}
	});
}

function assignmentGroup(id){
	PlatUtil.setData(PlatUtil.WIN_SELECTOR_CONFIG,{
    	//
    	id:id
	});
	PlatUtil.openWindow({
		title:"群转让",
		area: ["90%","92%"],
		content: "appmodel/DesignController.do?goGenUiView&DESIGN_CODE=selectuser",
		end:function(){
			if(PlatUtil.isSubmitSuccess()){
				PlatUtil.setData("submitSuccess",true);
				PlatUtil.closeWindow();
			  }
		}
	});
	
}

function editGroupInfo(id){
	var url = "chatonline/UserGroupController.do?goForm&UI_DESIGNCODE=GROUPFORM&USERGROUP_ID="+id;
	var title = "编辑群信息";
	PlatUtil.openWindow({
	  title:title,
	  area: ["1000px","500px"],
	  content: url,
	  end:function(){
		  
	  }
	});
}
</script>


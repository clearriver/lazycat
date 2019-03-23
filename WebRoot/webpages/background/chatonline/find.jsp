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
			<div class="tabs-container" platundragable="true"
				compcode="bootstraptab" id="GROUPTAB"
				style="width:100%;height:100%;">
				<ul class="nav nav-tabs">
					<li class="active" subtabid="TAB1"
						onclick="PlatUtil.onBootstrapTabClick('GROUPTAB','TAB1','','1');"><a
						data-toggle="tab" href="#TAB1" aria-expanded="true">群查找</a></li>
					<li class="" subtabid="TAB2"
						onclick="PlatUtil.onBootstrapTabClick('GROUPTAB','TAB2','showMyGroup','1');"><a
						data-toggle="tab" href="#TAB2" aria-expanded="false">我的群</a></li>
				</ul>
				<div class="tab-content" platundragable="true"
					compcode="bootstraptab" style="height: calc(100% - 42px);">
					<div id="TAB1" class="tab-pane active" style="height:100%;"
						platundragable="true" compcode="bootstraptab">
						<div class="plat-directlayout" style="height:100%"
							platundragable="true" compcode="direct_layout">
							<div class="ui-layout-center" platundragable="true"
								compcode="direct_layout">
								<div class="card-search">
									<div class="input-group">
										<input class="form-control" name="selectedUserGridsearchItem" maxlength="100" placeholder="请输入群名称" type="text">
										<span class="input-group-btn">
											<button class="btn btn-primary" type="button" onclick="searchGroup();">搜索</button>
										</span>
									</div>
								</div>	
								<div id="searchGroupDiv">
									
								</div>
								
							</div>
						</div>
					</div>
					<div id="TAB2" class="tab-pane " style="height:100%;"
						platundragable="true" compcode="bootstraptab">
						<div class="plat-directlayout" style="height:100%"
							platundragable="true" compcode="direct_layout">
							<div class="ui-layout-center" platundragable="true"
								compcode="direct_layout">
								<div class="card-search" style="text-align: right;">
									<button class="btn btn-outline btn-info btn-sm" type="button" onclick="addNewGroup();" >
										<i class="fa fa-plus"></i>
										 新建群
									</button>
								</div>	
								<div id="myGroupDiv">
									
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			<script type="text/javascript">
				function searchGroup(){
					var val =  $("input[name='selectedUserGridsearchItem']").val();
					if(val==""){
						
					}else{
						PlatUtil.ajaxProgress({
							   url:"chatonline/ChatOnlineController.do?showSearchGroupResult",
							   params:{
								   "val":val
							   },
							   callback:function(resultJson){
								   $("#searchGroupDiv").html("");
									var appendHtml = '';
									for(var i=0;i<resultJson.length;i++){
										appendHtml +='<div class="card-box">';
										appendHtml +='<div class="card-box-img" title="查看群成员" onclick="showGroupPerson(\''+resultJson[i].USERGROUP_ID+'\')">';
										appendHtml +='<img class="user-image" src="'+resultJson[i].USERGROUP_IMG+'" >';
										appendHtml +='</div><div class="card-box-content"><p title="'+resultJson[i].USERGROUP_NAME+'">'+resultJson[i].USERGROUP_NAME+'</p>';
										appendHtml +='<p style="text-align: right;padding: 2px;"><span><i class="fa fa-users" aria-hidden="true"></i>'+resultJson[i].USERGROUP_NUM+'</span>';
										appendHtml +='<button class="btn btn-outline btn-info btn-xs" onclick="applyAddGroup(\''+resultJson[i].USERGROUP_ID+'\')" type="button"><i class="fa fa-plus"></i>加入群聊</button>';
										appendHtml +='</p>';
										appendHtml +='</div>';
										appendHtml +='</div>';
										
									}
									$("#searchGroupDiv").html(appendHtml);
							   }
							});
					}
				}
				function showMyGroup(subTabId) {
					reloadMyGroup();
				}
				function reloadMyGroup(){
					PlatUtil.ajaxProgress({
						   url:"chatonline/ChatOnlineController.do?showMyGroupResult",
						   params:{
							   
						   },
						   callback:function(resultJson){
							   $("#myGroupDiv").html("");
								var appendHtml = '';
								for(var i=0;i<resultJson.length;i++){
									appendHtml +='<div class="card-box" title="管理群成员" onclick="editGroupPerson(\''+resultJson[i].USERGROUP_ID+'\')">';
									appendHtml +='<div class="card-box-img" >';
									appendHtml +='<img class="user-image" src="'+resultJson[i].USERGROUP_IMG+'" >';
									appendHtml +='</div><div class="card-box-content"><p title="'+resultJson[i].USERGROUP_NAME+'">'+resultJson[i].USERGROUP_NAME+'</p>';
									appendHtml +='<p style="text-align: right;padding: 2px;"><span><i class="fa fa-users" aria-hidden="true"></i>'+resultJson[i].USERGROUP_NUM+'</span>';
									appendHtml +='</p>';
									appendHtml +='</div>';
									appendHtml +='</div>';
									
								}
								$("#myGroupDiv").html(appendHtml);
						   }
						});
				}
				
				function addNewGroup(){
					var url = "chatonline/UserGroupController.do?goForm&UI_DESIGNCODE=GROUPFORM";
					var title = "新增群";
					PlatUtil.openWindow({
					  title:title,
					  area: ["1000px","500px"],
					  content: url,
					  end:function(){
						  if(PlatUtil.isSubmitSuccess()){
							  reloadMyGroup();
						  }
					  }
					});
				}
				
				function applyAddGroup(id){
					PlatUtil.ajaxProgress({
						   url:"chatonline/ChatOnlineController.do?applyAddGroup",
						   params:{
							   id:id
						   },
						   callback:function(resultJson){
							   if (resultJson.success) {
									parent.layer.msg(resultJson.msg, {icon: 1});
								} else {
									parent.layer.alert(resultJson.msg,{icon: 2,resize:false});
								}
						   }
						});
				}
				
				function showGroupPerson(id){
					openGroupPerson(id,"read");
				}
				
				function openGroupPerson(id,type){
					var url = "chatonline/UserGroupController.do?showUserGroupPerson&id="+id+"&type="+type;
					var title = "群成员";
					PlatUtil.openWindow({
					  title:title,
					  area: ["1000px","600px"],
					  content: url,
					  end:function(){
						  if(PlatUtil.isSubmitSuccess()){
							  reloadMyGroup();
						  }
					  }
					});
				}
				function editGroupPerson(id){
					openGroupPerson(id,"edit");
				}
			</script>
		</div>
	</div>
</body>
</html>


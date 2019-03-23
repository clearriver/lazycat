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
    <title>测试材料列表</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <plattag:resources restype="css" loadres="jquery-ui"></plattag:resources>
    <plattag:resources restype="js" loadres="jquery-ui"></plattag:resources>
    <link rel="stylesheet"  href="plug-in/layui.layim-3.6.0/css/layui.css" media="all">
  	<script src="plug-in/layui.layim-3.6.0/layui.js" ></script>
  	<script src="plug-in/laypage-1.3.0/laypage.js"></script>
  	<style>
	body .layim-chat-main{height: auto;}
	.layim-chat-main{min-height:calc(100vh - 50px); }
	</style>
  </head>
  
  <body>
	<div class="card-search">
		<div class="input-group">
			<input class="form-control" name="queryParam" maxlength="100" placeholder="请输入查询内容" autocomplete="off" type="text">
			<span class="input-group-btn">
				<button class="btn btn-primary" type="button" onclick="newjumpPage();">搜索</button>
			</span>
		</div>
	</div>
	<div class="layim-chat-main">
	 	 <ul id="LAY_view">
	 	 
	 	 	
	 	 </ul>
	</div>

	<div id="LAY_page" style="margin: 0 10px;"></div>
  </body>
</html>

<script>

layui.use(['layim'], function(){
	$(function(){
		jumpPage();
	});
	//alert(layui.layim.content(i));
	function jumpPage(curr){
		var rows = 10;
		var queryParam = $("input[name='queryParam']").val();
		PlatUtil.ajaxProgress({
			   url:"chatonline/ChatOnlineController.do?getChatLog",
			   params:{
				   "page":curr || 1,
				   "rows":rows,
				   "id":"${id}",
				   "type":"${type}",
				   "queryParam":queryParam
			   },
			   callback:function(resultJson){
				 	  //调用分页
					  laypage({
					    cont: $('#LAY_page'),
					    skip: true, //是否开启跳页
					    pages: resultJson.total,
					    skin: '#009688',
					    curr: curr || 1,
					    groups: 5, //连续显示分页数
					    jump: function(obj,first){
					    	 $("#LAY_page").find(".laypage_skip").val(obj.curr);
					    	 $("#LAY_page").find(".laypage_skip").attr("max",resultJson.total);
					    	if(!first){
					    		jumpPage(obj.curr);
					         }
					    }
					  });
				 	showData(resultJson);
			   }
			});
	}
	function showData(resultJson){
		var itemList = resultJson.rows;
		$("#LAY_view").html("");
		var appendHtml = '';
		for(var i=0;i<itemList.length;i++){
			if(itemList[i].ISMINE){
				appendHtml+='<li class="layim-chat-mine"><div class="layim-chat-user"><img src="'+itemList[i].avatar+'"><cite><i>'+itemList[i].NEWS_CREATETIME+'</i>'+itemList[i].username+'</cite></div><div class="layim-chat-text">'+layui.layim.content(itemList[i].NEWS_INFO)+'</div></li>'
			}else{
				appendHtml+='<li><div class="layim-chat-user"><img src="'+itemList[i].avatar+'"><cite><i>'+itemList[i].NEWS_CREATETIME+'</i>'+itemList[i].username+'</cite></div><div class="layim-chat-text">'+layui.layim.content(itemList[i].NEWS_INFO)+'</div></li>';
			}
			
		}
		$("#LAY_view").html(appendHtml);
	}
	window.newjumpPage = function(){
		jumpPage();
	}
});

</script>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.stooges.core.util.PlatPropUtil"%>
<%@ page language="java" import="com.stooges.platform.system.service.GlobalConfigService"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page language="java" import="com.stooges.core.util.PlatAppUtil"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="plattag" uri="/plattag"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String webSocketPath = PlatAppUtil.getWebSocketServerUrl(request);
request.setAttribute("webSocketPath", webSocketPath);
GlobalConfigService globalConfigService = (GlobalConfigService)PlatAppUtil.getBean("globalConfigService");
Map<String,Object> globalConfig = globalConfigService.getFirstConfigMap();
String chatonline = (String) globalConfig.get("CONFIG_CHATABLE");
if(StringUtils.isNotEmpty(chatonline)&&chatonline.equals("1")){
    request.setAttribute("showChatOnlineWindow", "true");
}else{
    request.setAttribute("showChatOnlineWindow", "false");
}
%>
  
  	 <c:if test="${showChatOnlineWindow=='true' }">
  	 <link rel="stylesheet"  href="plug-in/layui.layim-3.6.0/css/layui.css" media="all">
  	<script src="plug-in/layui.layim-3.6.0/layui.js" ></script>
   <script>
   layui.use('layim', function(layim){
	   var onlineUserId= "${sessionScope.__BACKPLATUSER.SYSUSER_ID}";
	   var websocket = PlatUtil.initWebSocket({
	    	  //指定websocket服务器
	    	  url:"${webSocketPath}",
	    	  //指定客户端ID
	    	  clientId:onlineUserId,
	    	  onmessage:function(msgContent){
	    		 var result = JSON.parse(msgContent);
	  			var msgType = result.msgType;
	  			if(msgType=="online"){
	  				var onlineUserId = result.onlineUserId;
	  				layim.setFriendStatus(onlineUserId, 'online');
	  			}else if(msgType=="offline"){
	  				var onlineUserId = result.onlineUserId;
	  				layim.setFriendStatus(onlineUserId, 'offline');
	  			}else if(msgType=="chatMessage"){
	  				var newMessage = result.newMessage;
	  				 layim.getMessage(newMessage);
	  			}else if(msgType=="applyGroup"){
	  				var newMessage = result.newMessage;
	  				 layim.addList(newMessage);
	  			}else if(msgType=="removeGroup"){
	  				var newMessage = result.newMessage;
	  				 layim.removeList(newMessage);
	  			}else if(msgType=="saveupdateGroup"){
	  				var newMessage = result.newMessage;
	  				layim.removeList(newMessage);
	  				layim.addList(newMessage);
	  			}else if(msgType=="addFriend"){
	  				var newMessage = result.newMessage;
	  				 layim.addList(newMessage);
	  			}else if(msgType=="removeFriend"){
	  				var newMessage = result.newMessage;
	  				 layim.removeList(newMessage);
	  			}
	    	  },
	    	  onopen:function(){
	    		 var msgContent = {msgType: "online",clientId:onlineUserId};
	   		     websocket.send("{msgContent:'"+JSON.stringify(msgContent)+"',invokeJavaInter:'chatOnlineService.sendWebSocketMsg'}");
	    	  },
	    	  onbeforeunload:function(){
	    		  var msgContent = {msgType: "offline",clientId:onlineUserId};
		   		  websocket.send("{msgContent:'"+JSON.stringify(msgContent)+"',invokeJavaInter:'chatOnlineService.sendWebSocketMsg'}");
	    	  },
	    	  onclose:function(){
	    		  //alert("调用了关闭方法...");
	    	  }
	      });
	   //基础配置
	   layim.config({
	    init: {
	        url: 'chatonline/ChatOnlineController.do?initList'
	        	,type: 'post' //默认get，一般可不填
	            ,data: {}
	          }
	   //获取群员接口（返回的数据格式见下文）
	    ,members: {
	      url: 'chatonline/ChatOnlineController.do?getMembers' //接口地址（返回的数据格式见下文）
	      ,type: 'post' //默认get，一般可不填
	      ,data: {} //额外参数
	    }
	    ,uploadImage: {
	    	  url: 'chatonline/ChatOnlineController.do?uploadImg'
	    } 
	    ,uploadFile: {
	    	  url: 'chatonline/ChatOnlineController.do?uploadImg'
	    } 
	     ,brief: false //是否简约模式（默认false，如果只用到在线客服，且不想显示主面板，可以设置 true）
	     ,title: '即时通讯' //主面板最小化后显示的名称
	     ,min: true //用于设定主面板是否在页面打开时，始终最小化展现。默认false，即记录上次展开状态。
	     ,minRight: null //【默认不开启】用户控制聊天面板最小化时、及新消息提示层的相对right的px坐标，如：minRight: '200px'
	     ,maxLength: 3000 //最长发送的字符长度，默认3000
	     ,isfriend: true //是否开启好友（默认true，即开启）
	     ,isgroup: true //是否开启群组（默认true，即开启）
	     ,isAudio:true
	     ,isVideo:true
	     ,notice:true
	     ,right: '150px' //默认0px，用于设定主面板右偏移量。该参数可避免遮盖你页面右下角已经的bar。
	     ,chatLog: 'chatonline/ChatOnlineController/showChatLog.do' //聊天记录地址（如果未填则不显示）
	     ,copyright: true //是否授权，如果通过官网捐赠获得LayIM，此处可填true
	     //,msgbox: layui.cache.dir + 'css/modules/layim/html/msgbox.html' //消息盒子页面地址，若不开启，剔除该项即可
	     ,find: 'chatonline/ChatOnlineController/find.do' //发现页面地址，若不开启，剔除该项即可
	     //,about:layui.cache.dir + 'css/modules/layim/html/about.html'
	   });
	   layim.on('ready', function(options){
		   PlatUtil.ajaxProgress({
			   url:"chatonline/ChatOnlineController.do?getNoReadMsg",
			   showProgress:"-1",
			   params:{
				   "SYSUSER_ID":onlineUserId
			   },
			   callback:function(resultJson){
					
			   }
			});
		 });
	   layim.on('sign', function(value){
		   PlatUtil.ajaxProgress({
			   url:"chatonline/ChatOnlineController.do?updateSign",
			   showProgress:"-1",
			   params:{
				   "SYSUSER_SIGN":value,
				   "SYSUSER_ID":onlineUserId
			   },
			   callback:function(resultJson){
					
			   }
			});
		   
		 });
	   layim.on('sendMessage', function(res){
		   var mine = res.mine; //包含我发送的消息及我的信息
		   var to = res.to; //对方的信息
		   var msgContent = {msgType: "chatMessage",data:res};
		   websocket.send("{msgContent:'"+JSON.stringify(msgContent)+"',invokeJavaInter:'chatOnlineService.sendWebSocketMsg'}");
		 });
	   
	 });  
</script>
</c:if>




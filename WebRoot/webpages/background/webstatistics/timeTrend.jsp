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
    <title>臭皮匠软件快速开发平台 开源流程引擎 JBPM|Activiti 可视化流程设计器 智能表单 JAVA开发平台</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <plattag:resources restype="css" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid"></plattag:resources>
    <plattag:resources restype="js" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid,echart"></plattag:resources>
  </head>
  
  <body>
  	<script type="text/javascript">
	$(function(){
		  PlatUtil.initUIComp();
	});
	</script>
  	 <div class="plat-directlayout" style="height:100%" platundragable="true" compcode="direct_layout">
   		<div class="ui-layout-center" platundragable="true" compcode="direct_layout" style="height:100%">
  			<div class="panel-Title">
				<h5 id="addBusiness_datatablepaneltitle">时刻趋势图</h5>
			</div>
			<div style="padding: 5px;" id="reviewItem_search" >
				<form action="javascript:void(0);"  class="form-horizontal">
					<div class="form-group">
					    <plattag:select name="siteId" allowblank="true" 
				        auth_type="write" value="${siteId}" 
				        istree="false" onlyselectleaf="false" label_value="统计站点" 
				        placeholder="请选择统计站点" comp_col_num="2" label_col_num="1" 
				        dyna_interface="siteInfoService.findForSelect" dyna_param="无">
						</plattag:select>
						<div class="col-sm-2" >
							<input class="wicon form-control" name="searchDay" value="${curDate}" max_date="${curDate}" istime="false" format="YYYY-MM-DD" auth_type="" readonly="readonly" posttimefmt="" placeholder=""   type="text">
						</div>
						<!-- <div class="col-sm-1" >
							<div class="checkbox checkbox-success checkbox-inline">
									<input  name="contrast" value="1" id="contrast_1"
										label="对比" type="checkbox"> <label for="contrast_1">对比</label>
							</div>
						</div> -->
						<div class="col-sm-2">
							<a onclick="searchTime();" class="btn btn-primary" href="javascript:void(0);"> 查 询</a>
						</div>
					</div>
					<div class="hr-line-dashed"></div>
				</form>
			</div>
			<!-- 为ECharts准备一个具备大小（宽高）的Dom -->
    		<div id="main" style="width: 100%;height: calc(100% - 82px);"></div>
    	</div>
    </div>
    <script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));

        // 指定图表的配置项和数据
        var option = {
        	    title: {
        	      
        	    },
        	    tooltip: {
        	        trigger: 'axis'
        	    },
        	    legend: {
        	    	show: true,
        	    	data:['浏览次数(PV)','独立访客(UV)','IP']
        	    },
        	    toolbox: {
        	        show: false
        	    },
        	    xAxis:  {
        	        type: 'category',
        	        boundaryGap: false,
        	        data: ['']
        	    },
        	    yAxis: {
        	        type: 'value'
        	    },
        	    series: [
        	    ]
        	};
		
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option); 
        
        
        function searchTime(){
        	var searchDay = $("input[name='searchDay']").val();
        	var siteId = PlatUtil.getSelectAttValue("siteId","value");
        	if(searchDay!=""&&siteId!=""){
        		PlatUtil.ajaxProgress({
        			url : "webstatistics/DayTrendController.do?getTimeTrendData",
        			params :{"searchDay":searchDay,"siteId":siteId},
        			callback : function(resultJson) {
        				myChart.setOption({
        	            	  xAxis : [
        	                           {
        	                               type : 'category',
        	                              data:resultJson.title
        	                           }
        	                    ],
        		              series: [{
        		                  name: '浏览次数(PV)',
        		                  type: 'line',
        		                  data: resultJson.pvData,
        		                  itemStyle : { normal: {label : {show: true,textStyle:{color:'#333333'}}}}
        		              },{
        		                  name: '独立访客(UV)',
        		                  type: 'line',
        		                  data: resultJson.uvData,
        		                  itemStyle : { normal: {label : {show: true,textStyle:{color:'#333333'}}}}
        		              },{
        		                  name: 'IP',
        		                  type: 'line',
        		                  data: resultJson.ipData,
        		                  itemStyle : { normal: {label : {show: true,textStyle:{color:'#333333'}}}}
        		              }]
        	              });
        			}
        		});
        	}else{
        		if(searchDay==""){
        			layer.alert("请选择查看日期!",{icon: 2,resize:false});
        		}else if(siteId==""){
        			layer.alert("请选择统计站点!",{icon: 2,resize:false});
        		}
        	}
        	
        }
        
        $(function(){
        	searchTime();
        })
    </script>
    
  </body>
</html>



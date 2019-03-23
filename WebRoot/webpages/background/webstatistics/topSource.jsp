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
				<h5 id="addBusiness_datatablepaneltitle">TOP10来源</h5>
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
						<div class="col-sm-4" >
							从
							<input class="wicon form-control" name="startvalue" istime="false" format="YYYY-MM-DD" value="${preDate}" max_date="${curDate}" auth_type="" readonly="readonly" posttimefmt="" placeholder="" style="width: 45%;"  end_name="endvalue" type="text">
							至
							<input class="wicon form-control" name="endvalue" istime="false" format="YYYY-MM-DD" value="${curDate}" max_date="${curDate}" auth_type="" readonly="readonly" posttimefmt="" placeholder="" style="width: 45%;"  start_name="startvalue" type="text">
							
						</div>
						<div class="col-sm-2" >
							<select class="form-control select2" name="type" istree="false" auth_type="write" placeholder="请选择查看类型"  tabindex="-1" aria-hidden="true">
								<option value="1" >浏览次数(PV)</option>
								<option value="2" >独立访客(UV)</option>
								<option value="3" >IP</option>
							</select>
						</div>
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
        	        trigger: 'axis',
        	        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
        	            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        	        }
        	    },
        	    legend: {
        	    	show: true
        	    },
        	    toolbox: {
        	        show: false
        	    },
        	    xAxis:  {
        	        type: 'category',
        	        axisLabel:{
        	        	interval:0,
        	        	rotate:10
        	        },
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
        	var startDay = $("input[name='startvalue']").val();
        	var endDay = $("input[name='endvalue']").val();
        	var type = $("select[name='type']").val();
        	var siteId = PlatUtil.getSelectAttValue("siteId","value");
        	if(type==""||type==null){
        		PlatUtil.changeSelect2Val("select[name='type']","1");
        		type="1";
        	}
        	if(startDay!=""&&endDay!=""&&siteId!=""){
        		PlatUtil.ajaxProgress({
        			url : "webstatistics/DayTrendController.do?getTopSourceData",
        			params :{"startDay":startDay,"endDay":endDay,"type":type,"siteId":siteId},
        			callback : function(resultJson) {
        				myChart.setOption({
        					legend: {
        	        	    	show: true,
        	        	    	data:[resultJson.name]
        	        	    },
        	            	  xAxis : [
        	                           {
        	                              data:resultJson.title
        	                           }
        	                    ],
        		              series: [{
        		                  name: resultJson.name,
        		                  type: 'bar',
        		                  data: resultJson.data,
        		                  barWidth:40,
        		                  itemStyle : { normal: {label : {show: true,position:'top'},color:'#0DCC6C'}}
        		              }]
        	              });
        			}
        		});
        	}else{
        		if(startDay==""){
        			layer.alert("请选择开始日期!",{icon: 2,resize:false});
        		}else if(endDay==""){
        			layer.alert("请选择结束日期!",{icon: 2,resize:false});
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



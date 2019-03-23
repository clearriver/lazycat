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
    <title>分布情况</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <plattag:resources restype="css" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid,touchspin"></plattag:resources>
    <plattag:resources restype="js" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid,echart,touchspin"></plattag:resources>
  </head>
  
  <body>
  	<script type="text/javascript">
	$(function(){
		  PlatUtil.initUIComp();
	});
	</script>
  	<div class="form-horizontal" >
		<div class="form-group">
		    <plattag:datetime  name="BEGIN_DATE" allowblank="false" auth_type="write" value="" format="YYYY-MM-DD" istime="false" label_value="日期" placeholder="请选择日期" label_col_num="1" comp_col_num="3">
		   </plattag:datetime>
		   <plattag:number name="MONEY" auth_type="write" step="1" max="10000000" allowblank="false" decimals="0" 
		     placeholder="请输入金额" comp_col_num="3" label_col_num="1" label_value="金额"></plattag:number>
		   
		   <%-- <plattag:datetime name="END_DATE" allowblank="false" auth_type="write" value="" format="YYYY-MM-DD" istime="false" label_value="结束时间" placeholder="请输入结束时间" label_col_num="1" comp_col_num="3">
		   </plattag:datetime> --%>
		   <div class="col-sm-4">
		   		<button class="btn btn-outline btn-info btn-sm" type="button" onclick="searchData();" platreskey="">
				<i class="fa fa-search"></i>
				 查询
				</button>
		   </div>
	  	</div>
  	</div>
    <div id="main" style="width: 100%;height:500px;"></div>
    <script type="text/javascript">
        // 基于准备好的dom，初始化echarts实例
        var myChart = echarts.init(document.getElementById('main'));

        // 指定图表的配置项和数据
        var option = {
        	    title: {
        	        text: '剩余金额折线图'
        	    },
        	    tooltip : {
        	        trigger: 'axis',
        	        axisPointer: {
        	            type: 'cross',
        	            label: {
        	                backgroundColor: '#6a7985'
        	            }
        	        }
        	    },
        	    legend: {
        	        data:['剩余金额']
        	    },
        	    toolbox: {
        	        feature: {
        	            saveAsImage: {}
        	        }
        	    },
        	    grid: {
        	        left: '3%',
        	        right: '4%',
        	        bottom: '3%',
        	        containLabel: true
        	    },
        	    xAxis : [
        	        {
        	            type : 'category',
        	            boundaryGap : false,
        	            axisLabel: {
	        	            interval:0,//横轴信息全部显示
	        	            //rotate: 180,//60度角倾斜显示
	        	            formatter:function(val){
	        	                return val.split("").join("\n"); //横轴信息文字竖直显示
	        	           }
        	            }
        	        }
        	    ],
        	    yAxis : [
        	        {
        	            type : 'value'
        	        }
        	    ],
        	    series : [
        	        {
        	            name:'剩余金额',
        	            type:'line'
        	        }
        	    ]
        	};

        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
        
        function searchData(){
        	var BEGIN_DATE = $("input[name='BEGIN_DATE']").val();
        	var MONEY = $("input[name='MONEY']").val();
        	PlatUtil.ajaxProgress({
				url:"bittrade/RacingController.do?getResultChart",
				params : {BEGIN_DATE:BEGIN_DATE,MONEY:MONEY},
				callback : function(resultJson) {
					if (resultJson.success) {
						myChart.setOption({
							xAxis : [
			                	        {
			                	            type : 'category',
			                	            boundaryGap : false,
			                	            data:resultJson.qslist
			                	        }
						             ],
				              series: [{
			        	            name:'剩余金额',
			        	            type:'line',
			        	            data:resultJson.jwlist,
			        	            markPoint: {
			        	                data: [
			        	                    {type: 'max', name: '最大值'},
			        	                    {type: 'min', name: '最小值'}
			        	                ]
			        	            }
			        	        }]
			              });
					} else {
							parent.layer.alert("请求失败！",{icon: 2,resize:false});
					}
				}
			});
        }
    </script>
  </body>
</html>

<script type="text/javascript">

</script>

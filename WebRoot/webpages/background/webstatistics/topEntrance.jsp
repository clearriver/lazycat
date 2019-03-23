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
    <title>访问页面排行</title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge,Chrome=1" />
	<meta http-equiv="X-UA-Compatible" content="IE=9" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
    <plattag:resources restype="css" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid"></plattag:resources>
    <plattag:resources restype="js" loadres="bootstrap-checkbox,jquery-ui,jqgrid,jedate,select2,nicevalid"></plattag:resources>
  </head>
  
  <body>
    <script type="text/javascript">
	$(function(){
		  PlatUtil.initUIComp();
	});
	</script>
    <div class="plat-directlayout" style="height:100%" platundragable="true" compcode="direct_layout">
   <div class="ui-layout-center" platundragable="true" compcode="direct_layout">
   <div class="panel-Title">
	<h5 id="basetable_datatablepaneltitle">访问页面排行</h5>
</div>
<div class="titlePanel">
	<div class="title-search form-horizontal" id="basetable_search" formcontrolid="297ecf1262603fa80162604cfc51003a">
		<table>
			<tr>
				<td>查询条件</td>
				<td style="padding-left: 5px;">
					<div class="table-filter">
						<input type="text" style="width: 200px; " onclick="PlatUtil.showOrHideSearchTable(this);" class="table-form-control" name="search" readonly="readonly">
						<div class="table-filter-list" style="width: 420px; display: none;max-height: 280px;">
								<div class="form-group">
								    <plattag:select placeholder="" istree="false" label_col_num="3" label_value="统计站点" style="width:100%;" allowblank="true" comp_col_num="9" auth_type="write" value="${SITE_ID}" static_values="" dyna_interface="siteInfoService.findForSelect" dyna_param="无" name="Q_T.STATIC_SITEID_EQ">
								    </plattag:select>
								</div>
								<div class="form-group">
								    <jsp:include page="/webpages/common/plattagtpl/rangetime_tag.jsp">
									    <jsp:param name="label_col_num" value="3"></jsp:param>
									    <jsp:param name="format" value="YYYY-MM-DD"></jsp:param>
									    <jsp:param name="label_value" value="访问日期"></jsp:param>
									    <jsp:param name="comp_col_num" value="9"></jsp:param>
									    <jsp:param value="auth_type" name="write"></jsp:param>
									    <jsp:param name="posttimefmt" value="1"></jsp:param>
									    <jsp:param name="istime" value="false"></jsp:param>
									    <jsp:param name="allowblank" value="false"></jsp:param>
									    <jsp:param name="start_name" value="Q_T.INVIEW_DAY_GE"></jsp:param>
									    <jsp:param name="end_name" value="Q_T.INVIEW_DAY_LE"></jsp:param>
									    <jsp:param value="${preDate}" name="start_value" ></jsp:param>
									    <jsp:param value="${curDate}" name="end_value" ></jsp:param>
									</jsp:include>
								</div>
							<div class="table-filter-list-bottom">
								<a onclick="PlatUtil.tableSearchReset('basetable_search');" class="btn btn-default" href="javascript:void(0);"> 重 置</a> 
								<a onclick="PlatUtil.tableDoSearch('basetable_datatable','basetable_search',true);" class="btn btn-primary" href="javascript:void(0);"> 查 询</a>
							</div>
						</div>
					</div>
				</td>
			</tr>
		</table>
	</div>
</div>

<div class="gridPanel">
	<table id="basetable_datatable"></table>
</div>
<div id="basetable_datatable_pager"></div>
<script type="text/javascript">

$(function(){
	PlatUtil.initJqGrid({
		  tableId:"basetable_datatable",
		  searchPanelId:"basetable_search",
		  url: "appmodel/CommonUIController.do?tabledata&FORMCONTROL_ID=297ecf1262603fa80162604cfc51003a",
		  ondblClickRow:function(rowid,iRow,iCol,e){
				  
		  },
		  colModel: [
		         {name: "ALLNUM",label:"访问次数",
		         width: 150,align:"left",
		         
		         
		         
		         sortable:false
		         },
		         {name: "INVIEW_NAME",label:"访问界面",
		         width: 200,align:"left",
		         
		         
		         
		         sortable:false
		         },
		         {name: "INVIEW_SOURCE",label:"界面地址",
		         width: 100,align:"left",
		         
		         
		         
		         sortable:false
		         }
           ]
	 });
});
</script>
</div>
</div>
  </body>
</html>

<script type="text/javascript">

</script>

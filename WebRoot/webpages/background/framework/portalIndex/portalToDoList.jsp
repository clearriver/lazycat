<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<div class="dashboard-stat blue col-sm-2" style="margin-right:4.1%;">
	<div class="visual"><i class="fa fa-comments-o"></i></div>
	<div class="details">
           <div class="number"> ${dbnum} </div>
           <div class="desc"> 今日累计申请笔数 </div>
       </div>
</div>

<div class="dashboard-stat red col-sm-2" style="margin-right:4.1%;">
		<div class="visual"><i class="fa fa-area-chart"></i></div>
		<div class="details">
            <div class="number"> ${Cpu}</div>
            <div class="desc"> 今日累计担保笔数 </div>
        </div>       
</div>
<div class="dashboard-stat green col-sm-2" style="margin-right:4.1%;">
	<div class="visual"><i class="fa fa-pie-chart"></i></div>
	<div class="details">
           <div class="number"> ${Memery}</div>
           <div class="desc"> 今日累计担保金额 </div>
       </div>
</div>
<div class="dashboard-stat purple col-sm-2" style="margin-right:4.1%;">
	<div class="visual"><i class="fa fa-pie-chart"></i></div>
	<div class="details">
           <div class="number"> ${onlineUserNum}</div>
           <div class="desc"> 今日累计到期笔数 </div>
       </div>
</div>
<div class="dashboard-stat lightblue col-sm-2">
	<div class="visual"><i class="fa fa-television"></i></div>
	<div class="details">
           <div class="number"> ${videoClickNum} </div>
           <div class="desc"> 截止目前逾期笔数 </div>
       </div>
</div>




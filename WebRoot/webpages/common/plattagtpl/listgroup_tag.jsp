<div class="panel-Title">
	<h5>${grouptitle}</h5>
</div>
<div
	style="position:absolute; top:32px; left:0px; right:0px; bottom:0px;">
	<ul class="list-group plat-listgroup slimscroll-enable" onclickfn="${onclickfn!''}" >
	    <!--  
		<li class="list-group-item plat-listgroupon"><span class="badge badge-primary">14</span>
			<i class="fa fa-exclamation-circle"></i>&nbsp;食品企业总数</li>
		-->
		<#list groupList as group>
		   <li class="list-group-item" onclick="PlatUtil.onListGroupClick(this,'${group.VALUE}');" >
		      <#if group.GROUP_NUM?? >
			  <span class="badge ${group.NUM_COLOR?default("badge-primary")}"
			  >${group.GROUP_NUM}</span> 
			  </#if>
			  <#if group.GROUP_FONT?? >
			  <i class="${group.GROUP_FONT}"></i>
			  </#if>
			  &nbsp;${group.LABEL}
		   </li>
	    </#list>
	</ul>
</div>

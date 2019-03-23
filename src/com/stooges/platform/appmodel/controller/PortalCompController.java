/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatSystemInfoUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.util.PlatWindowsInfoUtil;
import com.stooges.platform.appmodel.service.PortalCompService;
import com.stooges.platform.appmodel.service.PortalRowconfService;
import com.stooges.platform.cms.service.VideoCourseService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.system.service.SysUserService;
import com.stooges.platform.workflow.service.JbpmTaskService;

/**
 * 
 * 描述 门户组件业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-12 14:39:45
 */
@Controller  
@RequestMapping("/appmodel/PortalCompController")  
public class PortalCompController extends BaseController {
    /**
     * 
     */
    @Resource
    private PortalCompService portalCompService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private PortalRowconfService portalRowconfService;
    
    /**
     * 
     */
    @Resource
    private SysUserService sysUserService;
    /**
     * 
     */
    @Resource
    private VideoCourseService videoCourseService;
    
    /**
     * 删除门户组件数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        portalCompService.deleteRecords("PLAT_APPMODEL_PORTALCOMP","COMP_ID",selectColValues.split(","));
        portalRowconfService.updateCompToNull(selectColValues);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改门户组件数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> portalComp = PlatBeanUtil.getMapFromRequest(request);
        String COMP_ID = (String) portalComp.get("COMP_ID");
        if(StringUtils.isEmpty(COMP_ID)){
            portalComp.put("COMP_CREATETIME",PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        portalComp = portalCompService.saveOrUpdate("PLAT_APPMODEL_PORTALCOMP",
                portalComp,AllConstants.IDGENERATOR_UUID,null);
        COMP_ID = (String) portalComp.get("COMP_ID");
        String COMP_URL = (String) portalComp.get("COMP_URL");
        portalRowconfService.updateCompUrl(COMP_ID, COMP_URL);
        portalComp.put("success", true);
        this.printObjectJsonString(portalComp, response);
    }
    
    /**
     * 跳转到门户组件表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String COMP_ID = request.getParameter("COMP_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> portalComp = null;
        if(StringUtils.isNotEmpty(COMP_ID)){
            portalComp = this.portalCompService.getRecord("PLAT_APPMODEL_PORTALCOMP"
                    ,new String[]{"COMP_ID"},new Object[]{COMP_ID});
        }else{
            StringBuffer compUrl = new StringBuffer("appmodel/DesignController.do?");
            compUrl.append("goIncludeDesign&DESIGN_CODE=你的设计编码");
            portalComp = new HashMap<String,Object>();
            portalComp.put("COMP_URL", compUrl.toString());
        }
        request.setAttribute("portalComp", portalComp);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 
     * 描述
     * @created 2017年8月17日 下午3:44:59
     * @param request
     * @return
     */
    @RequestMapping(params = "portalToDoList")
    public ModelAndView main(HttpServletRequest request) {
        SqlFilter sqlFilter = new SqlFilter(request);
        int dbnum = 0;
        //String Cpu = PlatWindowsInfoUtil.getCpuRatioForWindows();
        Map<String,Object> cpuPerc = PlatSystemInfoUtil.getEasyCpuPerc();
        Map<String,Object> jvmInfo =  PlatSystemInfoUtil.getJVMInfo();
        //String Memery = PlatWindowsInfoUtil.getMemery();
        List<Map<String,Object>> list = sysUserService.findOnlineUsers(sqlFilter,null);
        request.setAttribute("dbnum", dbnum);
        request.setAttribute("Cpu", cpuPerc.get("cpuPercCombined"));
        request.setAttribute("Memery", jvmInfo.get("usedMemoryPre")+"%");
        request.setAttribute("onlineUserNum", list.size());
        request.setAttribute("videoClickNum", 0);
        return new ModelAndView("background/framework/portalIndex/portalToDoList");
    }
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.PlatBeanUtil;
import com.stooges.platform.appmodel.service.SheduleService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 定时任务业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-27 16:37:05
 */
@Controller  
@RequestMapping("/system/SheduleController")  
public class SheduleController extends BaseController {
    /**
     * 
     */
    @Resource
    private SheduleService sheduleService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除定时任务数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "updateStatus")
    public void updateStatus(HttpServletRequest request,
            HttpServletResponse response) {
        String sheduleIds = request.getParameter("selectColValues");
        String status = request.getParameter("status");
        sheduleService.updateStatus(sheduleIds, Integer.parseInt(status));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 删除定时任务数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        sheduleService.deleteCascadeJob(selectColValues);
        sysLogService.saveBackLog("定时器管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的定时任务", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改定时任务数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> shedule = PlatBeanUtil.getMapFromRequest(request);
        String SHEDULE_ID = (String) shedule.get("SHEDULE_ID");
        if(StringUtils.isEmpty(SHEDULE_ID)){
            String SHEDULE_CODE = (String) shedule.get("SHEDULE_CODE");
            boolean isExists = sheduleService.isExistsShedule(SHEDULE_CODE);
            if(isExists){
                shedule.put("success", false);
                shedule.put("msg","该定时器编码已经存在!");
                this.printObjectJsonString(shedule, response);
                return;
            }
        }
        shedule = sheduleService.saveOrUpdateCascadeJob(shedule);
        if(StringUtils.isNotEmpty(SHEDULE_ID)){
            sysLogService.saveBackLog("定时器管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+SHEDULE_ID+"]定时任务", request);
        }else{
            SHEDULE_ID = (String) shedule.get("SHEDULE_ID");
            sysLogService.saveBackLog("定时器管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+SHEDULE_ID+"]定时任务", request);
        }
        shedule.put("success", true);
        this.printObjectJsonString(shedule, response);
    }
    
    /**
     * 跳转到定时任务表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String SHEDULE_ID = request.getParameter("SHEDULE_ID");
        Map<String,Object> shedule = null;
        if(StringUtils.isNotEmpty(SHEDULE_ID)){
            shedule = this.sheduleService.getRecord("PLAT_SYSTEM_SHEDULE"
                    ,new String[]{"SHEDULE_ID"},new Object[]{SHEDULE_ID});
        }else{
            shedule = new HashMap<String,Object>();
            shedule.put("SHEDULE_CRON", "* * * * * ?");
        }
        request.setAttribute("shedule", shedule);
        return new ModelAndView("background/system/shedule/sheduleForm");
    }
    
}

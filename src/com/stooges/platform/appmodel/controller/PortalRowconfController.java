/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.appmodel.service.PortalRowconfService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 行组件配置业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 15:47:00
 */
@Controller  
@RequestMapping("/appmodel/PortalRowconfController")  
public class PortalRowconfController extends BaseController {
    /**
     * 
     */
    @Resource
    private PortalRowconfService portalRowconfService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除行组件配置数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        portalRowconfService.deleteRecords("PLAT_APPMODEL_PORTALROWCONF","CONF_ID",selectColValues.split(","));
        sysLogService.saveBackLog("门户管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的行组件配置", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改行组件配置数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> portalRowconf = PlatBeanUtil.getMapFromRequest(request);
        //获取主题ID
        String THEME_ID = (String) portalRowconf.get("THEME_ID");
        String CONF_COMPID = (String) portalRowconf.get("CONF_COMPID");
        String CONF_ID = (String) portalRowconf.get("CONF_ID");
        boolean isExists = portalRowconfService.isExists(THEME_ID, CONF_ID, CONF_COMPID);
        if(isExists){
            portalRowconf.put("success", false);
            portalRowconf.put("msg","当前主题中已经配置了该组件,不允许重复配置!");
        }else{
            portalRowconf = portalRowconfService.saveOrUpdate("PLAT_APPMODEL_PORTALROWCONF",
                    portalRowconf,AllConstants.IDGENERATOR_UUID,null);
            portalRowconf.put("success", true);
        }
        this.printObjectJsonString(portalRowconf, response);
    }
    
    /**
     * 跳转到行组件配置表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String CONF_ID = request.getParameter("CONF_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        String THEME_ID = request.getParameter("THEME_ID");
        Map<String,Object> portalRowconf = null;
        if(StringUtils.isNotEmpty(CONF_ID)){
            portalRowconf = this.portalRowconfService.getRecord("PLAT_APPMODEL_PORTALROWCONF"
                    ,new String[]{"CONF_ID"},new Object[]{CONF_ID});
        }else{
            portalRowconf = new HashMap<String,Object>();
        }
        portalRowconf.put("THEME_ID", THEME_ID);
        request.setAttribute("portalRowconf", portalRowconf);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

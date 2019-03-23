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
import com.stooges.platform.appmodel.service.PortalRowService;
import com.stooges.platform.appmodel.service.PortalThemeService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 门户行业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 15:07:52
 */
@Controller  
@RequestMapping("/appmodel/PortalRowController")  
public class PortalRowController extends BaseController {
    /**
     * 
     */
    @Resource
    private PortalRowService portalRowService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private PortalThemeService portalThemeService;
    /**
     * 删除门户行数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String ROW_ID = request.getParameter("ROW_ID");
        portalRowService.deleteCascadeConfg(ROW_ID);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改门户行数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> portalRow = PlatBeanUtil.getMapFromRequest(request);
        portalRow = portalRowService.saveCascadeRowConf(portalRow);
        portalRow.put("success", true);
        this.printObjectJsonString(portalRow, response);
    }
    
    /**
     * 跳转到门户行表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String ROW_ID = request.getParameter("ROW_ID");
        String ROW_THEMEID = request.getParameter("ROW_THEMEID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> portalRow = null;
        if(StringUtils.isNotEmpty(ROW_ID)){
            portalRow = this.portalRowService.getRecord("PLAT_APPMODEL_PORTALROW"
                    ,new String[]{"ROW_ID"},new Object[]{ROW_ID});
        }else{
            portalRow = new HashMap<String,Object>();
            portalRow.put("ROW_THEMEID",ROW_THEMEID);
            portalRow.put("ROW_HEIGHT","100");
        }
        request.setAttribute("portalRow", portalRow);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 更新行排序和主题名称
     * @param request
     * @param response
     */
    @RequestMapping(params = "updateRowSnAndTitle")
    public void updateRowSnAndTitle(HttpServletRequest request,
            HttpServletResponse response) {
        String THEME_ID = request.getParameter("THEME_ID");
        String THEME_NAME = request.getParameter("THEME_NAME");
        String rowJson = request.getParameter("rowJson");
        String isAdminDesign = request.getParameter("isAdminDesign");
        portalThemeService.updateThemeNameAndRowOrder(THEME_ID, THEME_NAME, rowJson,isAdminDesign);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
}

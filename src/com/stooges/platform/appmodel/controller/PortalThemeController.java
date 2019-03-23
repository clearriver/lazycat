/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.appmodel.service.PortalRowService;
import com.stooges.platform.appmodel.service.PortalRowconfService;
import com.stooges.platform.appmodel.service.PortalThemeService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 门户主题业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 10:20:49
 */
@Controller  
@RequestMapping("/appmodel/PortalThemeController")  
public class PortalThemeController extends BaseController {
    /**
     * 
     */
    @Resource
    private PortalThemeService portalThemeService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private PortalRowService portalRowService;
    /**
     * 
     */
    @Resource
    private PortalRowconfService portalRowconfService;
    
    /**
     * 删除门户主题数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        portalThemeService.deleteCascade(selectColValues);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改门户主题数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> portalTheme = PlatBeanUtil.getMapFromRequest(request);
        String THEME_ID = (String) portalTheme.get("THEME_ID");
        if(StringUtils.isEmpty(THEME_ID)){
            Map<String,Object> sysUser = PlatAppUtil.getBackPlatLoginUser();
            String CREATOR_ID = (String) sysUser.get("SYSUSER_ID");
            String CREATOR_NAME = (String) sysUser.get("SYSUSER_NAME");
            portalTheme.put("CREATOR_ID", CREATOR_ID);
            portalTheme.put("CREATOR_NAME", CREATOR_NAME);
            portalTheme.put("THEME_CREATETIME",PlatDateTimeUtil.
                    formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        portalTheme = portalThemeService.saveOrUpdate("PLAT_APPMODEL_PORTALTHEME",
                portalTheme,AllConstants.IDGENERATOR_UUID,null);
        portalTheme.put("success", true);
        this.printObjectJsonString(portalTheme, response);
    }
    
    /**
     * 跳转到门户主题表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String THEME_ID = request.getParameter("THEME_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> portalTheme = null;
        if(StringUtils.isNotEmpty(THEME_ID)){
            portalTheme = this.portalThemeService.getRecord("PLAT_APPMODEL_PORTALTHEME"
                    ,new String[]{"THEME_ID"},new Object[]{THEME_ID});
        }else{
            portalTheme = new HashMap<String,Object>();
        }
        request.setAttribute("portalTheme", portalTheme);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 跳转到门户在线设计界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goDesign")
    public ModelAndView goDesign(HttpServletRequest request) {
        String THEME_ID = request.getParameter("THEME_ID");
        Map<String,Object> portalTheme = this.portalThemeService.getRecord("PLAT_APPMODEL_PORTALTHEME"
                ,new String[]{"THEME_ID"},new Object[]{THEME_ID});
        List<Map<String,Object>> rowList = portalRowService.findByThemeId(THEME_ID);
        for(Map<String,Object> row:rowList){
            String ROW_ID = (String) row.get("ROW_ID");
            List<Map<String,Object>> confList = portalRowconfService.findByRowId(ROW_ID);
            row.put("confList", confList);
        }
        request.setAttribute("rowList", rowList);
        request.setAttribute("portalTheme", portalTheme);
        request.setAttribute("isAdminDesign", "true");
        return new ModelAndView("background/framework/portaldesign");
    }
    
    /**
     * 跳转到后台门户首页
     * @param request
     * @return
     */
    @RequestMapping(params = "goportal")
    public ModelAndView goportal(HttpServletRequest request) {
        Map<String,Object> loginUser = PlatAppUtil.getBackPlatLoginUser();
        String SYSUSER_ID = (String) loginUser.get("SYSUSER_ID");
        String THEME_ID = request.getParameter("THEME_ID");
        List<Map<String,Object>> mythemeList = portalThemeService.createAndfindByCreatorId(SYSUSER_ID);
        Map<String,Object> portalTheme = null;
        if(StringUtils.isNotEmpty(THEME_ID)){
            portalTheme = portalThemeService.getRecord("PLAT_APPMODEL_PORTALTHEME",
                    new String[]{"THEME_ID"},new Object[]{THEME_ID});
        }else{
            portalTheme = mythemeList.get(0);
            THEME_ID = (String) portalTheme.get("THEME_ID");
        }
        List<Map<String,Object>> rowList = portalRowService.findByThemeId(THEME_ID);
        for(Map<String,Object> row:rowList){
            String ROW_ID = (String) row.get("ROW_ID");
            List<Map<String,Object>> confList = portalRowconfService.findByRowId(ROW_ID);
            row.put("confList", confList);
        }
        request.setAttribute("rowList", rowList);
        request.setAttribute("portalTheme", portalTheme);
        request.setAttribute("mythemeList",mythemeList);
        request.setAttribute("isAdminDesign", "false");
        return new ModelAndView("background/framework/portaldesign");
    }
    
}

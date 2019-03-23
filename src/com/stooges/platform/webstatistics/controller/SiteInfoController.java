/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.controller;

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
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.webstatistics.service.SiteInfoService;

/**
 * 
 * 描述 统计站点信息业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2018-03-22 17:08:50
 */
@Controller  
@RequestMapping("/webstatistics/SiteInfoController")  
public class SiteInfoController extends BaseController {
    /**
     * 
     */
    @Resource
    private SiteInfoService siteInfoService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除统计站点信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        siteInfoService.deleteRecords("PLAT_WEBSTATISTICS_SITEINFO","SITEINFO_ID",selectColValues.split(","));
        sysLogService.saveBackLog("站点统计管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的统计站点信息", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改统计站点信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> siteInfo = PlatBeanUtil.getMapFromRequest(request);
        String SITEINFO_ID = (String) siteInfo.get("SITEINFO_ID");
        if(StringUtils.isEmpty(SITEINFO_ID)){
            siteInfo.put("SITEINFO_TIME",PlatDateTimeUtil.formatDate(new Date(), 
                    "yyyy-MM-dd HH:mm:ss"));
        }
        siteInfo = siteInfoService.saveOrUpdate("PLAT_WEBSTATISTICS_SITEINFO",
                siteInfo,AllConstants.IDGENERATOR_UUID,null);
        siteInfo.put("success", true);
        this.printObjectJsonString(siteInfo, response);
    }
    
    /**
     * 跳转到统计站点信息表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String SITEINFO_ID = request.getParameter("SITEINFO_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> siteInfo = null;
        if(StringUtils.isNotEmpty(SITEINFO_ID)){
            siteInfo = this.siteInfoService.getRecord("PLAT_WEBSTATISTICS_SITEINFO"
                    ,new String[]{"SITEINFO_ID"},new Object[]{SITEINFO_ID});
        }else{
            siteInfo = new HashMap<String,Object>();
        }
        request.setAttribute("siteInfo", siteInfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.wsbs.service.SercatalogService;

/**
 * 
 * 描述 目录表业务相关Controller
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-15 11:26:44
 */
@Controller  
@RequestMapping("/wsbs/SercatalogController")  
public class SercatalogController extends BaseController {
    /**
     * 
     */
    @Resource
    private SercatalogService sercatalogService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除目录表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String catalogId = request.getParameter("catalogId");
        sercatalogService.deleteRecords("PLAT_WSBS_SERCATALOG","SERCATALOG_ID",catalogId.split(","));
        sysLogService.saveBackLog("服务事项管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+catalogId+"]的目录表", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改目录表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> sercatalog = PlatBeanUtil.getMapFromRequest(request);
        String SERCATALOG_ID = (String) sercatalog.get("SERCATALOG_ID");
        sercatalog = sercatalogService.saveOrUpdate("PLAT_WSBS_SERCATALOG",
                sercatalog,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(SERCATALOG_ID)){
            sysLogService.saveBackLog("服务事项管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+SERCATALOG_ID+"]目录表", request);
        }else{
            SERCATALOG_ID = (String) sercatalog.get("SERCATALOG_ID");
            sysLogService.saveBackLog("服务事项管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+SERCATALOG_ID+"]目录表", request);
        }
        sercatalog.put("success", true);
        this.printObjectJsonString(sercatalog, response);
    }
    
    /**
     * 跳转到目录表表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String SERCATALOG_ID = request.getParameter("SERCATALOG_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> sercatalog = null;
        if(StringUtils.isNotEmpty(SERCATALOG_ID)){
            sercatalog = this.sercatalogService.getRecord("PLAT_WSBS_SERCATALOG"
                    ,new String[]{"SERCATALOG_ID"},new Object[]{SERCATALOG_ID});
        }else{
            sercatalog = new HashMap<String,Object>();
        }
        request.setAttribute("sercatalog", sercatalog);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

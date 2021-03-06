/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.controller;

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
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.workflow.service.RemindsService;

/**
 * 
 * 描述 催办信息业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-11-16 08:50:10
 */
@Controller  
@RequestMapping("/workflow/RemindsController")  
public class RemindsController extends BaseController {
    /**
     * 
     */
    @Resource
    private RemindsService remindsService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除催办信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        remindsService.deleteRecords("JBPM6_REMINDS","REMINDS_ID",selectColValues.split(","));
        sysLogService.saveBackLog("我的申请",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的催办信息", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改催办信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> reminds = PlatBeanUtil.getMapFromRequest(request);
        String REMINDS_EXEIDS = (String) reminds.get("REMINDS_EXEIDS");
        String REMINDS_CONTENT = (String) reminds.get("REMINDS_CONTENT");
        boolean result = remindsService.sendReminds(REMINDS_EXEIDS, REMINDS_CONTENT);
        reminds.put("success", result);
        this.printObjectJsonString(reminds, response);
    }
    
    /**
     * 跳转到催办信息表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String exeIds = request.getParameter("exeIds");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> reminds = new HashMap<String,Object>();
        reminds.put("REMINDS_EXEIDS", exeIds);
        request.setAttribute("reminds", reminds);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

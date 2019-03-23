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

import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.appmodel.service.InvokelogService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 服务调用日志业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-08-03 18:41:18
 */
@Controller  
@RequestMapping("/dataset/InvokelogController")  
public class InvokelogController extends BaseController {
    /**
     * 
     */
    @Resource
    private InvokelogService invokelogService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除服务调用日志数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        invokelogService.deleteRecords("MZT_DATASET_INVOKELOG","INVOKELOG_ID",selectColValues.split(","));
        sysLogService.saveBackLog("APP服务调用日志",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的服务调用日志", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改服务调用日志数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> invokelog = PlatBeanUtil.getMapFromRequest(request);
        String INVOKELOG_ID = (String) invokelog.get("INVOKELOG_ID");
        invokelog = invokelogService.saveOrUpdate("MZT_DATASET_INVOKELOG",
                invokelog,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //invokelog = invokelogService.saveOrUpdateTreeData("MZT_DATASET_INVOKELOG",
        //        invokelog,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(INVOKELOG_ID)){
            sysLogService.saveBackLog("APP服务调用日志",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+INVOKELOG_ID+"]服务调用日志", request);
        }else{
            INVOKELOG_ID = (String) invokelog.get("INVOKELOG_ID");
            sysLogService.saveBackLog("APP服务调用日志",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+INVOKELOG_ID+"]服务调用日志", request);
        }
        invokelog.put("success", true);
        this.printObjectJsonString(invokelog, response);
    }
    
    /**
     * 跳转到服务调用日志表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String INVOKELOG_ID = request.getParameter("INVOKELOG_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> invokelog = null;
        if(StringUtils.isNotEmpty(INVOKELOG_ID)){
            invokelog = this.invokelogService.getRecord("MZT_DATASET_INVOKELOG"
                    ,new String[]{"INVOKELOG_ID"},new Object[]{INVOKELOG_ID});
        }else{
            invokelog = new HashMap<String,Object>();
        }
        request.setAttribute("invokelog", invokelog);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

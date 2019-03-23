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
import com.stooges.platform.workflow.service.ExePresetService;

/**
 * 
 * 描述 实例预设办理人业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-26 09:04:38
 */
@Controller  
@RequestMapping("/workflow/ExePresetController")  
public class ExePresetController extends BaseController {
    /**
     * 
     */
    @Resource
    private ExePresetService exePresetService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除实例预设办理人数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        exePresetService.deleteRecords("JBPM6_EXEPRESET","EXEPRESET_ID",selectColValues.split(","));
        sysLogService.saveBackLog("流程监控管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的实例预设办理人", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改实例预设办理人数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> exePreset = PlatBeanUtil.getMapFromRequest(request);
        String EXEPRESET_EXEID = (String) exePreset.get("EXEPRESET_EXEID");
        Map<String,Object> oldExePreSet = exePresetService.
                getRecord("JBPM6_EXEPRESET",new String[]{"EXEPRESET_EXEID"},
                        new Object[]{EXEPRESET_EXEID});
        if(oldExePreSet!=null){
            exePreset.put("EXEPRESET_ID",oldExePreSet.get("EXEPRESET_ID"));
        }
        exePreset = exePresetService.saveOrUpdate("JBPM6_EXEPRESET",
                exePreset,AllConstants.IDGENERATOR_UUID,null);
        exePreset.put("success", true);
        this.printObjectJsonString(exePreset, response);
    }
    
    /**
     * 跳转到实例预设办理人表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String EXEID = request.getParameter("EXEID");
        request.setAttribute("EXEID", EXEID);
        return PlatUICompUtil.goDesignUI("expresetlist", request);
    }
}

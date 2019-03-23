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
import com.stooges.core.model.SqlFilter;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.workflow.service.FlowTypeService;

/**
 * 
 * 描述 流程类别业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-03 16:12:37
 */
@Controller  
@RequestMapping("/workflow/FlowTypeController")  
public class FlowTypeController extends BaseController {
    /**
     * 
     */
    @Resource
    private FlowTypeService flowTypeService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除流程类别数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String typeId = request.getParameter("typeId");
        flowTypeService.deleteCascadeFlowDef(typeId);
        sysLogService.saveBackLog("流程定义管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+typeId+"]的流程类别", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改流程类别数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> flowType = PlatBeanUtil.getMapFromRequest(request);
        String FLOWTYPE_ID = (String) flowType.get("FLOWTYPE_ID");
        flowType = flowTypeService.saveOrUpdate("JBPM6_FLOWTYPE",
                flowType,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(FLOWTYPE_ID)){
            sysLogService.saveBackLog("流程定义管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+FLOWTYPE_ID+"]流程类别", request);
        }else{
            FLOWTYPE_ID = (String) flowType.get("FLOWTYPE_ID");
            sysLogService.saveBackLog("流程定义管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+FLOWTYPE_ID+"]流程类别", request);
        }
        flowType.put("success", true);
        this.printObjectJsonString(flowType, response);
    }
    
    /**
     * 跳转到流程类别表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String FLOWTYPE_ID = request.getParameter("FLOWTYPE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> flowType = null;
        if(StringUtils.isNotEmpty(FLOWTYPE_ID)){
            flowType = this.flowTypeService.getRecord("JBPM6_FLOWTYPE"
                    ,new String[]{"FLOWTYPE_ID"},new Object[]{FLOWTYPE_ID});
        }else{
            flowType = new HashMap<String,Object>();
        }
        request.setAttribute("flowType", flowType);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 获取树形的流程类别和流程定义数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "typeAndDefs")
    public void typeAndDefs(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> params= PlatBeanUtil.getMapFromRequest(request);
        String typeDefJson = flowTypeService.getTypeAndDefJson(params);
        this.printJsonString(typeDefJson, response);
    }
    
    /**
     * 自动补全流程类别和定义
     * @param request
     * @param response
     */
    @RequestMapping(params = "autoTypeAndDef")
    public void autoTypeAndDef(HttpServletRequest request,
            HttpServletResponse response) {
        //如果自动补全的类型为2,那么获取到key之后进行判断过滤
        //String keyword = request.getParameter("keyword");
        SqlFilter sqlFilter = new SqlFilter(request);
        List<Map<String,Object>> list = flowTypeService.findAutoTypeDef(sqlFilter);
        String json = JSON.toJSONString(list);
        this.printJsonString(json.toLowerCase(), response);
    }
   
}

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
import com.stooges.core.model.SqlFilter;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.platform.appmodel.service.FormcontrolService;
import com.stooges.platform.appmodel.service.QueryConditionService;
import com.stooges.platform.common.controller.BaseController;

/**
 * 
 * 描述 QueryCondition业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-03-14 10:55:40
 */
@Controller  
@RequestMapping("/appmodel/QueryConditionController")  
public class QueryConditionController extends BaseController {
    /**
     * 
     */
    @Resource
    private QueryConditionService queryConditionService;
    /**
     * 
     */
    @Resource
    private FormcontrolService formcontrolService;
    
    /**
     * 新增或者修改字典类别
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> queryCondition = PlatBeanUtil.getMapFromRequest(request);
        String QUERYCONDITION_ID = (String) queryCondition.get("QUERYCONDITION_ID");
        if(StringUtils.isEmpty(QUERYCONDITION_ID)){
            String formControlId = (String) queryCondition.get("QUERYCONDITION_FORMCONTROLID");
            int nextSn = queryConditionService.getNextSn(formControlId);
            queryCondition.put("QUERYCONDITION_SN",nextSn);
        }
        queryCondition = queryConditionService.saveOrUpdate("PLAT_APPMODEL_QUERYCONDITION",
                queryCondition,AllConstants.IDGENERATOR_UUID,null);
        queryCondition.put("success", true);
        this.printObjectJsonString(queryCondition, response);
    }
    
    /**
     * 获取列表的JSON数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "datagrid")
    public void datagrid(HttpServletRequest request,
            HttpServletResponse response) {
        SqlFilter filter = new SqlFilter(request);
        filter.addFilter("O_T.QUERYCONDITION_SN","ASC",SqlFilter.FILTER_TYPE_ORDER);
        List<Map<String,Object>> list = queryConditionService.findBySqlFilter(filter);
        this.printListJsonString(null, list, response);
    }
    
    /**
     * 跳转到查询条件配置界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String FORMCONTROL_ID = request.getParameter("FORMCONTROL_ID");
        String QUERYCONDITION_ID = request.getParameter("QUERYCONDITION_ID");
        Map<String,Object> queryCondition = null;
        if(StringUtils.isNotEmpty(QUERYCONDITION_ID)){
            queryCondition = this.queryConditionService.getRecord("PLAT_APPMODEL_QUERYCONDITION"
                    ,new String[]{"QUERYCONDITION_ID"},new Object[]{QUERYCONDITION_ID});
        }else{
            queryCondition = new HashMap<String,Object>();
            queryCondition.put("QUERYCONDITION_FORMCONTROLID",FORMCONTROL_ID);
        }
        Map<String,Object> formControl = formcontrolService.
                getRecord("PLAT_APPMODEL_FORMCONTROL",
                        new String[]{"FORMCONTROL_ID"},new Object[]{FORMCONTROL_ID});
        String designId = (String) formControl.get("FORMCONTROL_DESIGN_ID");
        queryCondition.put("DESIGN_ID", designId);
        request.setAttribute("queryCondition", queryCondition);
        return new ModelAndView("background/appmodel/querycondition/condition_form");
    }
    
    /**
     * 删除查询条件信息
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        queryConditionService.deleteRecords("PLAT_APPMODEL_QUERYCONDITION"
                ,"QUERYCONDITION_ID", selectColValues.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
}

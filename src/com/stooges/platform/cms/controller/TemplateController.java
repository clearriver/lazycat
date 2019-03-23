/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.controller;

import java.util.Date;
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
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.cms.service.TemplateService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 模版业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-03 17:19:57
 */
@Controller  
@RequestMapping("/cms/TemplateController")  
public class TemplateController extends BaseController {
    /**
     * 
     */
    @Resource
    private TemplateService templateService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除模版数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        templateService.deleteRecords("PLAT_CMS_TEMPLATE","TEMPLATE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("模版管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的模版", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改模版数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> template = PlatBeanUtil.getMapFromRequest(request);
        String TEMPLATE_ID = (String) template.get("TEMPLATE_ID");
        if(StringUtils.isEmpty(TEMPLATE_ID)){
            template.put("TEMPLATE_CREATETIME",PlatDateTimeUtil.formatDate(new Date(), 
                    "yyyy-MM-dd HH:mm:ss"));
        }
        template = templateService.saveOrUpdate("PLAT_CMS_TEMPLATE",
                template,AllConstants.IDGENERATOR_UUID,null);
        template.put("success", true);
        this.printObjectJsonString(template, response);
    }
    
    /**
     * 跳转到模版表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String TEMPLATE_ID = request.getParameter("TEMPLATE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> template = null;
        if(StringUtils.isNotEmpty(TEMPLATE_ID)){
            template = this.templateService.getRecord("PLAT_CMS_TEMPLATE"
                    ,new String[]{"TEMPLATE_ID"},new Object[]{TEMPLATE_ID});
        }else{
            template = new HashMap<String,Object>();
        }
        request.setAttribute("template", template);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

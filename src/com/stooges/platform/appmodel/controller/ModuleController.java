/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
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
import com.stooges.platform.appmodel.service.ModuleService;
import com.stooges.platform.common.controller.BaseController;

/**
 * 描述
 * @author 胡裕
 * @created 2017年2月4日 上午9:23:59
 */
@Controller
@RequestMapping("/appmodel/ModuleController")
public class ModuleController extends BaseController {
    /**
     * 
     */
    @Resource
    private ModuleService moduleService;
    
    /**
     * 跳转到表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goform")
    public ModelAndView goform(HttpServletRequest request) {
        String MODULE_ID = request.getParameter("MODULE_ID");
        String MODULE_PARENTID = request.getParameter("PARENT_ID");
        Map<String,Object> module = null;
        if(StringUtils.isNotEmpty(MODULE_ID)){
            module = this.moduleService.getRecord("PLAT_APPMODEL_MODULE",
                    new String[]{"MODULE_ID"}, new Object[]{MODULE_ID});
            MODULE_PARENTID = (String) module.get("MODULE_PARENTID");
        }
        Map<String,Object> parentModule = null;
        if(MODULE_PARENTID.equals("0")){
            parentModule = new HashMap<String,Object>();
            parentModule.put("MODULE_ID",MODULE_PARENTID);
            parentModule.put("MODULE_NAME","系统模块树");
        }else{
            parentModule = this.moduleService.getRecord("PLAT_APPMODEL_MODULE",
                    new String[]{"MODULE_ID"}, new Object[]{MODULE_PARENTID});
        }
        if(module==null){
            module = new HashMap<String,Object>();
        }
        module.put("MODULE_PARENTID",parentModule.get("MODULE_ID"));
        module.put("PARENT_NAME",parentModule.get("MODULE_NAME"));
        request.setAttribute("module", module);
        return new ModelAndView("background/appmodel/visual/module_form");
    }
    
    /**
     * 新增或者修改模块信息
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> module = PlatBeanUtil.getMapFromRequest(request);
        module = moduleService.saveOrUpdateTreeData("PLAT_APPMODEL_MODULE",
                module,AllConstants.IDGENERATOR_UUID,null);
        module.put("success", true);
        this.printObjectJsonString(module, response);
    }
    
    /**
     * 删除系统模块数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "delRecord")
    public void delRecord(HttpServletRequest request,
            HttpServletResponse response) {
        String moduleId = request.getParameter("treeNodeId");
        moduleService.deleteModuleCascade(moduleId);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
}

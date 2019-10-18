/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.controller;

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
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.iguarantee.service.ApplyService;

/**
 * 
 * 描述 实地调查表业务相关Controller
 * @author river
 * @version 1.0
 * @created 2019-08-18 20:44:57
 */
@Controller  
@RequestMapping("/iguarantee/ApplyController")  
public class ApplyController extends BaseController {
    /**
     * 
     */
    @Resource
    private ApplyService applyService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除实地调查表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        applyService.deleteRecords("PLAT_IGUARANTEE_ENTERPRISE_APPLY","ENTERPRISE_APPLY_ID",selectColValues.split(","));
        sysLogService.saveBackLog("实地调查(企业)",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的实地调查表", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改实地调查表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> apply = PlatBeanUtil.getMapFromRequest(request);
        String ENTERPRISE_APPLY_ID = (String) apply.get("ENTERPRISE_APPLY_ID");
        apply = applyService.saveOrUpdate("PLAT_IGUARANTEE_ENTERPRISE_APPLY",
                apply,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //apply = applyService.saveOrUpdateTreeData("PLAT_IGUARANTEE_ENTERPRISE_APPLY",
        //        apply,AllConstants.IDGENERATOR_UUID,null);
        
        Map<String, Object> enterprise=applyService.getRecord("PLAT_IGUARANTEE_ENTERPRISE", new String[]{"ENTERPRISE_CODE"},new Object[] {apply.get("ENTERPRISE_CODE")});
        if(enterprise==null||enterprise.size()==0) {
        	applyService.saveOrUpdate("PLAT_IGUARANTEE_ENTERPRISE",apply,AllConstants.IDGENERATOR_UUID,null);
        }
        
        if(StringUtils.isNotEmpty(ENTERPRISE_APPLY_ID)){
            sysLogService.saveBackLog("实地调查(企业)",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+ENTERPRISE_APPLY_ID+"]实地调查表", request);
        }else{
            ENTERPRISE_APPLY_ID = (String) apply.get("ENTERPRISE_APPLY_ID");
            sysLogService.saveBackLog("实地调查(企业)",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+ENTERPRISE_APPLY_ID+"]实地调查表", request);
        }
        apply.put("success", true);
        this.printObjectJsonString(apply, response);
    }
    
    /**
     * 跳转到实地调查表表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String ENTERPRISE_APPLY_ID = request.getParameter("ENTERPRISE_APPLY_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> apply = null;
        if(StringUtils.isNotEmpty(ENTERPRISE_APPLY_ID)){
            apply = this.applyService.getRecord("PLAT_IGUARANTEE_ENTERPRISE_APPLY"
                    ,new String[]{"ENTERPRISE_APPLY_ID"},new Object[]{ENTERPRISE_APPLY_ID});
        }else{
            apply = new HashMap<String,Object>();
        }
        request.setAttribute("apply", apply);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String ENTERPRISE_APPLY_ID = request.getParameter("ENTERPRISE_APPLY_ID");
        String APPLY_PARENTID = request.getParameter("APPLY_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> apply = null;
        if(StringUtils.isNotEmpty(ENTERPRISE_APPLY_ID)){
            apply = this.applyService.getRecord("PLAT_IGUARANTEE_ENTERPRISE_APPLY"
                    ,new String[]{"ENTERPRISE_APPLY_ID"},new Object[]{ENTERPRISE_APPLY_ID});
            APPLY_PARENTID = (String) apply.get("Apply_PARENTID");
        }
        Map<String,Object> parentApply = null;
        if(APPLY_PARENTID.equals("0")){
            parentApply = new HashMap<String,Object>();
            parentApply.put("ENTERPRISE_APPLY_ID",APPLY_PARENTID);
            parentApply.put("APPLY_NAME","实地调查表树");
        }else{
            parentApply = this.applyService.getRecord("PLAT_IGUARANTEE_ENTERPRISE_APPLY",
                    new String[]{"ENTERPRISE_APPLY_ID"}, new Object[]{APPLY_PARENTID});
        }
        if(apply==null){
            apply = new HashMap<String,Object>();
        }
        apply.put("APPLY_PARENTID",parentApply.get("ENTERPRISE_APPLY_ID"));
        apply.put("APPLY_PARENTNAME",parentApply.get("APPLY_NAME"));
        request.setAttribute("apply", apply);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

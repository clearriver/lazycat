/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.controller;

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
import com.stooges.platform.iguarantee.service.EnterpriseApplyService;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.service.JbpmService;

/**
 * 
 * 描述 企业委托担保申请信息表业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-08-05 00:18:35
 */
@Controller  
@RequestMapping("/iguarantee/EnterpriseApplyController")  
public class EnterpriseApplyController extends BaseController {
    @Resource
    private EnterpriseApplyService enterpriseApplyService;
    @Resource
    private SysLogService sysLogService;
    @Resource
    private JbpmService jbpmService;
    /**
     * 删除企业委托担保申请信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        enterpriseApplyService.deleteRecords("PLAT_IGUARANTEE_ENTERPRISE_APPLY","ENTERPRISE_APPLY_ID",selectColValues.split(","));
        sysLogService.saveBackLog("企业申请",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的企业委托担保申请信息表", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改企业委托担保申请信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> enterpriseApply = PlatBeanUtil.getMapFromRequest(request);
        String ENTERPRISE_APPLY_ID = (String) enterpriseApply.get("ENTERPRISE_APPLY_ID");
        enterpriseApply = enterpriseApplyService.saveOrUpdate("PLAT_IGUARANTEE_ENTERPRISE_APPLY",
                enterpriseApply,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //enterpriseApply = enterpriseApplyService.saveOrUpdateTreeData("PLAT_IGUARANTEE_ENTERPRISE_APPLY",
        //        enterpriseApply,AllConstants.IDGENERATOR_UUID,null);
        
        Map<String, Object> enterprise=enterpriseApplyService.getRecord("PLAT_IGUARANTEE_ENTERPRISE", new String[]{"ENTERPRISE_CODE"},new Object[] {enterpriseApply.get("ENTERPRISE_CODE")});
        if(enterprise==null||enterprise.size()==0) {
        	enterpriseApplyService.saveOrUpdate("PLAT_IGUARANTEE_ENTERPRISE",enterpriseApply,AllConstants.IDGENERATOR_UUID,null);
        }
        if(StringUtils.isNotEmpty(ENTERPRISE_APPLY_ID)){
            sysLogService.saveBackLog("企业申请",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+ENTERPRISE_APPLY_ID+"]企业委托担保申请信息表", request);
        }else{
            ENTERPRISE_APPLY_ID = (String) enterpriseApply.get("ENTERPRISE_APPLY_ID");
            sysLogService.saveBackLog("企业申请",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+ENTERPRISE_APPLY_ID+"]企业委托担保申请信息表", request);
        }
        enterpriseApply.put("success", true);
        this.printObjectJsonString(enterpriseApply, response);
    }

    /**
     * 新增或者修改个人委托担保申请信息表数据并启动流程
     * @author wuqh5
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveAndStartWf")
    public ModelAndView saveAndStartWf(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> applyinfo = PlatBeanUtil.getMapFromRequest(request);
        String IGUARANTEE_ID = (String) applyinfo.get("ENTERPRISE_APPLY_ID");
        applyinfo = enterpriseApplyService.saveOrUpdate("PLAT_IGUARANTEE_ENTERPRISE_APPLY",
                applyinfo,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //applyinfo = applyinfoService.saveOrUpdateTreeData("PLAT_IGUARANTEE_APPLYINFO",
        //        applyinfo,AllConstants.IDGENERATOR_UUID,null);
        Map<String, Object> enterprise=enterpriseApplyService.getRecord("PLAT_IGUARANTEE_ENTERPRISE", new String[]{"ENTERPRISE_CODE"},new Object[] {applyinfo.get("ENTERPRISE_CODE")});
        if(enterprise==null||enterprise.size()==0) {
        	enterpriseApplyService.saveOrUpdate("PLAT_IGUARANTEE_ENTERPRISE",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        }
        
        if(StringUtils.isNotEmpty(IGUARANTEE_ID)){
            sysLogService.saveBackLog("企业申请",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+IGUARANTEE_ID+"]企业委托担保申请信息表", request);
        }else{
            IGUARANTEE_ID = (String) applyinfo.get("IGUARANTEE_ID");
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+IGUARANTEE_ID+"]企业委托担保申请信息表", request);
        }
        applyinfo.put("success", true);
//        this.printObjectJsonString(applyinfo, response);
       
        String FLOWDEF_ID = request.getParameter("FLOWDEF_ID");
        String SERITEM_ID = request.getParameter("SERITEM_ID");
        JbpmFlowInfo jbpmFlowInfo = jbpmService.getJbpmFlowInfo(null, FLOWDEF_ID, "false",null);
        jbpmFlowInfo.setJbpmExeId("-1");
        jbpmFlowInfo.setJbpmSerItemId(SERITEM_ID);
        return jbpmService.getFlowDesignUI(request, jbpmFlowInfo);
    }
    @RequestMapping(params = "goFormWf")
    public ModelAndView goFormWf(HttpServletRequest request) {
        String EXECUTION_ID = request.getParameter("EXECUTION_ID");
        String FLOWDEF_ID = request.getParameter("FLOWDEF_ID");
        String SERITEM_ID = request.getParameter("SERITEM_ID");
        String jbpmIsQuery = request.getParameter("jbpmIsQuery");
        JbpmFlowInfo jbpmFlowInfo =null;
        if(StringUtils.isBlank(EXECUTION_ID)) {
        	jbpmFlowInfo = jbpmService.getJbpmFlowInfo(null, FLOWDEF_ID, "false",null);
            jbpmFlowInfo.setJbpmExeId("-1");
        }else {
        	jbpmFlowInfo = jbpmService.getJbpmFlowInfo(EXECUTION_ID,null,jbpmIsQuery,null);
        }
        jbpmFlowInfo.setJbpmSerItemId(SERITEM_ID);

        String recordId =request.getParameter(jbpmFlowInfo.getJbpmMainTablePkName());
        jbpmFlowInfo.setJbpmMainTableRecordId(recordId);
        
        return jbpmService.getFlowDesignUI(request, jbpmFlowInfo);
    }
    /**
     * 跳转到企业委托担保申请信息表表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String ENTERPRISE_APPLY_ID = request.getParameter("ENTERPRISE_APPLY_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> enterpriseApply = null;
        if(StringUtils.isNotEmpty(ENTERPRISE_APPLY_ID)){
            enterpriseApply = this.enterpriseApplyService.getRecord("PLAT_IGUARANTEE_ENTERPRISE_APPLY"
                    ,new String[]{"ENTERPRISE_APPLY_ID"},new Object[]{ENTERPRISE_APPLY_ID});
        }else{
            enterpriseApply = new HashMap<String,Object>();
        }
        request.setAttribute("apply", enterpriseApply);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String ENTERPRISE_APPLY_ID = request.getParameter("ENTERPRISE_APPLY_ID");
        String ENTERPRISEAPPLY_PARENTID = request.getParameter("ENTERPRISEAPPLY_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> enterpriseApply = null;
        if(StringUtils.isNotEmpty(ENTERPRISE_APPLY_ID)){
            enterpriseApply = this.enterpriseApplyService.getRecord("PLAT_IGUARANTEE_ENTERPRISE_APPLY"
                    ,new String[]{"ENTERPRISE_APPLY_ID"},new Object[]{ENTERPRISE_APPLY_ID});
            ENTERPRISEAPPLY_PARENTID = (String) enterpriseApply.get("EnterpriseApply_PARENTID");
        }
        Map<String,Object> parentEnterpriseApply = null;
        if(ENTERPRISEAPPLY_PARENTID.equals("0")){
            parentEnterpriseApply = new HashMap<String,Object>();
            parentEnterpriseApply.put("ENTERPRISE_APPLY_ID",ENTERPRISEAPPLY_PARENTID);
            parentEnterpriseApply.put("ENTERPRISEAPPLY_NAME","企业委托担保申请信息表树");
        }else{
            parentEnterpriseApply = this.enterpriseApplyService.getRecord("PLAT_IGUARANTEE_ENTERPRISE_APPLY",
                    new String[]{"ENTERPRISE_APPLY_ID"}, new Object[]{ENTERPRISEAPPLY_PARENTID});
        }
        if(enterpriseApply==null){
            enterpriseApply = new HashMap<String,Object>();
        }
        enterpriseApply.put("ENTERPRISEAPPLY_PARENTID",parentEnterpriseApply.get("ENTERPRISE_APPLY_ID"));
        enterpriseApply.put("ENTERPRISEAPPLY_PARENTNAME",parentEnterpriseApply.get("ENTERPRISEAPPLY_NAME"));
        request.setAttribute("enterpriseApply", enterpriseApply);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

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
import com.stooges.platform.iguarantee.service.CreditEnterpriseService;

/**
 * 
 * 描述 企业客户信用评分表模型业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-08-05 23:45:25
 */
@Controller  
@RequestMapping("/iguarantee/CreditEnterpriseController")  
public class CreditEnterpriseController extends BaseController {
    /**
     * 
     */
    @Resource
    private CreditEnterpriseService creditEnterpriseService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除企业客户信用评分表模型数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        creditEnterpriseService.deleteRecords("PLAT_IGUARANTEE_CREDIT_ENTERPRISE","CREDIT_ENTERPRISE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("风险管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的企业客户信用评分表模型", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改企业客户信用评分表模型数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> creditEnterprise = PlatBeanUtil.getMapFromRequest(request);
        String CREDIT_ENTERPRISE_ID = (String) creditEnterprise.get("CREDIT_ENTERPRISE_ID");
        creditEnterprise = creditEnterpriseService.saveOrUpdate("PLAT_IGUARANTEE_CREDIT_ENTERPRISE",
                creditEnterprise,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //creditEnterprise = creditEnterpriseService.saveOrUpdateTreeData("PLAT_IGUARANTEE_CREDIT_ENTERPRISE",
        //        creditEnterprise,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(CREDIT_ENTERPRISE_ID)){
            sysLogService.saveBackLog("风险管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+CREDIT_ENTERPRISE_ID+"]企业客户信用评分表模型", request);
        }else{
            CREDIT_ENTERPRISE_ID = (String) creditEnterprise.get("CREDIT_ENTERPRISE_ID");
            sysLogService.saveBackLog("风险管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+CREDIT_ENTERPRISE_ID+"]企业客户信用评分表模型", request);
        }
        creditEnterprise.put("success", true);
        this.printObjectJsonString(creditEnterprise, response);
    }
    
    /**
     * 跳转到企业客户信用评分表模型表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String CREDIT_ENTERPRISE_ID = request.getParameter("CREDIT_ENTERPRISE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> creditEnterprise = null;
        if(StringUtils.isNotEmpty(CREDIT_ENTERPRISE_ID)){
            creditEnterprise = this.creditEnterpriseService.getRecord("PLAT_IGUARANTEE_CREDIT_ENTERPRISE"
                    ,new String[]{"CREDIT_ENTERPRISE_ID"},new Object[]{CREDIT_ENTERPRISE_ID});
        }else{
            creditEnterprise = new HashMap<String,Object>();
        }
        request.setAttribute("creditEnterprise", creditEnterprise);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String CREDIT_ENTERPRISE_ID = request.getParameter("CREDIT_ENTERPRISE_ID");
        String CREDITENTERPRISE_PARENTID = request.getParameter("CREDITENTERPRISE_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> creditEnterprise = null;
        if(StringUtils.isNotEmpty(CREDIT_ENTERPRISE_ID)){
            creditEnterprise = this.creditEnterpriseService.getRecord("PLAT_IGUARANTEE_CREDIT_ENTERPRISE"
                    ,new String[]{"CREDIT_ENTERPRISE_ID"},new Object[]{CREDIT_ENTERPRISE_ID});
            CREDITENTERPRISE_PARENTID = (String) creditEnterprise.get("CreditEnterprise_PARENTID");
        }
        Map<String,Object> parentCreditEnterprise = null;
        if(CREDITENTERPRISE_PARENTID.equals("0")){
            parentCreditEnterprise = new HashMap<String,Object>();
            parentCreditEnterprise.put("CREDIT_ENTERPRISE_ID",CREDITENTERPRISE_PARENTID);
            parentCreditEnterprise.put("CREDITENTERPRISE_NAME","企业客户信用评分表模型树");
        }else{
            parentCreditEnterprise = this.creditEnterpriseService.getRecord("PLAT_IGUARANTEE_CREDIT_ENTERPRISE",
                    new String[]{"CREDIT_ENTERPRISE_ID"}, new Object[]{CREDITENTERPRISE_PARENTID});
        }
        if(creditEnterprise==null){
            creditEnterprise = new HashMap<String,Object>();
        }
        creditEnterprise.put("CREDITENTERPRISE_PARENTID",parentCreditEnterprise.get("CREDIT_ENTERPRISE_ID"));
        creditEnterprise.put("CREDITENTERPRISE_PARENTNAME",parentCreditEnterprise.get("CREDITENTERPRISE_NAME"));
        request.setAttribute("creditEnterprise", creditEnterprise);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

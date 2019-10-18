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
import com.stooges.platform.iguarantee.service.CreditPersonService;

/**
 * 
 * 描述 个人客户信用评分表业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-08-01 01:53:28
 */
@Controller  
@RequestMapping("/iguarantee/CreditPersonController")  
public class CreditPersonController extends BaseController {
    /**
     * 
     */
    @Resource
    private CreditPersonService creditPersonService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除个人客户信用评分表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        creditPersonService.deleteRecords("PLAT_IGUARANTEE_CREDIT_PERSON","CREDIT_PERSON_ID",selectColValues.split(","));
        sysLogService.saveBackLog("风险管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的个人客户信用评分表", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改个人客户信用评分表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> creditPerson = PlatBeanUtil.getMapFromRequest(request);
        String CREDIT_PERSON_ID = (String) creditPerson.get("CREDIT_PERSON_ID");
        creditPerson = creditPersonService.saveOrUpdate("PLAT_IGUARANTEE_CREDIT_PERSON",
                creditPerson,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //creditPerson = creditPersonService.saveOrUpdateTreeData("PLAT_IGUARANTEE_CREDIT_PERSON",
        //        creditPerson,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(CREDIT_PERSON_ID)){
            sysLogService.saveBackLog("风险管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+CREDIT_PERSON_ID+"]个人客户信用评分表", request);
        }else{
            CREDIT_PERSON_ID = (String) creditPerson.get("CREDIT_PERSON_ID");
            sysLogService.saveBackLog("风险管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+CREDIT_PERSON_ID+"]个人客户信用评分表", request);
        }
        creditPerson.put("success", true);
        this.printObjectJsonString(creditPerson, response);
    }
    
    /**
     * 跳转到个人客户信用评分表表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String CREDIT_PERSON_ID = request.getParameter("CREDIT_PERSON_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> creditPerson = null;
        if(StringUtils.isNotEmpty(CREDIT_PERSON_ID)){
            creditPerson = this.creditPersonService.getRecord("PLAT_IGUARANTEE_CREDIT_PERSON"
                    ,new String[]{"CREDIT_PERSON_ID"},new Object[]{CREDIT_PERSON_ID});
        }else{
            creditPerson = new HashMap<String,Object>();
        }
        request.setAttribute("creditPerson", creditPerson);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String CREDIT_PERSON_ID = request.getParameter("CREDIT_PERSON_ID");
        String CREDITPERSON_PARENTID = request.getParameter("CREDITPERSON_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> creditPerson = null;
        if(StringUtils.isNotEmpty(CREDIT_PERSON_ID)){
            creditPerson = this.creditPersonService.getRecord("PLAT_IGUARANTEE_CREDIT_PERSON"
                    ,new String[]{"CREDIT_PERSON_ID"},new Object[]{CREDIT_PERSON_ID});
            CREDITPERSON_PARENTID = (String) creditPerson.get("CreditPerson_PARENTID");
        }
        Map<String,Object> parentCreditPerson = null;
        if(CREDITPERSON_PARENTID.equals("0")){
            parentCreditPerson = new HashMap<String,Object>();
            parentCreditPerson.put("CREDIT_PERSON_ID",CREDITPERSON_PARENTID);
            parentCreditPerson.put("CREDITPERSON_NAME","个人客户信用评分表树");
        }else{
            parentCreditPerson = this.creditPersonService.getRecord("PLAT_IGUARANTEE_CREDIT_PERSON",
                    new String[]{"CREDIT_PERSON_ID"}, new Object[]{CREDITPERSON_PARENTID});
        }
        if(creditPerson==null){
            creditPerson = new HashMap<String,Object>();
        }
        creditPerson.put("CREDITPERSON_PARENTID",parentCreditPerson.get("CREDIT_PERSON_ID"));
        creditPerson.put("CREDITPERSON_PARENTNAME",parentCreditPerson.get("CREDITPERSON_NAME"));
        request.setAttribute("creditPerson", creditPerson);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

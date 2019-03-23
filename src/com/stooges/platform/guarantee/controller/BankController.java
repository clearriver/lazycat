/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.guarantee.controller;

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
import com.stooges.platform.guarantee.service.BankService;

/**
 * 
 * 描述 合作银行业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-03-05 22:44:10
 */
@Controller  
@RequestMapping("/guarantee/BankController")  
public class BankController extends BaseController {
    /**
     * 
     */
    @Resource
    private BankService bankService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除合作银行数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        bankService.deleteRecords("PLAT_GUARANTEE_BANK","BANK_ID",selectColValues.split(","));
        sysLogService.saveBackLog("合作银行管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的合作银行", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改合作银行数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> bank = PlatBeanUtil.getMapFromRequest(request);
        String BANK_ID = (String) bank.get("BANK_ID");
        bank = bankService.saveOrUpdate("PLAT_GUARANTEE_BANK",
                bank,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //bank = bankService.saveOrUpdateTreeData("PLAT_GUARANTEE_BANK",
        //        bank,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(BANK_ID)){
            sysLogService.saveBackLog("合作银行管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+BANK_ID+"]合作银行", request);
        }else{
            BANK_ID = (String) bank.get("BANK_ID");
            sysLogService.saveBackLog("合作银行管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+BANK_ID+"]合作银行", request);
        }
        bank.put("success", true);
        this.printObjectJsonString(bank, response);
    }
    
    /**
     * 跳转到合作银行表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String BANK_ID = request.getParameter("BANK_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> bank = null;
        if(StringUtils.isNotEmpty(BANK_ID)){
            bank = this.bankService.getRecord("PLAT_GUARANTEE_BANK"
                    ,new String[]{"BANK_ID"},new Object[]{BANK_ID});
        }else{
            bank = new HashMap<String,Object>();
        }
        request.setAttribute("bank", bank);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String BANK_ID = request.getParameter("BANK_ID");
        String BANK_PARENTID = request.getParameter("BANK_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> bank = null;
        if(StringUtils.isNotEmpty(BANK_ID)){
            bank = this.bankService.getRecord("PLAT_GUARANTEE_BANK"
                    ,new String[]{"BANK_ID"},new Object[]{BANK_ID});
            BANK_PARENTID = (String) bank.get("Bank_PARENTID");
        }
        Map<String,Object> parentBank = null;
        if(BANK_PARENTID.equals("0")){
            parentBank = new HashMap<String,Object>();
            parentBank.put("BANK_ID",BANK_PARENTID);
            parentBank.put("BANK_NAME","合作银行树");
        }else{
            parentBank = this.bankService.getRecord("PLAT_GUARANTEE_BANK",
                    new String[]{"BANK_ID"}, new Object[]{BANK_PARENTID});
        }
        if(bank==null){
            bank = new HashMap<String,Object>();
        }
        bank.put("BANK_PARENTID",parentBank.get("BANK_ID"));
        bank.put("BANK_PARENTNAME",parentBank.get("BANK_NAME"));
        request.setAttribute("bank", bank);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

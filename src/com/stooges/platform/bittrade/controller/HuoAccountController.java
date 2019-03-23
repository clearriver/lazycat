/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.controller;

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
import com.stooges.platform.bittrade.service.HuoAccountService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 火币账号信息业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-15 09:41:39
 */
@Controller  
@RequestMapping("/bittrade/HuoAccountController")  
public class HuoAccountController extends BaseController {
    /**
     * 
     */
    @Resource
    private HuoAccountService huoAccountService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除火币账号信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        huoAccountService.deleteRecords("PLAT_BITTRADE_HUOACCOUNT","HUOACCOUNT_ID",selectColValues.split(","));
        sysLogService.saveBackLog("行情管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的火币账号信息", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改火币账号信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> huoAccount = PlatBeanUtil.getMapFromRequest(request);
        String HUOACCOUNT_ID = (String) huoAccount.get("HUOACCOUNT_ID");
        huoAccount = huoAccountService.saveOrUpdate("PLAT_BITTRADE_HUOACCOUNT",
                huoAccount,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //huoAccount = huoAccountService.saveOrUpdateTreeData("PLAT_BITTRADE_HUOACCOUNT",
        //        huoAccount,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(HUOACCOUNT_ID)){
            sysLogService.saveBackLog("行情管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+HUOACCOUNT_ID+"]火币账号信息", request);
        }else{
            HUOACCOUNT_ID = (String) huoAccount.get("HUOACCOUNT_ID");
            sysLogService.saveBackLog("行情管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+HUOACCOUNT_ID+"]火币账号信息", request);
        }
        huoAccount.put("success", true);
        this.printObjectJsonString(huoAccount, response);
    }
    
    /**
     * 跳转到火币账号信息表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String HUOACCOUNT_ID = request.getParameter("HUOACCOUNT_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> huoAccount = null;
        if(StringUtils.isNotEmpty(HUOACCOUNT_ID)){
            huoAccount = this.huoAccountService.getRecord("PLAT_BITTRADE_HUOACCOUNT"
                    ,new String[]{"HUOACCOUNT_ID"},new Object[]{HUOACCOUNT_ID});
        }else{
            huoAccount = new HashMap<String,Object>();
        }
        request.setAttribute("huoAccount", huoAccount);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String HUOACCOUNT_ID = request.getParameter("HUOACCOUNT_ID");
        String HUOACCOUNT_PARENTID = request.getParameter("HUOACCOUNT_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> huoAccount = null;
        if(StringUtils.isNotEmpty(HUOACCOUNT_ID)){
            huoAccount = this.huoAccountService.getRecord("PLAT_BITTRADE_HUOACCOUNT"
                    ,new String[]{"HUOACCOUNT_ID"},new Object[]{HUOACCOUNT_ID});
            HUOACCOUNT_PARENTID = (String) huoAccount.get("HuoAccount_PARENTID");
        }
        Map<String,Object> parentHuoAccount = null;
        if(HUOACCOUNT_PARENTID.equals("0")){
            parentHuoAccount = new HashMap<String,Object>();
            parentHuoAccount.put("HUOACCOUNT_ID",HUOACCOUNT_PARENTID);
            parentHuoAccount.put("HUOACCOUNT_NAME","火币账号信息树");
        }else{
            parentHuoAccount = this.huoAccountService.getRecord("PLAT_BITTRADE_HUOACCOUNT",
                    new String[]{"HUOACCOUNT_ID"}, new Object[]{HUOACCOUNT_PARENTID});
        }
        if(huoAccount==null){
            huoAccount = new HashMap<String,Object>();
        }
        huoAccount.put("HUOACCOUNT_PARENTID",parentHuoAccount.get("HUOACCOUNT_ID"));
        huoAccount.put("HUOACCOUNT_PARENTNAME",parentHuoAccount.get("HUOACCOUNT_NAME"));
        request.setAttribute("huoAccount", huoAccount);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

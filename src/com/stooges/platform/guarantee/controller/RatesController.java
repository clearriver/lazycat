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
import com.stooges.platform.guarantee.service.RatesService;

/**
 * 
 * 描述 费率表业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-03-07 21:29:54
 */
@Controller  
@RequestMapping("/guarantee/RatesController")  
public class RatesController extends BaseController {
    /**
     * 
     */
    @Resource
    private RatesService ratesService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除费率表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        ratesService.deleteRecords("PLAT_GUARANTEE_RATES","RATES_ID",selectColValues.split(","));
        sysLogService.saveBackLog("保费费率设置",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的费率表", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改费率表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> rates = PlatBeanUtil.getMapFromRequest(request);
        String RATES_ID = (String) rates.get("RATES_ID");
        rates = ratesService.saveOrUpdate("PLAT_GUARANTEE_RATES",
                rates,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //rates = ratesService.saveOrUpdateTreeData("PLAT_GUARANTEE_RATES",
        //        rates,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(RATES_ID)){
            sysLogService.saveBackLog("保费费率设置",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+RATES_ID+"]费率表", request);
        }else{
            RATES_ID = (String) rates.get("RATES_ID");
            sysLogService.saveBackLog("保费费率设置",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+RATES_ID+"]费率表", request);
        }
        rates.put("success", true);
        this.printObjectJsonString(rates, response);
    }
    
    /**
     * 跳转到费率表表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String RATES_ID = request.getParameter("RATES_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> rates = null;
        if(StringUtils.isNotEmpty(RATES_ID)){
            rates = this.ratesService.getRecord("PLAT_GUARANTEE_RATES"
                    ,new String[]{"RATES_ID"},new Object[]{RATES_ID});
        }else{
            rates = new HashMap<String,Object>();
        }
        request.setAttribute("rates", rates);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String RATES_ID = request.getParameter("RATES_ID");
        String RATES_PARENTID = request.getParameter("RATES_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> rates = null;
        if(StringUtils.isNotEmpty(RATES_ID)){
            rates = this.ratesService.getRecord("PLAT_GUARANTEE_RATES"
                    ,new String[]{"RATES_ID"},new Object[]{RATES_ID});
            RATES_PARENTID = (String) rates.get("Rates_PARENTID");
        }
        Map<String,Object> parentRates = null;
        if(RATES_PARENTID.equals("0")){
            parentRates = new HashMap<String,Object>();
            parentRates.put("RATES_ID",RATES_PARENTID);
            parentRates.put("RATES_NAME","费率表树");
        }else{
            parentRates = this.ratesService.getRecord("PLAT_GUARANTEE_RATES",
                    new String[]{"RATES_ID"}, new Object[]{RATES_PARENTID});
        }
        if(rates==null){
            rates = new HashMap<String,Object>();
        }
        rates.put("RATES_PARENTID",parentRates.get("RATES_ID"));
        rates.put("RATES_PARENTNAME",parentRates.get("RATES_NAME"));
        request.setAttribute("rates", rates);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

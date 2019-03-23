/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.controller;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.bittrade.service.StockInfoService;

/**
 * 
 * 描述 股票涨跌幅业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2018-06-02 10:44:07
 */
@Controller  
@RequestMapping("/bittrade/StockInfoController")  
public class StockInfoController extends BaseController {
    /**
     * 
     */
    @Resource
    private StockInfoService stockInfoService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除股票涨跌幅数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        stockInfoService.deleteRecords("PLAT_BITTRADE_STOCKINFO","STOCKINFO_ID",selectColValues.split(","));
        sysLogService.saveBackLog("股票行情管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的股票涨跌幅", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    private String getRange(Double start,Double end){
        NumberFormat nFromat = NumberFormat.getPercentInstance();
        nFromat.setMinimumFractionDigits(2);
        nFromat.setGroupingUsed(false);
        Double closeopen= end-start;
        String RANGEINFO_UP= nFromat.format(closeopen/start);
        RANGEINFO_UP = RANGEINFO_UP.replace("%", "");
        return RANGEINFO_UP;
    }
    
    /**
     * 新增或者修改股票涨跌幅数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> stockInfo = PlatBeanUtil.getMapFromRequest(request);
        String STOCKINFO_ID = (String) stockInfo.get("STOCKINFO_ID");
        if(StringUtils.isEmpty(STOCKINFO_ID)){
            stockInfo.put("RKSJ",PlatDateTimeUtil.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
        }
        String SCDTJ = request.getParameter("SCDTJ");
        String DTHZDJ = request.getParameter("DTHZDJ");
        String SCDF= this.getRange(Double.parseDouble(SCDTJ), Double.parseDouble(DTHZDJ));
        stockInfo.put("SCDF", SCDF);
        String FTHZGJ = request.getParameter("FTHZGJ");
        if(StringUtils.isNotEmpty(FTHZGJ)){
            String FTZF = this.getRange(Double.parseDouble(DTHZDJ),Double.parseDouble(FTHZGJ));
            stockInfo.put("FTZF", FTZF);
        }
        stockInfo = stockInfoService.saveOrUpdate("PLAT_BITTRADE_STOCKINFO",
                stockInfo,AllConstants.IDGENERATOR_UUID,null);
        stockInfo.put("success", true);
        this.printObjectJsonString(stockInfo, response);
    }
    
    /**
     * 跳转到股票涨跌幅表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String STOCKINFO_ID = request.getParameter("STOCKINFO_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> stockInfo = null;
        if(StringUtils.isNotEmpty(STOCKINFO_ID)){
            stockInfo = this.stockInfoService.getRecord("PLAT_BITTRADE_STOCKINFO"
                    ,new String[]{"STOCKINFO_ID"},new Object[]{STOCKINFO_ID});
        }else{
            stockInfo = new HashMap<String,Object>();
        }
        request.setAttribute("stockInfo", stockInfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String STOCKINFO_ID = request.getParameter("STOCKINFO_ID");
        String STOCKINFO_PARENTID = request.getParameter("STOCKINFO_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> stockInfo = null;
        if(StringUtils.isNotEmpty(STOCKINFO_ID)){
            stockInfo = this.stockInfoService.getRecord("PLAT_BITTRADE_STOCKINFO"
                    ,new String[]{"STOCKINFO_ID"},new Object[]{STOCKINFO_ID});
            STOCKINFO_PARENTID = (String) stockInfo.get("StockInfo_PARENTID");
        }
        Map<String,Object> parentStockInfo = null;
        if(STOCKINFO_PARENTID.equals("0")){
            parentStockInfo = new HashMap<String,Object>();
            parentStockInfo.put("STOCKINFO_ID",STOCKINFO_PARENTID);
            parentStockInfo.put("STOCKINFO_NAME","股票涨跌幅树");
        }else{
            parentStockInfo = this.stockInfoService.getRecord("PLAT_BITTRADE_STOCKINFO",
                    new String[]{"STOCKINFO_ID"}, new Object[]{STOCKINFO_PARENTID});
        }
        if(stockInfo==null){
            stockInfo = new HashMap<String,Object>();
        }
        stockInfo.put("STOCKINFO_PARENTID",parentStockInfo.get("STOCKINFO_ID"));
        stockInfo.put("STOCKINFO_PARENTNAME",parentStockInfo.get("STOCKINFO_NAME"));
        request.setAttribute("stockInfo", stockInfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

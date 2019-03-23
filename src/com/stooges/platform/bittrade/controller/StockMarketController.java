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
import com.stooges.platform.bittrade.service.StockMarketService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 股票行情信息业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-19 15:12:30
 */
@Controller  
@RequestMapping("/bittrade/StockMarketController")  
public class StockMarketController extends BaseController {
    /**
     * 
     */
    @Resource
    private StockMarketService stockMarketService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除股票行情信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        stockMarketService.deleteRecords("PLAT_BITTRADE_STOCKMARKET","STOCKMARKET_ID",selectColValues.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改股票行情信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> stockMarket = PlatBeanUtil.getMapFromRequest(request);
        String STOCKMARKET_CODE = request.getParameter("STOCKMARKET_CODE");
        String beginDate = request.getParameter("STOCKMARKET_BDATE");
        String endDate = request.getParameter("STOCKMARKET_EDATE");
        stockMarketService.saveMarket(STOCKMARKET_CODE, beginDate, endDate);
        stockMarket.put("success", true);
        this.printObjectJsonString(stockMarket, response);
    }
    
    /**
     * 跳转到股票行情信息表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String STOCKMARKET_ID = request.getParameter("STOCKMARKET_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> stockMarket = null;
        if(StringUtils.isNotEmpty(STOCKMARKET_ID)){
            stockMarket = this.stockMarketService.getRecord("PLAT_BITTRADE_STOCKMARKET"
                    ,new String[]{"STOCKMARKET_ID"},new Object[]{STOCKMARKET_ID});
        }else{
            stockMarket = new HashMap<String,Object>();
        }
        request.setAttribute("stockMarket", stockMarket);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

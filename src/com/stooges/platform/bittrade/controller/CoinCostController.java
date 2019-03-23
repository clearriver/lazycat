/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
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
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.bittrade.service.CoinCostService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 币交易成本业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-04 17:01:22
 */
@Controller  
@RequestMapping("/bittrade/CoinCostController")  
public class CoinCostController extends BaseController {
    /**
     * 
     */
    @Resource
    private CoinCostService coinCostService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除币交易成本数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        coinCostService.deleteRecords("PLAT_BITTRADE_COINCOST","COINCOST_ID",selectColValues.split(","));
        sysLogService.saveBackLog("比特币交易管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的币交易成本", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改币交易成本数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> coinCost = PlatBeanUtil.getMapFromRequest(request);
        String COINCOST_ID = (String) coinCost.get("COINCOST_ID");
        if(StringUtils.isEmpty(COINCOST_ID)){
            coinCost.put("COINCOST_TIME",PlatDateTimeUtil.
                    formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        String symbol = request.getParameter("COINCOST_SYMBOL");
        Map<String,Double> price = coinCostService.getCoinNewestPrice(symbol);
        coinCost.putAll(price);
        coinCost = coinCostService.saveOrUpdate("PLAT_BITTRADE_COINCOST",
                coinCost,AllConstants.IDGENERATOR_UUID,null);
        coinCost.put("success", true);
        this.printObjectJsonString(coinCost, response);
    }
    
    /**
     * 跳转到币交易成本表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String COINCOST_ID = request.getParameter("COINCOST_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> coinCost = null;
        if(StringUtils.isNotEmpty(COINCOST_ID)){
            coinCost = this.coinCostService.getRecord("PLAT_BITTRADE_COINCOST"
                    ,new String[]{"COINCOST_ID"},new Object[]{COINCOST_ID});
        }else{
            coinCost = new HashMap<String,Object>();
        }
        request.setAttribute("coinCost", coinCost);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 跳转到币交易成本表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goTradeEdit")
    public ModelAndView goTradeEdit(HttpServletRequest request) {
        String COINCOST_ID = request.getParameter("COINCOST_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        request.setAttribute("COINCOST_ID", COINCOST_ID);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 保存币交易记录
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveCoinTrade")
    public void saveCoinTrade(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String COINCOST_ID = request.getParameter("COINCOST_ID");
        String COINCOST_TRADEJSON = request.getParameter("COINCOST_TRADEJSON");
        List<Map> tradeList = JSON.parseArray(COINCOST_TRADEJSON, Map.class);
        //获取旧数据
        Map<String,Object> oldCoinCost = coinCostService.getRecord("PLAT_BITTRADE_COINCOST", 
                new String[]{"COINCOST_ID"},new Object[]{COINCOST_ID});
        String symbol = (String) oldCoinCost.get("COINCOST_SYMBOL");
        Map<String,Double> newPrice = coinCostService.getCoinNewestPrice(symbol);
        if(newPrice.size()>1){
            oldCoinCost.putAll(newPrice);
            Double totalCost = 0.0;
            Double totalNum = 0.0;
            for(Map trade:tradeList){
                Double COIN_NUM = Double.parseDouble(trade.get("COIN_NUM").toString());
                Double COIN_PRICE = Double.parseDouble(trade.get("COIN_PRICE").toString());
                totalCost+=(COIN_NUM*COIN_PRICE);
                totalNum+=COIN_NUM;
            }
            //获取目前成本价格
            Double COINCOST_PRICE = totalCost/totalNum;
            //获取目前最新价格
            Double COINCOST_NEWPRICE = (Double) oldCoinCost.get("COINCOST_NEWPRICE");
            Double minus = COINCOST_NEWPRICE-COINCOST_PRICE;
            NumberFormat nFromat = NumberFormat.getPercentInstance();
            nFromat.setMinimumFractionDigits(2);
            nFromat.setGroupingUsed(false);
            //获取
            String COINCOST_RATE = nFromat.format(minus/COINCOST_PRICE);
            COINCOST_RATE = COINCOST_RATE.replace("%", "");
            oldCoinCost.put("COINCOST_RATE", COINCOST_RATE);
            oldCoinCost.put("COINCOST_PRICE", COINCOST_PRICE);
            oldCoinCost.put("COINCOST_TRADEJSON", COINCOST_TRADEJSON);
            coinCostService.saveOrUpdate("PLAT_BITTRADE_COINCOST",oldCoinCost,
                    AllConstants.IDGENERATOR_UUID, null);
            result.put("success", true);
        }else{
            oldCoinCost.put("COINCOST_TRADEJSON", COINCOST_TRADEJSON);
            coinCostService.saveOrUpdate("PLAT_BITTRADE_COINCOST",oldCoinCost,
                    AllConstants.IDGENERATOR_UUID, null);
            result.put("success", false);
        }
        this.printObjectJsonString(result, response);
    }
}

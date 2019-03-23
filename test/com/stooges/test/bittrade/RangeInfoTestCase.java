/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.bittrade;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.stooges.core.test.BaseTestCase;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.SSLClient;
import com.stooges.platform.bittrade.service.RangeInfoService;

/**
 * @author 胡裕
 *
 * 
 */
public class RangeInfoTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private RangeInfoService rangeInfoService;
    
    public static void main(String[] args){
        NumberFormat nFromat = NumberFormat.getPercentInstance();
        nFromat.setMinimumFractionDigits(2);
        nFromat.setGroupingUsed(false);
        String RANGEINFO_RANGE = nFromat.format(15.3125);
        System.out.println(RANGEINFO_RANGE);
    }
    
    /**
     * 
     */
    @Test
    public void initDateRangInfo(){
        List<String> symList = rangeInfoService.getSymbolList();
        for(String symbol:symList){
            rangeInfoService.initRangInfo("2017-12-14", symbol);
        }
    }
    
    /**
     * 
     */
    @Test
    public void initSymbolType(){
        String huobihost = PlatPropUtil.getPropertyValue("conf/config.properties","huobihost");
        String url = huobihost+"v1/common/symbols";
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        List<Map> list = (List<Map>) map.get("data");
        for(Map data:list){
            String basecurrency = (String) data.get("base-currency");
            String quotecurrency = (String) data.get("quote-currency");
            System.out.println(basecurrency+"/"+quotecurrency);
        }
    }
    
    /**
     * 
     */
    @Test
    public void initBitType(){
        String huobihost = PlatPropUtil.getPropertyValue("conf/config.properties","huobihost");
        String url = huobihost+"v1/common/symbols";
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        List<Map> list = (List<Map>) map.get("data");
        Set<String> setList = new HashSet<String>();
        for(Map data:list){
            String basecurrency = (String) data.get("base-currency");
            String quotecurrency = (String) data.get("quote-currency");
            setList.add(basecurrency);
            setList.add(quotecurrency);
        }
        int sn = 1;
        for(String code:setList){
            Map<String,Object> dic = new HashMap<String,Object>();
            dic.put("DIC_NAME", code);
            dic.put("DIC_VALUE", code);
            dic.put("DIC_SN", sn);
            dic.put("DIC_DICTYPE_CODE","bittype");
            rangeInfoService.saveOrUpdate("PLAT_SYSTEM_DICTIONARY",dic,
                    AllConstants.IDGENERATOR_UUID,null);
            sn++;
        }
    }
    
    /**
     * 
     */
    @Test
    public void initRangInfo(){
        String beginDate = "2017-12-10";
        String endDate = "2017-12-14";
        String symbolBegin = "smt";
        String symbolEnd = "btc";
        rangeInfoService.initRangInfo(beginDate, endDate, symbolBegin, symbolEnd);
    }
    
    /**
     * 
     */
    @Test
    public void getNowPrice(){
         System.out.println(rangeInfoService.getNowPrice("xrpusdt"));
    }
}

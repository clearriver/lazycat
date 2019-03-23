/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.bittrade;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.stooges.core.test.BaseTestCase;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatFileUtil;
import com.stooges.platform.bittrade.service.StockMarketService;

/**
 * @author 胡裕
 *
 * 
 */
public class StockMarketServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private StockMarketService stockMarketService;
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args){
        String json = PlatFileUtil.readFileString("d:/1.txt");
        Map<String,Object> price = JSON.parseArray(json, Map.class).get(0);
        System.out.println(price.toString());
    }
    
    /**
     * 
     */
    @Test
    public void getMarket(){
        System.out.println(stockMarketService.getMarket("gb_aln","20180119"));
    }
}

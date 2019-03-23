/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.weixin;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.bittrade.service.CoinCostService;

/**
 * @author 胡裕
 *
 * 
 */
public class CoinCostServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private CoinCostService coinCostService;
    
    /**
     * 
     */
    @Test
    public void getCoinNewestPrice(){
        Map<String,Double> result = coinCostService.getCoinNewestPrice("mdsbtc");
        System.out.println(JSON.toJSONString(result));
    }
}

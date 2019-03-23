/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.bittrade;

import javax.annotation.Resource;

import org.junit.Test;

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
    public void updateCostInfo(){
        coinCostService.updateCostInfo();
    }
    
}

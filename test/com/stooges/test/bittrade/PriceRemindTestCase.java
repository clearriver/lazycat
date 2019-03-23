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
import com.stooges.platform.bittrade.service.PriceRemindService;

/**
 * @author 胡裕
 *
 * 
 */
public class PriceRemindTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private PriceRemindService priceRemindService;
    
    /**
     * 
     */
    @Test
    public void getInfo(){
        Map<String,Object> priceRemind = priceRemindService.
                getRecord("PLAT_BITTRADE_PRICEREMIND",
                        new String[]{"PRICEREMIND_ID"},
                        new Object[]{"402882a1607169b7016071700dfb000d"});
        System.out.println(JSON.toJSONString(priceRemind));
    }
    
    
    /**
     * 
     */
    @Test
    public void remindPrice(){
        priceRemindService.remindPrice();
    }
}

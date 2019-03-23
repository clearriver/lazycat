/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.bittrade;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.bittrade.service.HuoAccountService;

/**
 * @author 胡裕
 *
 * 
 */
public class HuoAccountTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private HuoAccountService huoAccountService;
    
    /**
     * 
     */
    @Test
    public void findAccountsByCode(){
        List<Map> list = huoAccountService.findAccountsByCode("zhangjiamin");
        System.out.println(list.toString());
    }
    
    /**
     * 
     */
    @Test
    public void findOrderList(){
        //List<Map> list = huoAccountService.findOrderList("huyu","xrpusdt");
        //System.out.println(JSON.toJSONString(list));
        List<Map> list = huoAccountService.findOrderList("huyu", "xrpusdt", "buy-limit");
        System.out.println(JSON.toJSONString(list));
    }
    
    /**
     * 
     */
    @Test
    public void revokeOneOrder(){
        huoAccountService.revokeOneOrder("huyu","359991797");
    }
    
    /**
     * 
     */
    @Test
    public void getAccountId(){
        String accountId = huoAccountService.getAccountId("huyu");
        System.out.println(accountId);
    }
    
    /**
     * 
     */
    @Test
    public void createOrder(){
        huoAccountService.createOrder("313","0.4687","xrpusdt","buy-limit","huyu");
    }
    
}


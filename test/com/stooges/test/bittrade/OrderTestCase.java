/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.bittrade;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.bittrade.service.OrderService;
import com.stooges.platform.bittrade.util.Order;

/**
 * @author 胡裕
 *
 * 
 */
public class OrderTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private OrderService orderService;

    /**
     * 
     */
    @Test
    public void exeTrade(){
        orderService.exeTrade();
    }
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args){
        double m1 = 0.8426;
        double m2 = 0.8426000000000001;
        String s1 = String.valueOf(m1);
        String s2 = String.valueOf(m2);
        if(s2.contains(s1)){
            System.out.println("包含..");
        }else{
            System.out.println("不");
        }
    }
}

/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.demo;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.demo.service.LeaveInfoService;

/**
 * @author 胡裕
 *
 * 
 */
public class LeaveInfoServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private LeaveInfoService leaveInfoService;
    
    /**
     * 
     */
    @Test
    public void testDbChange(){
        leaveInfoService.testNativeDb();
        leaveInfoService.testMysql();
    }
    
    /**
     * 
     */
    @Test
    public void testDbChange2(){
        leaveInfoService.testOracle();
        leaveInfoService.testNativeDb();
    }
}

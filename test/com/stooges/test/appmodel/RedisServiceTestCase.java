/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.appmodel;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.appmodel.service.RedisService;

/**
 * @author 胡裕
 *
 * 
 */
public class RedisServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private RedisService redisService;
    
    /**
     * 
     */
    @Test
    public void getKeySetByKeyPattern(){
        System.out.println(redisService.getKeySetByKeyPattern("001_").toString());
    }
    
    /**
     * 
     */
    @Test
    public void deleteByLikeKeyName(){
        redisService.deleteByLikeKeyName("001_");
    }
}

/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.webstatistics;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.webstatistics.service.DomainService;

/**
 * @author 胡裕
 *
 * 
 */
public class DomainServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private DomainService domainService;
    
    /**
     * 
     */
    @Test
    public void saveDomainData(){
        domainService.saveDomainData("2018-03-21", "4028d081624d011001624d050eab00ae");
    }
}

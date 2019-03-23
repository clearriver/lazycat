/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.workflow;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.workflow.service.ExecutionService;

/**
 * @author 胡裕
 *
 * 
 */
public class ExecutionTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private ExecutionService executionService;
    
    /**
     * 
     */
    @Test
    public void deleteExeById(){
        executionService.deleteExeById("FJFDA1712261451380109");
    }
}

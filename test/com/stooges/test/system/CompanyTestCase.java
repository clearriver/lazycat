/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.system;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.core.util.AllConstants;
import com.stooges.platform.system.service.CompanyService;

/**
 * @author 胡裕
 *
 * 
 */
public class CompanyTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private CompanyService companyService;
    
    /**
     * 
     */
    @Test
    public void testSave(){
        Map<String,Object> company = new HashMap<String,Object>();
        company.put("COMPANY_ID","1");
        company.put("COMPANY_NAME","测试");
        company.put("COMPANY_PARENTID","402848a55b556eb9015b55968e8e002c");
        companyService.saveOrUpdateTreeData("PLAT_SYSTEM_COMPANY",company,
                AllConstants.IDGENERATOR_ASSIGNED, null);
    }
}

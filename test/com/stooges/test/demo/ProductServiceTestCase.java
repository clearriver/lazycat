/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.demo;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.demo.dao.ProductDao;
import com.stooges.platform.demo.service.ProductService;

/**
 * @author 胡裕
 *
 * 
 */
public class ProductServiceTestCase extends BaseTestCase{
    /**
     * 
     */
    @Resource
    private ProductService productService;
    /**
     * 
     */
    @Resource
    private ProductDao productDao;
    
    /**
     * 
     */
    @Test
    public void testGet(){
        Map<String,Object> product = productService.
                getRecord("plat_demo_product",
                        new String[]{"PRODUCT_ID"},new Object[]{"2c90ef1a616f1eca01616f22a0ef00b0"});
        System.out.println(product.toString());
    }
    
    /**
     * 
     */
    @Test
    public void findPrimaryKeyNames(){
        List<String> pkName = productService.findPrimaryKeyNames("PLAT_SYSTEM_SYSLOG");
        System.out.println(pkName);
    }
    
    @Test
    public void testList(){
        productDao.testList();
    }
    
 
}

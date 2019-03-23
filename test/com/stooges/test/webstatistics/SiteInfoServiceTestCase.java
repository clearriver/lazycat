/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.webstatistics;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.stooges.core.test.BaseTestCase;
import com.stooges.core.util.PlatJsonUtil;
import com.stooges.platform.webstatistics.service.SiteInfoService;

/**
 * @author 胡裕
 *
 * 
 */
public class SiteInfoServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private SiteInfoService siteInfoService;
    
    /**
     * 
     */
    @Test
    public void getDayTrendResult(){
        System.out.println(siteInfoService.getDayTrendResult("2018-03-22",
                "4028d081624d011001624d050eab00ae"));
    }
    
    /**
     * 
     */
    @Test
    public void getDomainTrendResult(){
        System.out.println(siteInfoService.getDomainTrendResult("2018-03-21",
                "4028d081624d011001624d050eab00ae"));
    }
    
    @Test
    public void getInviewTrendResult(){
        System.out.println(siteInfoService.getInviewTrendResult("2018-03-22",
                "4028d081624d011001624d050eab00ae"));
    }
    
    @Test
    public void getNewOldTrendResult(){
        System.out.println(siteInfoService.getNewOldTrendResult("2018-03-22",
                "4028d081624d011001624d050eab00ae"));
    }
    
    @Test
    public void getMapTrendResult(){
        System.out.println(siteInfoService.getMapTrendResult("2018-03-22",
                "4028d081624d011001624d050eab00ae"));
    }
    
    @Test
    public void saveSiteMapTrendInfo(){
        siteInfoService.saveSiteMapTrendInfo("2018-03-23");
    }
    
    /**
     * 
     */
    @Test
    public void testSearch(){
        Map<String,Object> siteInfo = siteInfoService.getRecord("PLAT_WEBSTATISTICS_SITEINFO",
                new String[]{"SITEINFO_ID"},new Object[]{"4028d081624d011001624d050eab00ae"});
        String SITEINFO_PAGEMAP = (String) siteInfo.get("SITEINFO_PAGEMAP");
        Map data = PlatJsonUtil.getUniqueData("PAGE_URL","http://www.stoogessoft.com1/"
                ,SITEINFO_PAGEMAP);
        if(data!=null){
            System.out.println(data.toString());
        }
        
    }
}

/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.platform.webstatistics.dao.DomainDao;
import com.stooges.platform.webstatistics.service.DomainService;
import com.stooges.platform.webstatistics.service.SiteInfoService;
import com.stooges.platform.webstatistics.util.TrafficStatisticsUrl;

/**
 * 描述 访问来源业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 11:36:14
 */
@Service("domainService")
public class DomainServiceImpl extends BaseServiceImpl implements DomainService {

    /**
     * 所引入的dao
     */
    @Resource
    private DomainDao dao;
    /**
     * 
     */
    @Resource
    private SiteInfoService siteInfoService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

    /**
     * 描述
     * @author 李俊
     * @created 2017年7月27日 上午11:37:42
     * @param date
     */
    @Override
    public void saveDomainData(String date,String staticSiteId) {
        String result = siteInfoService.getDomainTrendResult(date, staticSiteId);
        if(result!=null){
            StringBuffer timesql = new StringBuffer("");
            timesql.append("DELETE FROM PLAT_WEBSTATISTICS_DOMAIN  WHERE DOMAIN_DAY=? ");
            timesql.append(" AND STATIC_SITEID=? ");
            dao.executeSql(timesql.toString(), new Object[]{date,staticSiteId});
            Map<String,Object> resultMap = (Map)JSON.parseObject(result, Map.class).get("data");
            Map<String,Object> inputList = (Map)resultMap.get("inputList");
            Map<String,Object> inputListItems = (Map)inputList.get("items");
            for (String key : inputListItems.keySet()) {
                if(key.equals("直接输入网址或书签")){
                    Map<String,Object> summaryItems = (Map)inputListItems.get(key);
                    Map<String,Object> domain = new HashMap<String,Object>();
                    domain.put("DOMAIN_DAY",date);
                    domain.put("DOMAIN_PV",Integer.parseInt(summaryItems.get("pv").toString()));
                    domain.put("DOMAIN_UV",Integer.parseInt(summaryItems.get("uv").toString()));
                    domain.put("DOMAIN_IP",Integer.parseInt(summaryItems.get("ip").toString()));
                    domain.put("DOMAIN_NEWUV",Integer.parseInt(summaryItems.get("newuv").toString()));
                    //domain.put("DOMAIN_SESSION",Integer.parseInt(summaryItems.get("session").toString()));
                    domain.put("DOMAIN_EACHPV",summaryItems.get("averageupv"));
                    domain.put("DOMAIN_ACCESSDEPTH",summaryItems.get("averagepv"));
                    domain.put("DOMAIN_TYPE","2");
                    domain.put("STATIC_SITEID",staticSiteId);
                    this.saveOrUpdate("PLAT_WEBSTATISTICS_DOMAIN",
                            domain,AllConstants.IDGENERATOR_UUID,null);
                }
           }
            
            if(!resultMap.get("refererDomainList").equals("")){
                Map<String,Object> refererDomainList = (Map)resultMap.get("refererDomainList");
                List<Map<String,Object>> refererDomainListItems = (List)refererDomainList.get("items");
                for (int i = 0; i < refererDomainListItems.size(); i++) {
                    Map<String,Object> summaryItems = (Map)refererDomainListItems.get(i);
                    Map<String,Object> domain = new HashMap<String,Object>();
                    domain.put("DOMAIN_DAY",date);
                    domain.put("DOMAIN_PV",Integer.parseInt(summaryItems.get("pv").toString()));
                    domain.put("DOMAIN_UV",Integer.parseInt(summaryItems.get("uv").toString()));
                    domain.put("DOMAIN_IP",Integer.parseInt(summaryItems.get("ip").toString()));
                    domain.put("DOMAIN_NEWUV",Integer.parseInt(summaryItems.get("newuv").toString()));
                    //domain.put("DOMAIN_SESSION",Integer.parseInt(summaryItems.get("session").toString()));
                    domain.put("DOMAIN_EACHPV",summaryItems.get("averageupv"));
                    domain.put("DOMAIN_ACCESSDEPTH",summaryItems.get("averagepv"));
                    domain.put("DOMAIN_LINK",summaryItems.get("link"));
                    domain.put("DOMAIN_KEY",summaryItems.get("key"));
                    domain.put("DOMAIN_TYPE","1");
                    domain.put("STATIC_SITEID",staticSiteId);
                    this.saveOrUpdate("PLAT_WEBSTATISTICS_DOMAIN",
                            domain,AllConstants.IDGENERATOR_UUID,null);
                }
            }
            
           
        }
        
    }
  
}

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
import com.stooges.platform.webstatistics.dao.InviewDao;
import com.stooges.platform.webstatistics.service.InviewService;
import com.stooges.platform.webstatistics.service.SiteInfoService;
import com.stooges.platform.webstatistics.util.TrafficStatisticsUrl;

/**
 * 描述 入口数据业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 17:50:00
 */
@Service("inviewService")
public class InviewServiceImpl extends BaseServiceImpl implements InviewService {

    /**
     * 所引入的dao
     */
    @Resource
    private InviewDao dao;
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
     * @created 2017年7月27日 下午5:53:49
     * @param date
     */
    @Override
    public void saveInviewData(String date,String staticSiteId) {
        String result = siteInfoService.getInviewTrendResult(date, staticSiteId);
        if(result!=null){
            StringBuffer timesql = new StringBuffer("");
            timesql.append("DELETE  FROM PLAT_WEBSTATISTICS_INVIEW  WHERE INVIEW_DAY=? ");
            timesql.append(" AND STATIC_SITEID=? ");
            dao.executeSql(timesql.toString(), new Object[]{date,staticSiteId});
            Map<String,Object> resultMap = (Map)JSON.parseObject(result, Map.class).get("data");
            if(!resultMap.get("statistics").toString().equals("")){
                Map<String,Object> statistics = (Map)resultMap.get("statistics");
                List<Map<String,Object>> statisticsItems = (List)statistics.get("items");
                if(statisticsItems!=null){
                    for (int i = 0; i < statisticsItems.size(); i++) {
                        Map<String,Object> inview = new HashMap<String,Object>();
                        inview.put("INVIEW_DAY",date);
                        inview.put("INVIEW_PV", Integer.parseInt(statisticsItems.get(i).get("pv").toString()));
                        inview.put("INVIEW_OUTPRO", statisticsItems.get(i).get("outpro"));
                        inview.put("INVIEW_ACCESSDEPTH", statisticsItems.get(i).get("averagepv"));
                        inview.put("INVIEW_INPUT", statisticsItems.get(i).get("input"));
                        inview.put("INVIEW_AVERAGESTIME", statisticsItems.get(i).get("averagestime"));
                        inview.put("INVIEW_SOURCE", statisticsItems.get(i).get("source"));
                        inview.put("STATIC_SITEID",staticSiteId);
                        this.saveOrUpdate("PLAT_WEBSTATISTICS_INVIEW",
                                inview,AllConstants.IDGENERATOR_UUID,null);
                    }
                }
            }
        }
    }
  
}

/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.platform.webstatistics.dao.NewOldDao;
import com.stooges.platform.webstatistics.service.NewOldService;
import com.stooges.platform.webstatistics.service.SiteInfoService;
import com.stooges.platform.webstatistics.util.TrafficStatisticsUrl;

/**
 * 描述 新建用户对比业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 20:17:53
 */
@Service("newOldService")
public class NewOldServiceImpl extends BaseServiceImpl implements NewOldService {

    /**
     * 所引入的dao
     */
    @Resource
    private NewOldDao dao;
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
     * @created 2017年7月27日 下午8:18:41
     * @param date
     */
    @Override
    public void saveNewOldData(String date,String staticSiteId) {
        String result = siteInfoService.getNewOldTrendResult(date, staticSiteId);
        if(result!=null){
            StringBuffer timesql = new StringBuffer("");
            timesql.append("DELETE FROM PLAT_WEBSTATISTICS_NEWOLD  WHERE NEWOLD_DAY=? ");
            timesql.append(" AND STATIC_SITEID=? ");
            dao.executeSql(timesql.toString(), new Object[]{date,staticSiteId});
            Map<String,Object> resultMap = (Map)JSON.parseObject(result, Map.class).get("data");
            Map<String,Object> newOldList = (Map)resultMap.get("newOldList");
            List<Map<String,Object>> newOldListItems = (List)newOldList.get("items");
            if(newOldListItems!=null){
                for (int i = 0; i < newOldListItems.size(); i++) {
                    Map<String,Object> newOld = new HashMap<String,Object>();
                    newOld.put("NEWOLD_DAY", date);
                    newOld.put("NEWOLD_PV", newOldListItems.get(i).get("pv"));
                    newOld.put("NEWOLD_UV", newOldListItems.get(i).get("uv"));
                    newOld.put("NEWOLD_SESSION", newOldListItems.get(i).get("session"));
                    newOld.put("NEWOLD_TYPE", (i+1)+"");
                    newOld.put("STATIC_SITEID",staticSiteId);
                    this.saveOrUpdate("PLAT_WEBSTATISTICS_NEWOLD",
                            newOld,AllConstants.IDGENERATOR_UUID,null);
                }
            }
        }
        
    }

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月10日 下午8:38:28
     * @param startDay
     * @param endDay
     * @param type
     * @return
     */
    @Override
    public Map<String, Object> getNewOldData(String startDay, String endDay,
            String type) {
        String name = "";
        List<Map<String,Object>>  data = new ArrayList<Map<String,Object>>();
        StringBuffer sql = new StringBuffer("");
        String dbType = PlatAppUtil.getDbType();
        String isNullFn = null;
        if(dbType.equals("ORACLE")){
            isNullFn = "NVL";
        }else if(dbType.equals("MYSQL")){
            isNullFn = "IFNULL";
        }
        if(type.equals("1")){
            sql.append(" select ").append(isNullFn).append("(SUM(T.NEWOLD_PV),0) AS ALLNUM ");
            name =  "浏览次数(PV)";
        }else if(type.equals("2")){
            sql.append(" select ").append(isNullFn).append("(SUM(T.NEWOLD_UV),0) AS ALLNUM ");
            name =  "独立访客(UV)";
        }
        sql.append("  from PLAT_WEBSTATISTICS_NEWOLD t ");
        sql.append("  WHERE T.NEWOLD_DAY>=? AND T.NEWOLD_DAY<=? ");
        sql.append("  AND T.NEWOLD_TYPE=? ");
        Map<String,Object> newMap = dao.getBySql(sql.toString(), new Object[]{startDay,endDay,"1"});
        Map<String,Object> oldMap = dao.getBySql(sql.toString(), new Object[]{startDay,endDay,"2"});
        if(newMap!=null){
            Map<String,Object> dataMap = new HashMap<String,Object>();
            dataMap.put("name", "新用户");
            dataMap.put("value",Integer.parseInt(newMap.get("ALLNUM").toString()));
            data.add(dataMap);
        }
        if(oldMap!=null){
            Map<String,Object> dataMap = new HashMap<String,Object>();
            dataMap.put("name", "旧用户");
            dataMap.put("value",Integer.parseInt(oldMap.get("ALLNUM").toString()));
            data.add(dataMap);
        }
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("data", data);
        map.put("name", name);
        return map;
    }
  
}

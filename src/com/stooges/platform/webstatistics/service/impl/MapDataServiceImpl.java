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

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.platform.webstatistics.dao.MapDataDao;
import com.stooges.platform.webstatistics.service.MapDataService;
import com.stooges.platform.webstatistics.service.SiteInfoService;
import com.stooges.platform.webstatistics.util.TrafficStatisticsUrl;

/**
 * 描述 地区数据业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 20:42:05
 */
@Service("mapDataService")
public class MapDataServiceImpl extends BaseServiceImpl implements MapDataService {

    /**
     * 所引入的dao
     */
    @Resource
    private MapDataDao dao;
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
     * 描述 获取地区数据
     * @author 李俊
     * @created 2017年7月27日 下午8:43:16
     * @param dAYTREND_DAY
     */
    @Override
    public void saveMapData(String date,String staticSiteId) {
        Map<String,String> result = siteInfoService.getMapTrendResult(date, staticSiteId);
        String resultpv = result.get("resultpv");
        String resultuv = result.get("resultuv");
        String resultip = result.get("resultip");
        String resultnewuv = result.get("resultnewuv");
        String resultsession = result.get("resultsession");
        if(StringUtils.isNotEmpty(resultpv)){
            StringBuffer timesql = new StringBuffer("");
            timesql.append("DELETE FROM PLAT_WEBSTATISTICS_MAPDATA  WHERE MAPDATA_DAY=? ");
            timesql.append(" AND STATIC_SITEID=? ");
            dao.executeSql(timesql.toString(), new Object[]{date,staticSiteId});
            Map<String,Object> resultpvMap = (Map)JSON.parseObject(resultpv, Map.class).get("data");
            List<Map<String,Object>> resultpvItems = (List)resultpvMap.get("mapData");
            Map<String,Object> resultuvMap = (Map)JSON.parseObject(resultuv, Map.class).get("data");
            List<Map<String,Object>> resultuvItems = (List)resultuvMap.get("mapData");
            Map<String,Object> resultipMap = (Map)JSON.parseObject(resultip, Map.class).get("data");
            List<Map<String,Object>> resultipItems = (List)resultipMap.get("mapData");
            Map<String,Object> resultnewuvMap = (Map)JSON.parseObject(resultnewuv, Map.class).get("data");
            List<Map<String,Object>> resultnewuvItems = (List)resultnewuvMap.get("mapData");
            Map<String,Object> resultsessionMap = (Map)JSON.parseObject(resultsession, Map.class).get("data");
            List<Map<String,Object>> resultsessionItems = (List)resultsessionMap.get("mapData");
            for (int i = 0; i < resultpvItems.size(); i++) {
                Map<String,Object> mapData = new HashMap<String,Object>();
                mapData.put("MAPDATA_DAY", date);
                mapData.put("MAPDATA_NAME", resultpvItems.get(i).get("name"));
                mapData.put("MAPDATA_CODE", resultpvItems.get(i).get("cha"));
                mapData.put("MAPDATA_PV", Integer.parseInt(resultpvItems.get(i).get("des").toString()));
                mapData.put("MAPDATA_UV", Integer.parseInt(resultuvItems.get(i).get("des").toString()));
                mapData.put("MAPDATA_IP", Integer.parseInt(resultipItems.get(i).get("des").toString()));
                mapData.put("MAPDATA_NEWUV", Integer.parseInt(resultnewuvItems.get(i).get("des").toString()));
                mapData.put("MAPDATA_SESSION", Integer.parseInt(resultsessionItems.get(i).get("des").toString()));
                mapData.put("STATIC_SITEID",staticSiteId);
                this.saveOrUpdate("PLAT_WEBSTATISTICS_MAPDATA",
                        mapData,AllConstants.IDGENERATOR_UUID,null);
            }
            
        }
        
        
    }

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月10日 下午9:16:04
     * @param startDay
     * @param endDay
     * @return
     */
    @Override
    public Map<String, Object> getMapData(String startDay, String endDay,String siteId) {
        StringBuffer sql = new StringBuffer("");
        sql.append(" select t.mapdata_name,sum(t.mapdata_pv) AS PV,sum(t.mapdata_uv) AS UV,");
        sql.append("sum(t.mapdata_ip) AS IP,sum(t.mapdata_newuv) AS NEWUV ");
        sql.append("  from PLAT_WEBSTATISTICS_MAPDATA t  ");
        sql.append(" where t.mapdata_day>=? and  t.mapdata_day<=? ");
        sql.append(" AND T.STATIC_SITEID=? ");
        sql.append(" group by t.mapdata_name ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(),
                new Object[]{startDay,endDay,siteId}, null);
        Map<String, Object> map = new HashMap<String,Object>();
        List<Map<String,Object>>  pvdata = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>>  uvdata = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>>  ipdata = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>>  newuvdata = new ArrayList<Map<String,Object>>();
        int maxnum = 0;
        if(list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> e1 = new HashMap<String,Object>();
                Map<String, Object> e2 = new HashMap<String,Object>();
                Map<String, Object> e3 = new HashMap<String,Object>();
                Map<String, Object> e4 = new HashMap<String,Object>();
                String mapdata_name = (String)list.get(i).get("MAPDATA_NAME");
                if(mapdata_name.equals("内蒙古自治区")){
                    mapdata_name = "内蒙古";
                }else if(mapdata_name.equals("黑龙江省")){
                    mapdata_name = "黑龙江";
                }else{
                    mapdata_name = mapdata_name.substring(0, 2);
                }
                e1.put("name", mapdata_name);
                e1.put("value", Integer.parseInt(list.get(i).get("PV").toString()));
                e2.put("name", mapdata_name);
                e2.put("value", Integer.parseInt(list.get(i).get("UV").toString()));
                e3.put("name", mapdata_name);
                e3.put("value", Integer.parseInt(list.get(i).get("IP").toString()));
                e4.put("name", mapdata_name);
                e4.put("value", Integer.parseInt(list.get(i).get("NEWUV").toString()));
                int m = Integer.parseInt(list.get(i).get("PV").toString())
                        +Integer.parseInt(list.get(i).get("UV").toString())
                        +Integer.parseInt(list.get(i).get("IP").toString())
                        +Integer.parseInt(list.get(i).get("NEWUV").toString());
                if(m>=maxnum){
                  maxnum = m;  
                }
                pvdata.add(e1);
                uvdata.add(e2);
                ipdata.add(e3);
                newuvdata.add(e4);
            }
        }
        map.put("pvdata", pvdata);
        map.put("uvdata", uvdata);
        map.put("ipdata", ipdata);
        map.put("newuvdata", newuvdata);
        map.put("maxnum", maxnum);
        return map;
    }
  
}

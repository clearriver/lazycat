/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.core.util.PlatJsonUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.webstatistics.dao.DayTrendDao;
import com.stooges.platform.webstatistics.service.DayTrendService;
import com.stooges.platform.webstatistics.service.SiteInfoService;
import com.stooges.platform.webstatistics.util.TrafficStatisticsUrl;

/**
 * 描述 日期趋势业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2017-07-26 21:59:29
 */
@Service("dayTrendService")
public class DayTrendServiceImpl extends BaseServiceImpl implements DayTrendService {

    /**
     * 所引入的dao
     */
    @Resource
    private DayTrendDao dao;
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
     * @created 2017年7月26日 下午10:05:33
     * @param date
     */
    @Override
    public void saveTrendData(String date,String staticSiteId) {
        String result = siteInfoService.getDayTrendResult(date, staticSiteId);
        if(result!=null){
            Map<String,Object> resultMap = (Map)JSON.parseObject(result, Map.class).get("data"); 
            Map<String,Object> summary = (Map)resultMap.get("summary");
            Map<String,Object> summaryItems = (Map)summary.get("items");
            Map<String,Object> dayTrend =  dao.getRecord("PLAT_WEBSTATISTICS_DAYTREND",
                    new String[]{"DAYTREND_DAY","STATIC_SITEID"},new Object[]{date,staticSiteId});
            if(dayTrend==null){
                dayTrend = new HashMap<String,Object>();
            }
            dayTrend.put("DAYTREND_DAY",date);
            dayTrend.put("DAYTREND_PV",Integer.parseInt(summaryItems.get("pv").toString()));
            dayTrend.put("DAYTREND_UV",Integer.parseInt(summaryItems.get("uv").toString()));
            dayTrend.put("DAYTREND_IP",Integer.parseInt(summaryItems.get("ip").toString()));
            dayTrend.put("DAYTREND_NEWUV",Integer.parseInt(summaryItems.get("newuv").toString()));
            dayTrend.put("DAYTREND_SESSION",Integer.parseInt(summaryItems.get("session").toString()));
            dayTrend.put("DAYTREND_EACHPV",summaryItems.get("averageupv"));
            dayTrend.put("DAYTREND_ACCESSDEPTH",summaryItems.get("averagepv"));
            dayTrend.put("STATIC_SITEID",staticSiteId);
            dayTrend = dao.saveOrUpdate("PLAT_WEBSTATISTICS_DAYTREND",
                    dayTrend,AllConstants.IDGENERATOR_UUID,null);
            Map<String,Object> fluxList = (Map)resultMap.get("fluxList");
            List<Map<String,Object>> fluxListItems = (List)fluxList.get("items");
            StringBuffer timesql = new StringBuffer("");
            timesql.append("DELETE FROM PLAT_WEBSTATISTICS_TIMETREND  WHERE TIMETREND_DAY=? ");
            timesql.append("AND STATIC_SITEID=? ");
            dao.executeSql(timesql.toString(), new Object[]{date,staticSiteId});
            for (int i = 0; i < fluxListItems.size(); i++) {
                Map<String,Object> timeTrend = new HashMap<String,Object>();
                String pv = fluxListItems.get(i).get("pv").toString();
                String uv = fluxListItems.get(i).get("uv").toString();
                String ip = fluxListItems.get(i).get("ip").toString();
                String newuv = fluxListItems.get(i).get("newuv").toString();
                String session = fluxListItems.get(i).get("session").toString();
                String averageupv = fluxListItems.get(i).get("averageupv").toString();
                String averagepv = fluxListItems.get(i).get("averagepv").toString();
                if(pv.equals("-")){
                    timeTrend.put("TIMETREND_PV", 0);
                }else{
                    timeTrend.put("TIMETREND_PV", Integer.parseInt(pv));
                }
                if(uv.equals("-")){
                    timeTrend.put("TIMETREND_UV", 0);
                }else{
                    timeTrend.put("TIMETREND_UV", Integer.parseInt(uv));
                }
                if(ip.equals("-")){
                    timeTrend.put("TIMETREND_IP", 0);
                }else{
                    timeTrend.put("TIMETREND_IP", Integer.parseInt(ip));
                }
                if(newuv.equals("-")){
                    timeTrend.put("TIMETREND_NEWUV", 0);
                }else{
                    timeTrend.put("TIMETREND_NEWUV", Integer.parseInt(newuv));
                }
                if(session.equals("-")){
                    timeTrend.put("TIMETREND_SESSION", 0);
                }else{
                    timeTrend.put("TIMETREND_SESSION", Integer.parseInt(session));
                }
                if(averageupv.equals("-")){
                    timeTrend.put("TIMETREND_EACHPV", 0);
                }else{
                    timeTrend.put("TIMETREND_EACHPV", averageupv);
                }
                if(averagepv.equals("-")){
                    timeTrend.put("TIMETREND_ACCESSDEPTH", 0);
                }else{
                    timeTrend.put("TIMETREND_ACCESSDEPTH", averagepv);
                }
                timeTrend.put("TIMETREND_DAY", date);
                timeTrend.put("TIMETREND_TIME", fluxListItems.get(i).get("key").toString());
                timeTrend.put("TIMETREND_SN", i);
                timeTrend.put("STATIC_SITEID",staticSiteId);
                dayTrend = dao.saveOrUpdate("PLAT_WEBSTATISTICS_TIMETREND",
                        timeTrend,AllConstants.IDGENERATOR_UUID,null);
            }
            
        }
        
    }

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月8日 下午7:42:37
     * @param searchDay
     * @return
     */
    @Override
    public Map<String, Object> getTimeTrendData(String searchDay,String siteId) {
        List<String>  title = new ArrayList<String>();
        List<Integer> pvData = new ArrayList<Integer>();
        List<Integer> uvData = new ArrayList<Integer>();
        List<Integer> ipData = new ArrayList<Integer>();
        StringBuffer sql = new StringBuffer("");
        sql.append("SELECT * FROM PLAT_WEBSTATISTICS_TIMETREND T ");
        sql.append(" where t.timetrend_day=? AND STATIC_SITEID=? ");
        sql.append(" order by t.timetrend_sn asc ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), 
                new Object[]{searchDay,siteId}, null);
        if(list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                title.add(list.get(i).get("TIMETREND_TIME").toString()); 
                pvData.add(Integer.parseInt(list.get(i).get("TIMETREND_PV").toString()));
                uvData.add(Integer.parseInt(list.get(i).get("TIMETREND_UV").toString()));
                ipData.add(Integer.parseInt(list.get(i).get("TIMETREND_IP").toString()));
            }
        }else{
            for (int i = 0; i < 24; i++) {
                String startStr = PlatStringUtil.getFormatNumber(2, i+"");
                title.add(startStr+":00-"+startStr+":59"); 
                pvData.add(0);
                uvData.add(0);
                ipData.add(0);
            }
            
        }
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("title", title.toArray());
        map.put("pvData", pvData.toArray());
        map.put("uvData", uvData.toArray());
        map.put("ipData", ipData.toArray());
        return map;
    }

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月8日 下午9:40:06
     * @param startDay
     * @param endDay
     * @return
     */
    @Override
    public Map<String, Object> getDayTrendData(String startDay, String endDay,String siteId) {
        List<String>  title = new ArrayList<String>();
        List<Integer> pvData = new ArrayList<Integer>();
        List<Integer> uvData = new ArrayList<Integer>();
        List<Integer> ipData = new ArrayList<Integer>();
        StringBuffer sql = new StringBuffer("");
        sql.append(" select t.* from PLAT_WEBSTATISTICS_DAYTREND t  ");
        sql.append(" where t.daytrend_day<=? and t.daytrend_day>=? ");
        sql.append(" AND T.STATIC_SITEID=? ");
        sql.append(" order by t.daytrend_day asc ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), 
                new Object[]{endDay,startDay,siteId}, null);
        if(list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                title.add(list.get(i).get("DAYTREND_DAY").toString()); 
                pvData.add(Integer.parseInt(list.get(i).get("DAYTREND_PV").toString()));
                uvData.add(Integer.parseInt(list.get(i).get("DAYTREND_UV").toString()));
                ipData.add(Integer.parseInt(list.get(i).get("DAYTREND_IP").toString()));
            }
        }else{
            
        }
        Map<String, Object> map = new HashMap<String,Object>();
        map.put("title", title.toArray());
        map.put("pvData", pvData.toArray());
        map.put("uvData", uvData.toArray());
        map.put("ipData", ipData.toArray());
        return map;
    }

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月9日 下午8:35:40
     * @param startDay
     * @param endDay
     * @param type
     * @return
     */
    @Override
    public Map<String, Object> getTopSourceData(String startDay, String endDay,
            String type,String siteId) {
        List<String>  title = new ArrayList<String>();
        List<Integer> pvData = new ArrayList<Integer>();
        String name = "";
        StringBuffer sql = new StringBuffer("");
        sql.append(" SELECT * FROM ( ");
        if(type.equals("1")){
            sql.append(" select t.domain_key,sum(t.DOMAIN_PV) ");
            name =  "浏览次数(PV)";
        }else if(type.equals("2")){
            sql.append(" select t.domain_key,sum(t.DOMAIN_UV) ");
            name =  "独立访客(UV)";
        }else if(type.equals("3")){
            sql.append(" select t.domain_key,sum(t.DOMAIN_IP) ");
            name =  "IP";
        }
        sql.append("  AS allnum from PLAT_WEBSTATISTICS_DOMAIN t ");
        sql.append(" WHERE T.DOMAIN_DAY>=? AND T.DOMAIN_DAY<=? AND T.STATIC_SITEID=? group by t.domain_key ");
        sql.append(" )m order by m.allnum desc ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(),
                new Object[]{startDay,endDay,siteId}, null);
        Map<String, Object> map = new HashMap<String,Object>();
        if(list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                String DOMAIN_KEY = (String)list.get(i).get("DOMAIN_KEY");
                if(StringUtils.isNotEmpty(DOMAIN_KEY)){
                    title.add(list.get(i).get("DOMAIN_KEY").toString()); 
                }else{
                    title.add("浏览器直接输入"); 
                }
                pvData.add(Integer.parseInt(list.get(i).get("ALLNUM").toString()));
            }
        }else{
            
        }
        map.put("title", title.toArray());
        map.put("data", pvData.toArray());
        map.put("name", name);
        return map;
    }

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月9日 下午10:28:12
     * @param startDay
     * @param endDay
     * @param type
     * @return
     */
    @Override
    public Map<String, Object> getTopEntranceData(String startDay,
            String endDay) {
        List<String>  title = new ArrayList<String>();
        List<Integer> pvData = new ArrayList<Integer>();
        String name = "进入次数";
        StringBuffer sql = new StringBuffer("");
        sql.append(" SELECT * FROM ( ");
        sql.append(" select t.INVIEW_SOURCE,sum(t.INVIEW_INPUT) ");
        sql.append("  AS allnum from PLAT_WEBSTATISTICS_INVIEW t ");
        sql.append(" WHERE T.INVIEW_DAY>=? AND T.INVIEW_DAY<=? group by t.INVIEW_SOURCE ");
        sql.append(" )m order by m.allnum desc ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(),
                new Object[]{startDay,endDay}, null);
        Map<String, Object> map = new HashMap<String,Object>();
        if(list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                title.add(list.get(i).get("INVIEW_SOURCE").toString()); 
                pvData.add(Integer.parseInt(list.get(i).get("ALLNUM").toString()));
            }
        }else{
            
        }
        map.put("title", title.toArray());
        map.put("data", pvData.toArray());
        map.put("name", name);
        return map;
    }
    
    /**
     * 获取列表数据
     * @param filter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findList(SqlFilter filter,Map<String,Object> fieldInfo){
        String beginDate = filter.getRequest().getParameter("Q_T.INVIEW_DAY_GE");
        String endDate = filter.getRequest().getParameter("Q_T.INVIEW_DAY_LE");
        String siteId = filter.getRequest().getParameter("Q_T.STATIC_SITEID_EQ");
        StringBuffer sql = new StringBuffer("");
        sql.append(" SELECT * FROM ( ");
        sql.append(" select t.INVIEW_SOURCE,sum(t.INVIEW_PV) ");
        sql.append("  AS ALLNUM from PLAT_WEBSTATISTICS_INVIEW t ");
        sql.append(" WHERE T.INVIEW_DAY>=? AND T.INVIEW_DAY<=? AND T.STATIC_SITEID=? ");
        sql.append(" group by t.INVIEW_SOURCE");
        sql.append(" )m order by m.allnum desc ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(),
                new Object[]{beginDate,endDate,siteId}, filter.getPagingBean());
        Map<String,Object> siteInfo = this.getRecord("PLAT_WEBSTATISTICS_SITEINFO",
                new String[]{"SITEINFO_ID"},new Object[]{siteId});
        String SITEINFO_PAGEMAP = (String) siteInfo.get("SITEINFO_PAGEMAP");
        for(Map<String,Object> map:list){
            String INVIEW_SOURCE = (String) map.get("INVIEW_SOURCE");
            if(StringUtils.isNotEmpty(SITEINFO_PAGEMAP)){
                Map data = PlatJsonUtil.getUniqueData("PAGE_URL",INVIEW_SOURCE,SITEINFO_PAGEMAP);
                if(data!=null){
                    map.put("INVIEW_NAME",data.get("PAGE_NAME"));
                }else{
                    map.put("INVIEW_NAME","");
                }
            }
        }
        return list;
    }
  
}

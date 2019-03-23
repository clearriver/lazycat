/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.platform.webstatistics.dao.SiteInfoDao;
import com.stooges.platform.webstatistics.service.DayTrendService;
import com.stooges.platform.webstatistics.service.DomainService;
import com.stooges.platform.webstatistics.service.InviewService;
import com.stooges.platform.webstatistics.service.MapDataService;
import com.stooges.platform.webstatistics.service.NewOldService;
import com.stooges.platform.webstatistics.service.SiteInfoService;
import com.stooges.platform.webstatistics.util.TrafficStatisticsUrl;

/**
 * 描述 统计站点信息业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2018-03-22 17:08:50
 */
@Service("siteInfoService")
public class SiteInfoServiceImpl extends BaseServiceImpl implements SiteInfoService {

    /**
     * 所引入的dao
     */
    @Resource
    private SiteInfoDao dao;
    /**
     * 
     */
    @Resource
    private DayTrendService dayTrendService;
    /**
     * 
     */
    @Resource
    private DomainService domainService;
    /**
     * 
     */
    @Resource
    private InviewService inviewService;
    /**
     * 
     */
    @Resource
    private NewOldService newOldService;
    /**
     * 
     */
    @Resource
    private MapDataService mapDataService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取日访问趋势结果
     * @param date
     * @return
     */
    public String getDayTrendResult(String date,String siteInfoId){
        Map<String,Object> siteInfo = this.getRecord("PLAT_WEBSTATISTICS_SITEINFO",
                new String[]{"SITEINFO_ID"}, new Object[]{siteInfoId});
        String SITEINFO_CNZZID = (String) siteInfo.get("SITEINFO_CNZZID");
        Map<String,Object> params = new HashMap<String,Object>();
        String url = TrafficStatisticsUrl.domainname+TrafficStatisticsUrl.trendurl+"&siteid="
                +SITEINFO_CNZZID+"&st="+date+"&et="+date;
        String result = PlatHttpUtil.sendPostParams(url, params, TrafficStatisticsUrl.getHttpclient(SITEINFO_CNZZID));
        return result;
    }
    
    /**
     * 获取来源访问趋势结果
     * @param date
     * @param siteInfoId
     * @return
     */
    public String getDomainTrendResult(String date,String siteInfoId){
        Map<String,Object> siteInfo = this.getRecord("PLAT_WEBSTATISTICS_SITEINFO",
                new String[]{"SITEINFO_ID"}, new Object[]{siteInfoId});
        Map<String,Object> params = new HashMap<String,Object>();
        String SITEINFO_CNZZID = (String) siteInfo.get("SITEINFO_CNZZID");
        String url = TrafficStatisticsUrl.domainname+TrafficStatisticsUrl.domainurl+"&siteid="
                +SITEINFO_CNZZID+"&st="+date+"&et="+date;
        String result = PlatHttpUtil.sendPostParams(url, params, TrafficStatisticsUrl
                .getHttpclient(SITEINFO_CNZZID));
        return result;
    }
    
    /**
     * 获取网站内部各页面访问结果
     * @param date
     * @param siteInfoId
     * @return
     */
    public String getInviewTrendResult(String date,String siteInfoId){
        Map<String,Object> siteInfo = this.getRecord("PLAT_WEBSTATISTICS_SITEINFO",
                new String[]{"SITEINFO_ID"}, new Object[]{siteInfoId});
        Map<String,Object> params = new HashMap<String,Object>();
        String SITEINFO_CNZZID = (String) siteInfo.get("SITEINFO_CNZZID");
        String url = TrafficStatisticsUrl.domainname+TrafficStatisticsUrl.inviewurl+"&siteid="
                +SITEINFO_CNZZID+"&st="+date+"&et="+date;
        String result = PlatHttpUtil.sendPostParams(url, params, TrafficStatisticsUrl
                .getHttpclient(SITEINFO_CNZZID));
        return result;
    }
    
    /**
     * 获取网站新旧用户访问结果
     * @param date
     * @param siteInfoId
     * @return
     */
    public String getNewOldTrendResult(String date,String siteInfoId){
        Map<String,Object> siteInfo = this.getRecord("PLAT_WEBSTATISTICS_SITEINFO",
                new String[]{"SITEINFO_ID"}, new Object[]{siteInfoId});
        Map<String,Object> params = new HashMap<String,Object>();
        String SITEINFO_CNZZID = (String) siteInfo.get("SITEINFO_CNZZID");
        String url = TrafficStatisticsUrl.domainname+TrafficStatisticsUrl.newoldurl+"&siteid="
                +SITEINFO_CNZZID+"&st="+date+"&et="+date;
        String result = PlatHttpUtil.sendPostParams(url, params, TrafficStatisticsUrl
                .getHttpclient(SITEINFO_CNZZID));
        return result;
    }
    
    /**
     * 获取地图访问数据
     * @param date
     * @param siteInfoId
     * @return
     */
    public Map<String,String> getMapTrendResult(String date,String siteInfoId){
        Map<String,Object> siteInfo = this.getRecord("PLAT_WEBSTATISTICS_SITEINFO",
                new String[]{"SITEINFO_ID"}, new Object[]{siteInfoId});
        Map<String,Object> params = new HashMap<String,Object>();
        String SITEINFO_CNZZID = (String) siteInfo.get("SITEINFO_CNZZID");
        String url = TrafficStatisticsUrl.domainname+TrafficStatisticsUrl.mapdataurl+"&siteid="
                +SITEINFO_CNZZID+"&st="+date+"&et="+date;
        String urlpv = url +"&Quota=pv";
        String urluv = url +"&Quota=uv";
        String urlip = url +"&Quota=ip";
        String urlnewuv = url +"&Quota=newuv";
        String urlsession = url +"&Quota=session";
        String resultpv = PlatHttpUtil.sendPostParams(urlpv, params,
                TrafficStatisticsUrl.getHttpclient(SITEINFO_CNZZID));
        String resultuv = PlatHttpUtil.sendPostParams(urluv, params, 
                TrafficStatisticsUrl.getHttpclient(SITEINFO_CNZZID));
        String resultip = PlatHttpUtil.sendPostParams(urlip, params, 
                TrafficStatisticsUrl.getHttpclient(SITEINFO_CNZZID));
        String resultnewuv = PlatHttpUtil.sendPostParams(urlnewuv, params, 
                TrafficStatisticsUrl.getHttpclient(SITEINFO_CNZZID));
        String resultsession = PlatHttpUtil.sendPostParams(urlsession, params, 
                TrafficStatisticsUrl.getHttpclient(SITEINFO_CNZZID));
        Map<String,String> result = new HashMap<String,String>();
        result.put("resultpv", resultpv);
        result.put("resultuv", resultuv);
        result.put("resultip", resultip);
        result.put("resultnewuv", resultnewuv);
        result.put("resultsession", resultsession);
        return result;
    }
    
    /**
     * 保存站点所有访问数据
     * @param date
     */
    public void saveSiteMapTrendInfo(String date){
        StringBuffer sql = new StringBuffer("SELECT * FROM ");
        sql.append("PLAT_WEBSTATISTICS_SITEINFO T ORDER BY ");
        sql.append("T.SITEINFO_TIME DESC ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(),null,null);
        for(Map<String,Object> map:list){
            String SITEINFO_ID = (String) map.get("SITEINFO_ID");
            dayTrendService.saveTrendData(date, SITEINFO_ID);
            domainService.saveDomainData(date, SITEINFO_ID);
            inviewService.saveInviewData(date, SITEINFO_ID);
            newOldService.saveNewOldData(date, SITEINFO_ID);
            mapDataService.saveMapData(date, SITEINFO_ID);
        }
    }
    
    /**
     * 站点选择数据
     * @param param
     * @return
     */
    public List<Map<String,Object>> findForSelect(String param){
        StringBuffer sql = new StringBuffer("SELECT T.SITEINFO_ID");
        sql.append(" AS VALUE,T.SITEINFO_NAME AS LABEL FROM ");
        sql.append("PLAT_WEBSTATISTICS_SITEINFO T ORDER BY T.SITEINFO_TIME ASC");
        return dao.findBySql(sql.toString(),null,null);
    }
    
    /**
     * 获取缺省统计站点ID
     * @return
     */
    public String getDefaultSiteId(){
        List<Map<String,Object>> list = this.findForSelect(null);
        Map<String,Object> data = list.get(0);
        return (String) data.get("VALUE");
    }
    
    /**
     * 获取可编辑表格的数据
     * @param filter
     * @return
     */
    public List<Map> findEditTableDatas(SqlFilter filter){
        String SITEINFO_ID = filter.getRequest().getParameter("SITEINFO_ID");
        if(StringUtils.isNotEmpty(SITEINFO_ID)){
            Map<String,Object> siteInfo = this.getRecord("PLAT_WEBSTATISTICS_SITEINFO",
                    new String[]{"SITEINFO_ID"},new Object[]{SITEINFO_ID});
            String SITEINFO_PAGEMAP= (String) siteInfo.get("SITEINFO_PAGEMAP");
            return JSON.parseArray(SITEINFO_PAGEMAP, Map.class);
        }else{
            return null;
        }
    }
  
}

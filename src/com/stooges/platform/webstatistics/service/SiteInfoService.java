/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 统计站点信息业务相关service
 * @author HuYu
 * @version 1.0
 * @created 2018-03-22 17:08:50
 */
public interface SiteInfoService extends BaseService {
    /**
     * 获取日访问趋势结果
     * @param date
     * @return
     */
    public String getDayTrendResult(String date,String siteInfoId);
    /**
     * 获取来源访问趋势结果
     * @param date
     * @param siteInfoId
     * @return
     */
    public String getDomainTrendResult(String date,String siteInfoId);
    
    /**
     * 获取网站内部各页面访问结果
     * @param date
     * @param siteInfoId
     * @return
     */
    public String getInviewTrendResult(String date,String siteInfoId);
    
    /**
     * 获取网站新旧用户访问结果
     * @param date
     * @param siteInfoId
     * @return
     */
    public String getNewOldTrendResult(String date,String siteInfoId);
    /**
     * 获取地图访问数据
     * @param date
     * @param siteInfoId
     * @return
     */
    public Map<String,String> getMapTrendResult(String date,String siteInfoId);
    /**
     * 保存站点所有访问数据
     * @param date
     */
    public void saveSiteMapTrendInfo(String date);
    /**
     * 站点选择数据
     * @param param
     * @return
     */
    public List<Map<String,Object>> findForSelect(String param);
    /**
     * 获取缺省统计站点ID
     * @return
     */
    public String getDefaultSiteId();
    
    /**
     * 获取可编辑表格的数据
     * @param filter
     * @return
     */
    public List<Map> findEditTableDatas(SqlFilter filter);
}

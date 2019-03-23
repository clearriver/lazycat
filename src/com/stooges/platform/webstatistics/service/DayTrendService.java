/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 日期趋势业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2017-07-26 21:59:29
 */
public interface DayTrendService extends BaseService {

    /**
     * 描述 获取某天的趋势数据
     * @author 李俊
     * @created 2017年7月26日 下午10:05:04
     * @param date
     */
    public void saveTrendData(String date,String staticSiteId);

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月8日 下午7:42:17
     * @param searchDay
     * @return
     */
    public Map<String, Object> getTimeTrendData(String searchDay,String siteId);

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月8日 下午9:39:08
     * @param startDay
     * @param endDay
     * @return
     */
    public Map<String, Object> getDayTrendData(String startDay,String endDay,String siteId);

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月9日 下午8:34:46
     * @param startDay
     * @param endDay
     * @param type
     * @return
     */
    public Map<String, Object> getTopSourceData(String startDay, String endDay,
            String type,String siteId);

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月9日 下午10:28:02
     * @param startDay
     * @param endDay
     * @param type
     * @return
     */
    public Map<String, Object> getTopEntranceData(String startDay,
            String endDay);
    /**
     * 获取列表数据
     * @param filter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findList(SqlFilter filter,Map<String,Object> fieldInfo);
    
}

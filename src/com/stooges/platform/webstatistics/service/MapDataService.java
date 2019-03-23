/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service;

import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 地区数据业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 20:42:05
 */
public interface MapDataService extends BaseService {

    /**
     * 描述
     * @author 李俊
     * @created 2017年7月27日 下午8:43:05
     * @param dAYTREND_DAY
     */
    public void saveMapData(String date,String staticSiteId);

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月10日 下午9:15:33
     * @param startDay
     * @param endDay
     * @return
     */
    public Map<String, Object> getMapData(String startDay, String endDay,String siteId);
    
}

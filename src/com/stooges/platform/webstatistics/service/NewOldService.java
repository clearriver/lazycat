/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service;

import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 新建用户对比业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 20:17:53
 */
public interface NewOldService extends BaseService {
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年7月27日 下午8:18:24
     * @param date
     */
    public void saveNewOldData(String date,String staticSiteId);

    /**
     * 描述
     * @author 李俊
     * @created 2017年8月10日 下午8:38:19
     * @param startDay
     * @param endDay
     * @param type
     * @return
     */
    public Map<String, Object> getNewOldData(String startDay, String endDay,
            String type);
}

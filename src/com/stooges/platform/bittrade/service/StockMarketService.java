/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service;

import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 股票行情信息业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-19 15:12:30
 */
public interface StockMarketService extends BaseService {
    /**
     * 保存股票行情信息
     * @param code
     * @param beginDate
     * @param endDate
     */
    public void saveMarket(String code,String beginDate,String endDate);
    /**
     * 获取股票行情
     * @param code
     * @param date
     * @return
     */
    public Map<String,Double> getMarket(String code,String date);
}

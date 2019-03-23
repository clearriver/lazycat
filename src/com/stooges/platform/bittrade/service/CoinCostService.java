/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 币交易成本业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-04 17:01:22
 */
public interface CoinCostService extends BaseService {
    /**
     * 获取某个交易对的最新价格
     * @param symbol
     * @return
     */
    public Map<String,Double> getCoinNewestPrice(String symbol);
    
    /**
     * 根据filter获取配置的交易记录
     * @param filter
     * @return
     */
    public List<Map> findTradeByFilter(SqlFilter filter);
    /**
     * 更新所有价格
     */
    public void updateCostInfo();
}

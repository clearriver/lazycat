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
 * 描述 日涨幅信息业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-14 16:16:54
 */
public interface RangeInfoService extends BaseService {
    
    /**
     * 
     * @param date
     * @param symbol
     */
    public void initRangInfo(String date,String symbol);
    /**
     * 初始化振幅信息数据
     * @param beginDate
     * @param endDate
     * @param symbolBegin
     * @param symbolEnd
     */
    public void initRangInfo(String beginDate,String endDate,
            String symbolBegin,String symbolEnd);
    
    /**
     * 根据filter和配置信息获取数据列表
     * @param filter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findList(SqlFilter filter,Map<String,Object> fieldInfo);
    /**
     * 获取交易对列表
     * @return
     */
    public List<String> getSymbolList();
    /**
     * 根据交易对获取最新价格
     * @param symbol
     * @return
     */
    public double getNowPrice(String symbol);
    
}

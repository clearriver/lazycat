/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.dao;

import java.util.List;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 委托单业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-16 18:12:13
 */
public interface OrderDao extends BaseDao {

    /**
     * 获取止盈价列表
     * @param symbol
     * @param userCode
     * @return
     */
    public List<Double> findUserOrderSell(String symbol,String userCode);
}

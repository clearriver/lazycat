/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.service.BaseService;

/**
 * 描述 委托单业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-16 18:12:13
 */
public interface OrderService extends BaseService {
    /**
     * 执行自动交易
     */
    public void exeTrade();
    /**
     * 生成委托单
     * @param orderInfo
     */
    public void genOrders(HttpServletRequest request);
    /**
     * 获取当前系统中的交易用户账号和交易对
     * @return
     */
    public List<Map<String,Object>> findCurrentUserAndSymbol();
    /**
     * 获取止盈价列表
     * @param symbol
     * @param userCode
     * @return
     */
    public List<Double> findUserOrderSell(String symbol,String userCode);
    /**
     * 获取订单列表
     * @param symbol
     * @param userCode
     * @return
     */
    public List<Map<String,Object>> findOrders(String symbol,String userCode);
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 火币账号信息业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-15 09:41:39
 */
public interface HuoAccountService extends BaseService {
    
    /**
     * 根据账号编码获取火币账号列表
     * @param code
     * @return
     */
    public List<Map> findAccountsByCode(String code);
    /**
     * 获取API地址
     * @param accessKey
     * @param secretKey
     * @param method
     * @param uri
     * @return
     */
    public String getApiUrl(String accessKey,String secretKey,String method,String uri,Map<String,String> params);
    /**
     * 获取当前委托列表
     * @param code
     * @return
     */
    public List<Map> findOrderList(String code,String symbol);
    /**
     * 获取当前委托列表
     * @param code
     * @param symbol
     * @param type
     * @return
     */
    public List<Map> findOrderList(String code,String symbol,String type);
    /**
     * 撤销一个订单
     * @param userCode
     * @param orderId
     */
    public void revokeOneOrder(String userCode,String orderId);
    /**
     * 获取账号的ID
     * @param code
     * @return
     */
    public String getAccountId(String code);
    
    /**
     * 创建一个订单
     * @param accountId:账号ID
     * @param amount:购买数量
     * @param price:价格
     * @param symbol:成交对
     * @param userCode:用户编码
     */
    public void createOrder(String amount,
            String price,String symbol,String type,String userCode);
    
    /**
     * 
     * 描述 根据查询参数JSON获取数据列表
     * @created 2016年3月27日 上午11:16:25
     * @param queryParamsJson
     * @return
     */
    public List<Map<String,Object>> findList(String queryParamsJson);
}

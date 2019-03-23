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
 * 描述 价格提醒业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-18 11:20:54
 */
public interface PriceRemindService extends BaseService {
    
    /**
     * 保存提醒信息数据
     * @param userCode
     * @param symbol
     * @param fallPrice
     * @param risePrice
     */
    public void saveRemind(String userCode,String symbol,
            double fallPrice,double risePrice);
    /**
     * 获取下一个提醒的委托单
     * @return
     */
    public Map<String,Object> getNextRemindOrder(double lastFallPrice,double lastRisePrice);
    /**
     * 更新状态
     * @param userCode
     * @param smlbol
     * @param type
     */
    public void updateDisable(String userCode,String smlbol,int type);
    /**
     * 获取当前设置的有效价格提醒列表
     * @return
     */
    public List<Map<String,Object>> findList();
    
    /**
     * 提醒价格接口
     */
    public void remindPrice();
}

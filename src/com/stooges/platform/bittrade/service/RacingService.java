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
 * 描述 赛车数据业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-31 10:43:06
 */
public interface RacingService extends BaseService {
    /**
     * 根据日期获取列表数据
     * @param date
     * @return
     */
    public List<Map<String,Object>> findList(String date);
    /**
     * 获取金额
     * @param date
     * @param totalMoney
     * @return
     */
    public Map<String,Object> getBetResult(String date,Integer totalMoney);
}

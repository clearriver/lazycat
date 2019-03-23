/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 定时任务业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-27 16:37:05
 */
public interface SheduleDao extends BaseDao {
    /**
     * 根据编码获取数量
     * @param sheduleCode
     * @return
     */
    public int getCountBySheduleCode(String sheduleCode);

}

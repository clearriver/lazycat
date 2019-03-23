/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 工作日业务相关dao
 * @author 李俊
 * @version 1.0
 * @created 2017-05-03 09:32:53
 */
public interface WorkdayDao extends BaseDao {
    /**
     * 根据开始日期和结束日期获取工作日数量
     * @param beginDate
     * @param endDate
     * @return
     */
    public int getWorkDayCount(String beginDate,String endDate);
}

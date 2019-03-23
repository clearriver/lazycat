/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 视频教程业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-08-07 16:27:05
 */
public interface VideoCourseDao extends BaseDao {
    /**
     * 获取最大排序值
     * @return
     */
    public int getMaxSn();
}

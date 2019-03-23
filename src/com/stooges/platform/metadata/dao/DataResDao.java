/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.metadata.dao;

import java.util.List;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 数据资源信息业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2018-05-07 17:35:52
 */
public interface DataResDao extends BaseDao {
    /**
     * 根据资源ID获取目录ID列表
     * @param resId
     * @return
     */
    public List<String> findCataLogIds(String resId);
}

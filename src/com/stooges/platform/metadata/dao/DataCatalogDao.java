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
 * 描述 数据目录业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2018-05-07 10:53:23
 */
public interface DataCatalogDao extends BaseDao {
    /**
     * 根据资源ID获取目录id集合
     * @param resId
     * @return
     */
    public List<String> findCatalogIds(String resId);
}

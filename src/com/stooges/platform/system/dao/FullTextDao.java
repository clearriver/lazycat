/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 全文检索业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-01 10:15:45
 */
public interface FullTextDao extends BaseDao {
    /**
     * 根据表名和记录ID获取ID
     * @param busTableName
     * @param recordId
     * @return
     */
    public String getId(String busTableName,String recordId);
}

/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 数据源信息业务相关dao
 * @author HuYu
 * @version 1.0
 * @created 2018-03-30 15:06:38
 */
public interface DbConnDao extends BaseDao {
    /**
     * 获取数据库类型
     * @param dbConnCode
     * @return
     */
    public String getDbType(String dbConnCode);
}

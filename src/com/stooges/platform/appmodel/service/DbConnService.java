/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 数据源信息业务相关service
 * @author HuYu
 * @version 1.0
 * @created 2018-03-30 15:06:38
 */
public interface DbConnService extends BaseService {
    /**
     * 判断是否是有效的连接
     * @param dbConn
     * @return
     */
    public boolean isValidDb(Map<String,Object> dbConn);
    
    /**
     * 获取数据库类型
     * @param dbConnCode
     * @return
     */
    public String getDbType(String dbConnCode);
    
}

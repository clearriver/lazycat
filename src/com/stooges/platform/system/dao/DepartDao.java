/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.dao;

import java.util.List;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 部门业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-09 09:43:59
 */
public interface DepartDao extends BaseDao {
    /**
     * 根据单位ID和部门编码判断是否存在部门信息
     * @param companyId
     * @param departCode
     * @return
     */
    public boolean isExistsDepart(String companyId,String departCode);
}

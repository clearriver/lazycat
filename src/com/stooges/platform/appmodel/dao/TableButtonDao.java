/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 表格按钮业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-03-17 10:25:49
 */
public interface TableButtonDao extends BaseDao {
    
    /**
     * 根据表单控件ID获取最大排序值
     * @param formControlId
     * @return
     */
    public int getMaxSn(String formControlId);
}

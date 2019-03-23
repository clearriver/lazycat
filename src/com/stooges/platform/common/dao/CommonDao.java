/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.common.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 描述
 * @author 胡裕
 * @created 2017年2月1日 上午10:22:50
 */
public interface CommonDao extends BaseDao {
    /**
     * 获取记录的数量
     * @param validTableName
     * @param validFieldName
     * @param validFieldValue
     * @return
     */
    public int getRecordCount(String validTableName,
            String validFieldName,String validFieldValue,String RECORD_ID);
}

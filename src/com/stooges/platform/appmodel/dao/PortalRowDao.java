/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 门户行业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 15:07:52
 */
public interface PortalRowDao extends BaseDao {
    /**
     * 根据主题ID获取最大排序值
     * @param themeId
     * @return
     */
    public int getMaxSn(String themeId);
}

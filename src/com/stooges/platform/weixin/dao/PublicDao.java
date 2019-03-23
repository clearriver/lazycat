/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 公众号业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-18 15:51:54
 */
public interface PublicDao extends BaseDao {
    /**
     * 获取最早配置的公众号ID
     * @return
     */
    public String firstPublicId();
}

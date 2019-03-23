/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.dao;

import java.util.List;
import java.util.Map;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 文章信息业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-11 10:47:32
 */
public interface ArticleDao extends BaseDao {
    /**
     * 获取最新的标识ID
     * @return
     */
    public int getNewSignId();
    /**
     * 获取最新的排序值
     * @return
     */
    public int getNewArticleSn();
}

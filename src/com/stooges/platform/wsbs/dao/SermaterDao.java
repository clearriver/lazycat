/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 材料表业务相关dao
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-18 10:40:57
 */
public interface SermaterDao extends BaseDao {
    /**
     * 根据事项ID获取最大排序
     * @param serItemId
     * @return
     */
    public int getMaxSn(String serItemId);
    /**
     * 描述 更新排序字段
     * @param materIds
     */
    public void updateSn(String[] materIds);
    /**
     * 描述 修改绑定材料是否为必须提供
     * @param isneed
     * @param materIds
     */
    public void updateIsneed(String isneed, String materIds);
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 行组件配置业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 15:47:00
 */
public interface PortalRowconfDao extends BaseDao {
    /**
     * 获取组件配置数量
     * @param rowId
     * @return
     */
    public int getCompConfCount(String rowId);
    
    /**
     * 判断是否存在该配置
     * @param THEME_ID 主题ID
     * @param CONF_ID 现有配置ID
     * @param CONF_COMPID 组件ID
     * @return
     */
    public boolean isExists(String THEME_ID,String CONF_ID,String CONF_COMPID);
}

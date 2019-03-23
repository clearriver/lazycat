/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.dao;

import java.util.Set;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 角色组业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-15 15:20:40
 */
public interface RoleGroupDao extends BaseDao {

    /**
     * 根据用户ID
     * @param userId
     * @return
     */
    public Set<String> getUserGrantGroupIds(String userId);
}

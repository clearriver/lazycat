/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.dao;

import java.util.List;
import java.util.Set;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 角色权限中间表业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-23 16:57:24
 */
public interface RoleRightDao extends BaseDao {
    /**
     * 根据角色ID获取
     * @param roleId
     * @return
     */
    public List<String> getRightRecordIds(String roleId,String tableName);
    /**
     * 根据用户ID和表名称获取用户被授权的资源IDS集合
     * @param userId
     * @param tableName
     * @return
     */
    public Set<String> getUserGrantRightIds(String userId,String tableName);
}

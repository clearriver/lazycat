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
 * 描述 系统用户业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-10 17:03:31
 */
public interface SysUserDao extends BaseDao {
    /**
     * 根据角色ID获取用户IDS
     * @param roleId
     * @return
     */
    public List<String> findUserIds(String roleId);
    
    
    /**
     * 获取被授权的资源ID集合
     * @param userId
     * @return
     */
    public List<String> findGrantRightIds(String userId);
    /**
     * 判断是否存在用户
     * @param userId
     * @return
     */
    public boolean isExistsUser(String userId);
    /**
     * 根据单位ID和角色ID获取用户IDS
     * @param companyId
     * @param roleIds
     * @return
     */
    public List<String> findUserIds(String companyId,String roleIds);

    /**
     * 保存排序
     * @param userIds
     */
	public void updateSn(String[] userIds);
	/**
     * 根据用户组ID获取用户ID列表
     * @param USERGROUP_ID
     * @return
     */
    public List<String> findGroupUserIds(String USERGROUP_ID);


	/**
	 * @param 根据部门ID获取获取ID列表
	 * @return
	 */
	public List<String> findDepartUserIds(String dEPART_ID);
}

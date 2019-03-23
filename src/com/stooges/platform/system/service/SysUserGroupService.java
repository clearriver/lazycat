/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.service.BaseService;

/**
 * 描述 用户组管理业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2018-05-10 22:05:56
 */
public interface SysUserGroupService extends BaseService {
	/**
     * 根据单位ID获取下拉树数据
     * @param companyId
     * @return
     */
    public List<Map<String,Object>> findSelectList(String companyId);

	/**
	 * @param uSERGROUP_ID
	 * @param asList
	 */
	public void saveUsers(String uSERGROUP_ID, String checkUserIds);
	
	/**
     * 分配用户接口
     * @param request
     * @return
     */
    public Map<String,Object> grantUsers(HttpServletRequest request);
    
    /**
     * 删除用户组信息
     * @param request
     * @return
     */
    public Map<String,Object> deleteGroups(HttpServletRequest request);
    
    /**
     * 
     * @param request
     * @return
     */
    public Map<String,Object> saveOrUpdateGroup(HttpServletRequest request);
    
}

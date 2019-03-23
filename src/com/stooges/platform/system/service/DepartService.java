/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 部门业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-09 09:43:59
 */
public interface DepartService extends BaseService {
    /**
     * 根据单位ID和部门编码判断是否存在部门信息
     * @param companyId
     * @param departCode
     * @return
     */
    public boolean isExistsDepart(String companyId,String departCode);
    /**
     * 根据单位ID获取下拉树数据
     * @param companyId
     * @return
     */
    public List<Map<String,Object>> findSelectTree(String companyId);
    /**
     * 获取部门和用户的JSON字符串
     * @param params
     * @return
     */
    public String getDepartAndUserJson(Map<String,Object> params);
    /**
     * 获取自动补全的部门和用户的数据源
     * @param sqlFilter
     * @return
     */
    public List<Map<String,Object>> findAutoDepartUser(SqlFilter sqlFilter);
    /**
     * 删除部门并且级联删除用户
     * @param departId
     */
    public void deleteDepartCascadeUser(String departId);
    
    /**
     * 新增或者修改部门信息
     * @param request
     * @param postParams
     * @return
     */
    public Map<String,Object> saveOrUpdateDepart(HttpServletRequest request,
            Map<String,Object> postParams);
    
    /**
     * 删除部门
     * @param request
     * @param postParams
     * @return
     */
    public Map<String,Object> delDepart(HttpServletRequest request,
            Map<String,Object> postParams);
    
    /**
     * 根据filter获取网格项目
     * @param sqlFilter
     * @return
     */
    public List<Map<String,Object>> findGridItemList(SqlFilter sqlFilter);
    /**
     * 新增或者修改部门接口
     * @param request
     * @return
     */
    public Map<String,Object> saveOrUpdateDep(HttpServletRequest request);
    /**
     * 删除部门接口
     * @param request
     * @return
     */
    public Map<String,Object> deleteDepartCascadeUser(HttpServletRequest request);
	/**
	 * 保存选择的用户的部门ID
	 * @param dEPART_ID
	 * @param checkUserIds
	 */
	public void saveUsers(String dEPART_ID, String checkUserIds);
	/**
	 * @param params
	 * @return
	 */
	public String getDepartAndUserAndAllJson(Map<String, Object> params);
	/**
	 * 分配用户接口
	 * @param request
	 * @return
	 */
	public Map<String,Object> grantUsers(HttpServletRequest request);
    
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.service.BaseService;

/**
 * 描述 单位业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-09 09:43:59
 */
public interface CompanyService extends BaseService {
    /**
     * 根据单位ID级联删除相关的数据库表信息
     * @param companyId
     */
    public void deleteCompanyCacasdeAssocial(String companyId); 
    /**
     * 新增或者修改单位信息表
     * @param request
     * @return
     */
    public Map<String,Object> saveOrUpdateCompany(HttpServletRequest request);
    /**
     * 删除单位信息
     * @param request
     * @return
     */
    public Map<String,Object> delCompany(HttpServletRequest request);
}

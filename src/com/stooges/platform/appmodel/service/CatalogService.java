/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 资源分类业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-21 11:07:35
 */
public interface CatalogService extends BaseService {
    /**
     * 删除数据并且级联删除子表
     * @param catalogId
     */
    public void deleteCascaseSub(String catalogId);
    
    /**
     * 获取自动补全的目录和服务数据列表
     * @param filter
     * @return
     */
    public List<Map<String,Object>> findAutoCatalogSer(SqlFilter filter);
    
    /**
     * 获取目录和服务树形JSON
     * @param params
     * @return
     */
    public String getCatalogAndSerJson(Map<String,Object> params);
}

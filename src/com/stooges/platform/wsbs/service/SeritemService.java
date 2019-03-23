/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 信息表业务相关service
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-15 11:26:44
 */
public interface SeritemService extends BaseService {

    /**
     * 根据事项编号获取事项信息和流程信息
     * @param sERITEM_CODE
     * @return
     */
    Map<String, Object> getItemAndDefMap(String seritemCode);
    /**
     * 根据服务事项目录ID获取服务事项列表
     * @param SERCATALOG_ID
     * @return
     */
    public List<Map<String, Object>> getItemList(String SERCATALOG_ID);
    /**
     * 根据filter获取网格项目
     * @param sqlFilter
     * @return
     */
    public List<Map<String,Object>> findGridItemList(SqlFilter sqlFilter);
    /**
     * 获取树形的服务事项类别和服务事项数据
     * @param request
     * @param response
     */
    public String getTypeAndItemsJson(Map<String,Object> params);
    
}

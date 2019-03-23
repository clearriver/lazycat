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
 * 描述 材料表业务相关service
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-15 11:26:44
 */
public interface SermaterService extends BaseService {
    /**
     * 获取材料列表
     * @param sqlFilter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findBySqlFilter(SqlFilter sqlFilter,Map<String,Object> fieldInfo);
    /**
     * 根据服务事项ID获取最大排序
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
     * 保存材料信息,级联附件
     * @param sermater
     * @return
     */
    public Map<String,Object> saveCascadeFileAttach(Map<String,Object> sermater);
    /**
     * 描述 修改绑定材料是否为必须提供
     * @param isneed
     * @param materIds
     */
    public void updateIsneed(String isneed, String materIds);
}

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
 * 描述 GenCmpTpl业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-03-15 17:15:24
 */
public interface GenCmpTplService extends BaseService {
    /**
     * 根据sqlFilter
     * @param sqlFilter
     * @return
     */
    public List<Map<String,Object>> findBySqlFilter(SqlFilter sqlFilter);
    /**
     * 根据模版类型获取可选JAVA接口列表
     * @param tplType
     * @return
     */
    public List<Map<String,Object>> findSelectJavaInters(String tplType);
    /**
     * 根据模版类型获取button接口列表
     * @param tplType
     * @return
     */
    public List<Map<String,Object>> findSelectButtons(String tplType);
}

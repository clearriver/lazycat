/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 目录表业务相关service
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-15 11:26:44
 */
public interface SercatalogService extends BaseService {
    /**
     * 获取服务事项目录列表数据源
     * @param queryParamJson
     * @return
     */
    public List<Map<String,Object>> findCatalogList(String queryParamJson);
}

/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 网站栏目业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-05 15:05:57
 */
public interface ColumnService extends BaseService {
    /**
     * 根据站点ID获取可选下拉树数据
     * @param siteId
     * @return
     */
    public List<Map<String,Object>> findSelectTree(String siteId);
}

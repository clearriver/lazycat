/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.metadata.service;

import java.util.List;

import com.stooges.core.service.BaseService;

/**
 * 描述 数据目录业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2018-05-07 10:53:23
 */
public interface DataCatalogService extends BaseService {
    /**
     * 根据目录ID级联删除子孙目录
     * @param catalogId
     */
    public void deleteCascadeChild(String catalogId);
    /**
     * 根据资源ID获取目录id集合
     * @param resId
     * @return
     */
    public List<String> findCatalogIds(String resId);
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 门户组件业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-12 14:39:45
 */
public interface PortalCompService extends BaseService {
    /**
     * 根据类别编码获取列表数据
     * @param typeCode
     * @return
     */
    public List<Map<String,Object>> findByCompTypeCode(String typeCode);
}

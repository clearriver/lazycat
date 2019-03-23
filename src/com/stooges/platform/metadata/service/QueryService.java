/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.metadata.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 输入参数业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2018-05-08 11:11:34
 */
public interface QueryService extends BaseService {
    /**
     * 根据资源ID获取参数列表
     * @param resId
     * @return
     */
    public List<Map<String,Object>> findByResId(String resId);
    /**
     * 判断参数是否存在
     * @param resId
     * @param cn
     * @param en
     * @return
     */
    public boolean isExistsQuery(String resId,String cn,String en);
}

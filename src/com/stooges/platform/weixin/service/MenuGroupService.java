/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 菜单组业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-21 10:04:25
 */
public interface MenuGroupService extends BaseService {
    
    /**
     * 获取菜单组列表数据源
     * @param queryParamJson
     * @return
     */
    public List<Map<String,Object>> findGroupList(String publicId);
    /**
     * 获取组的数量
     * @param publicId
     * @return
     */
    public int getGroupCount(String publicId);
    
}

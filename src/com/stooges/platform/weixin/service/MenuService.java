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
 * 描述 菜单业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-21 10:04:25
 */
public interface MenuService extends BaseService {
    /**
     * 根据原始ID删除菜单
     * @param sourceId
     * @return
     */
    public String deleteMenus(String sourceId);
    /**
     * 根据菜单组获取数量
     * @param groupId
     * @return
     */
    public int getCountByGroupId(String groupId);
    /**
     * 更新所有菜单到微信
     * @param publicId
     */
    public String updateAllMenuToWeixin(String publicId);
    /**
     * 根据组ID获取列表
     * @param groupId
     * @return
     */
    public List<Map<String,Object>> findByGroupId(String groupId);
    
}

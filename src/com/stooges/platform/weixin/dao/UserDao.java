/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.dao;

import java.util.List;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 微信用户业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 16:30:21
 */
public interface UserDao extends BaseDao {
    /**
     * 获取用户被打的标签IDS
     * @param userId
     * @return
     */
    public List<String> getUserTagIds(String userId);
    /**
     * 获取用户的openId列表
     * @param userIds
     * @return
     */
    public List<String> findUserOpenIdList(String userIds);
    /**
     * 获取用户的IDS
     * @param publicId
     * @return
     */
    public List<String> findUserIds(String publicId);
    /**
     * 根据公众ID获取openIds
     * @param publicId
     * @return
     */
    public List<String> findUserOpenIdsByPublicId(String publicId);
}

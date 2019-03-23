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
 * 描述 用户标签业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-29 11:11:42
 */
public interface UserTagDao extends BaseDao {
    /**
     * 获取在微信公众号中不存在的标签IDS
     * @param publicId
     * @param weixinTagIds
     * @return
     */
    public List<String> findUnExistTagIdList(String publicId,String weixinTagIds);
    /**
     * 获取本地存在的微信标签ID
     * @param userId
     * @return
     */
    public List<String> findLocalUserWeixinTagId(String userId);
}

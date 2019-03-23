/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.weixin.dao.UserDao;

/**
 * 描述微信用户业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 16:30:21
 */
@Repository("weixinUserDao")
public class UserDaoImpl extends BaseDaoImpl implements UserDao {

    /**
     * 获取用户被打的标签IDS
     * @param userId
     * @return
     */
    public List<String> getUserTagIds(String userId){
        StringBuffer sql = new StringBuffer("SELECT T.TAG_ID");
        sql.append(" FROM PLAT_WEIXIN_UANDTAG T WHERE T.WEIUSER_ID=?");
        List<String> tagIds = this.getJdbcTemplate().queryForList(sql.toString(), 
                new Object[]{userId}, String.class);
        return tagIds;
    }
    
    /**
     * 获取用户的openId列表
     * @param userIds
     * @return
     */
    public List<String> findUserOpenIdList(String userIds){
        StringBuffer sql = new StringBuffer("SELECT U.OPEN_ID");
        sql.append(" FROM PLAT_WEIXIN_USER U WHERE U.USER_ID IN ");
        sql.append(PlatStringUtil.getSqlInCondition(userIds));
        return this.getJdbcTemplate().queryForList(sql.toString(), String.class);
    }
    
    /**
     * 根据公众ID获取openIds
     * @param publicId
     * @return
     */
    public List<String> findUserOpenIdsByPublicId(String publicId){
        StringBuffer sql = new StringBuffer("SELECT U.OPEN_ID");
        sql.append(" FROM PLAT_WEIXIN_USER U WHERE U.PUBLIC_ID=? ");
        sql.append("ORDER BY U.CREATETIME DESC");
        return this.getJdbcTemplate().queryForList(sql.toString(),new Object[]{publicId}
        , String.class);
    }
    
    /**
     * 获取用户的IDS
     * @param publicId
     * @return
     */
    public List<String> findUserIds(String publicId){
        StringBuffer sql = new StringBuffer("SELECT U.USER_ID");
        sql.append(" FROM PLAT_WEIXIN_USER U WHERE U.PUBLIC_ID=? ");
        sql.append("ORDER BY U.CREATETIME DESC");
        return this.getJdbcTemplate().queryForList(sql.toString(),new Object[]{publicId}
        , String.class);
    }
}

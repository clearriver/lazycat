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
import com.stooges.platform.weixin.dao.UserTagDao;

/**
 * 描述用户标签业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-29 11:11:42
 */
@Repository
public class UserTagDaoImpl extends BaseDaoImpl implements UserTagDao {

    /**
     * 获取在微信公众号中不存在的标签IDS
     * @param publicId
     * @param weixinTagIds
     * @return
     */
    public List<String> findUnExistTagIdList(String publicId,String weixinTagIds){
        StringBuffer sql = new StringBuffer("SELECT T.USERTAG_ID");
        sql.append(" FROM PLAT_WEIXIN_USERTAG T WHERE T.USERTAG_PUBID=?");
        sql.append(" AND T.USERTAG_WEIID NOT IN ").append(PlatStringUtil.
                getSqlInCondition(weixinTagIds));
        List<String> idList = this.getJdbcTemplate().queryForList(sql.toString(),
                new Object[]{publicId}, String.class);
        return idList;
    }
    
    /**
     * 获取本地存在的微信标签ID
     * @param userId
     * @return
     */
    public List<String> findLocalUserWeixinTagId(String userId){
        StringBuffer sql = new StringBuffer("SELECT U.USERTAG_WEIID FROM PLAT_WEIXIN_USERTAG U");
        sql.append(" WHERE U.USERTAG_ID IN (select T.TAG_ID from PLAT_WEIXIN_UANDTAG T");
        sql.append(" WHERE T.WEIUSER_ID=? )");
        return this.getJdbcTemplate().queryForList(sql.toString(),new Object[]{userId},String.class);
    }
}

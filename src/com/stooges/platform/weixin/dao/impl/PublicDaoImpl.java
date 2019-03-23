/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.weixin.dao.PublicDao;

/**
 * 描述公众号业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-18 15:51:54
 */
@Repository
public class PublicDaoImpl extends BaseDaoImpl implements PublicDao {

    /**
     * 获取最早配置的公众号ID
     * @return
     */
    public String firstPublicId(){
        StringBuffer sql = new StringBuffer("SELECT T.PUBLIC_ID");
        sql.append(" FROM PLAT_WEIXIN_PUBLIC T ORDER BY T.PUBLIC_CREATETIME ASC");
        String publicId = this.getJdbcTemplate().queryForObject(sql.toString(),String.class);
        return publicId;
    }
}

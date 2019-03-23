/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.appmodel.dao.GlobalUrlDao;

/**
 * 描述全局URL业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-23 09:22:54
 */
@Repository
public class GlobalUrlDaoImpl extends BaseDaoImpl implements GlobalUrlDao {
    /**
     * 根据过滤类型获取全局URL列表
     * @param filterType
     * @return
     */
    public List<String> findByFilterType(String filterType){
        StringBuffer sql = new StringBuffer("select T.URL_ADDRESS from");
        sql.append(" PLAT_SYSTEM_GLOBALURL T WHERE T.URL_FILTERTYPE=?");
        sql.append(" ORDER BY T.URL_CREATETIME DESC");
        List<String> urls = this.getJdbcTemplate().queryForList(sql.toString(), 
                new Object[]{filterType},String.class);
        return urls;
    }
}

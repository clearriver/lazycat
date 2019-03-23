/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.metadata.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.metadata.dao.DataCatalogDao;

/**
 * 描述数据目录业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2018-05-07 10:53:23
 */
@Repository
public class DataCatalogDaoImpl extends BaseDaoImpl implements DataCatalogDao {

    /**
     * 根据资源ID获取目录id集合
     * @param resId
     * @return
     */
    public List<String> findCatalogIds(String resId){
        StringBuffer sql = new StringBuffer("SELECT T.CATALOG_ID");
        sql.append(" FROM PLAT_METADATA_CATARES T WHERE T.DATARES_ID=? ");
        return this.getJdbcTemplate().queryForList(sql.toString(),
                new Object[]{resId},String.class);
    }
}

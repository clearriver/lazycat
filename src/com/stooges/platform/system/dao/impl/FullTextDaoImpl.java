/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.dao.impl;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.system.dao.FullTextDao;

/**
 * 描述全文检索业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-01 10:15:45
 */
@Repository
public class FullTextDaoImpl extends BaseDaoImpl implements FullTextDao {

    /**
     * 根据表名和记录ID获取ID
     * @param busTableName
     * @param recordId
     * @return
     */
    public String getId(String busTableName,String recordId){
        StringBuffer sql = new StringBuffer("select T.FULLTEXT_ID");
        sql.append(" FROM PLAT_SYSTEM_FULLTEXT T ");
        sql.append("WHERE T.FULLTEXT_TABLENAME=? AND T.FULLTEXT_RECORDID=?");
        String FULLTEXT_ID = this.getUniqueObj(sql.toString(), 
                new Object[]{busTableName,recordId}, String.class);
        return FULLTEXT_ID;
    }
}

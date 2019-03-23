/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao.impl;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;

import org.springframework.stereotype.Repository;

import com.stooges.platform.appmodel.dao.DbConnDao;

/**
 * 描述数据源信息业务相关dao实现类
 * @author HuYu
 * @version 1.0
 * @created 2018-03-30 15:06:38
 */
@Repository
public class DbConnDaoImpl extends BaseDaoImpl implements DbConnDao {

    /**
     * 获取数据库类型
     * @param dbConnCode
     * @return
     */
    public String getDbType(String dbConnCode){
        StringBuffer sql = new StringBuffer("SELECT T.DBCONN_CLASS");
        sql.append(" FROM PLAT_APPMODEL_DBCONN T WHERE T.DBCONN_CODE=?");
        String driverName = this.getJdbcTemplate().queryForObject(sql.toString(), 
                new Object[]{dbConnCode}, String.class);
        if(driverName.equals("com.mysql.jdbc.Driver")){
            return "MYSQL";
        }else if(driverName.equals("com.microsoft.sqlserver.jdbc.SQLServerDriver")){
            return "SQLSERVER";
        }else{
            return "ORACLE";
        }
    }
}

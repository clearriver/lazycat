/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.common.dynadatasource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;

import com.alibaba.druid.pool.DruidDataSource;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatDbUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatPropUtil;
import com.sun.mail.imap.AppendUID;

/**
 * 
 * @author 李俊
 *
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource{
    /**
     * 
     * @return
     */
    @Override  
    protected Object determineCurrentLookupKey() {  
        // 从自定义的位置获取数据源标识  
        return DynamicDataSourceHolder.getDatasource();  
    }  
    /**
     * 
     */
    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources){
        try{
            StringBuffer sql = new StringBuffer("");
            sql.append("select * from PLAT_APPMODEL_DBCONN t");
            sql.append(" order by t.DBCONN_TIME DESC");
            Properties properties = PlatPropUtil.readProperties("conf/config.properties");
            String dbUrl = properties.getProperty("jdbc.url");
            String username = properties.getProperty("jdbc.username");
            String password = properties.getProperty("jdbc.password");
            Connection conn = PlatDbUtil.getConnect(dbUrl, username, password);
            List<Map<String, Object>> list = PlatDbUtil.findBySql(conn, 
                    sql.toString(),null);
            for(Map<String,Object> data:list){
                String DBCONN_CLASS = (String) data.get("DBCONN_CLASS");
                String DBCONN_URL = (String) data.get("DBCONN_URL");
                String DBCONN_USERNAME = (String) data.get("DBCONN_USERNAME");
                String DBCONN_PASS = (String) data.get("DBCONN_PASS");
                String DBCONN_CODE = (String) data.get("DBCONN_CODE");
                DruidDataSource dataSource  = new DruidDataSource();
                dataSource.setDriverClassName(DBCONN_CLASS);
                dataSource.setUrl(DBCONN_URL);
                dataSource.setName(DBCONN_CODE);
                dataSource.setUsername(DBCONN_USERNAME);
                dataSource.setPassword(DBCONN_PASS);
                targetDataSources.put(DBCONN_CODE, dataSource);
            }
        }catch(Exception e){
            PlatLogUtil.printStackTrace(e);
        }
        super.setTargetDataSources(targetDataSources);
    }
    
}

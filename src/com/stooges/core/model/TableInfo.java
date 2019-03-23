/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.model;

import java.io.Serializable;

/**
 * 描述 数据库表信息
 * @author 胡裕
 * @created 2017年1月25日 上午8:53:51
 */
public class TableInfo implements Serializable {
    /**
     * 数据库表名称
     */
    private String tableName;
    /**
     * 数据库表注释
     */
    private String tableComments;
    
    public TableInfo(){
        
    }
    /**
     * @param tableName
     * @param tableComments
     */
    public TableInfo(String tableName, String tableComments) {
        this.tableName = tableName;
        this.tableComments = tableComments;
    }
    
    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }
    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    /**
     * @return the tableComments
     */
    public String getTableComments() {
        return tableComments;
    }
    /**
     * @param tableComments the tableComments to set
     */
    public void setTableComments(String tableComments) {
        this.tableComments = tableComments;
    }
}

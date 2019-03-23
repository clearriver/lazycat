/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.appmodel.dao.DbManagerDao;

/**
 * 描述
 * @author 胡裕
 * @created 2017年1月25日 上午9:29:41
 */
@Repository
public class DbManagerDaoImpl extends BaseDaoImpl implements DbManagerDao {

    /**
     * 判断一张表是否存在oracle中
     * @param tableName
     * @return
     */
    private boolean isExistsTableInOracle(String tableName,boolean isIncludeThisTable){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM TABS ");
        sql.append("T WHERE T.TABLE_NAME=? ");
        List params = new ArrayList();
        params.add(tableName);
        if(!isIncludeThisTable){
            sql.append(" AND T.TABLE_NAME!=? ");
            params.add(tableName);
        }
        int count = this.getIntBySql(sql.toString(),
                params.toArray());
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 判断oracle数据库中是否存在该视图
     * @param viewName
     * @param isIncludeThisTable
     * @return
     */
    private boolean isExistsViewInOracle(String viewName,boolean isIncludeThisTable){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM USER_VIEWS ");
        sql.append("T WHERE T.VIEW_NAME=? ");
        List params = new ArrayList();
        params.add(viewName);
        if(!isIncludeThisTable){
            sql.append(" AND T.VIEW_NAME!=? ");
            params.add(viewName);
        }
        int count = this.getIntBySql(sql.toString(),
                params.toArray());
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 判断一个视图是否存在数据库中
     * @param viewName
     * @param isIncludeThisView
     * @return
     */
    public boolean isExistsView(String viewName,boolean isIncludeThisView){
        String dbType = PlatAppUtil.getDbType();
        if(dbType.equals("ORACLE")){
            return this.isExistsViewInOracle(viewName,isIncludeThisView);
        }else{
            return true;
        }
    }
    
    /**
     * 判断一张表是否存在mysql中
     * @param tableName
     * @return
     */
    private boolean isExistsTableInMySql(String tableName,boolean isIncludeThisTable){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM information_schema.TABLES ");
        sql.append("T WHERE T.TABLE_SCHEMA=? AND T.TABLE_NAME=? ");
        List params = new ArrayList();
        String dbSchema = PlatAppUtil.getDbSchema();
        params.add(dbSchema);
        params.add(tableName);
        if(!isIncludeThisTable){
            sql.append(" AND T.TABLE_NAME!=? ");
            params.add(tableName);
        }
        int count = this.getIntBySql(sql.toString(),
                params.toArray());
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 判断一张表是否存在sqlServer中
     * @param tableName
     * @param isIncludeThisTable
     * @return
     */
    private boolean isExistsTableInSqlServer(String tableName,boolean isIncludeThisTable){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) ");
        sql.append("from sysobjects U WHERE U.TYPE='U' AND U.NAME=? ");
        List params = new ArrayList();
        params.add(tableName);
        if(!isIncludeThisTable){
            sql.append(" AND U.NAME!=? ");
            params.add(tableName);
        }
        int count = this.getIntBySql(sql.toString(),
                params.toArray());
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 判断一张表是否存在数据库中
     * @param tableName:表名称
     * @param isIncludeThisTable:判断是否包含该传入参数表
     * @return
     */
    public boolean isExistsTable(String tableName,boolean isIncludeThisTable){
        String dbType = PlatAppUtil.getDbType();
        if(dbType.equals("ORACLE")){
            return this.isExistsTableInOracle(tableName, isIncludeThisTable);
        }else if(dbType.equals("MYSQL")){
            return this.isExistsTableInMySql(tableName, isIncludeThisTable);
        }else if(dbType.equals("SQLSERVER")){
            return this.isExistsTableInSqlServer(tableName, isIncludeThisTable);
        }else{
            return true;
        }
    }
    
    /**
     * 获取oracle的约束类型
     * @param tableName
     * @param columnName
     * @return
     */
    public String getOracleConstraintType(String tableName,String columnName){
        StringBuffer sql = new StringBuffer("SELECT AU.constraint_type FROM user_constraints AU");
        sql.append(" LEFT JOIN user_cons_columns CU ON cu.constraint_name=au.constraint_name");
        sql.append(" AND AU.table_name=CU.table_name WHERE AU.table_name=? ");
        sql.append(" AND AU.constraint_type IN ('P','R','U') AND CU.column_name=? ");
        String constraintType = null;
        try{
            Map<String,Object> info =  this.getBySql(sql.toString(),new Object[]{tableName,columnName});
            if(info!=null){
                constraintType = (String) info.get("CONSTRAINT_TYPE");
            }else{
                return null;
            }
        }catch(Exception e){
            PlatLogUtil.printStackTrace(e);
        }
        return constraintType;
    }
    
    /**
     * 获取oracle的表约束
     * @param tableName
     * @return
     */
    public List<Map<String,Object>> findOracleConstraints(String tableName){
        StringBuffer sql = new StringBuffer("SELECT AU.constraint_type,AU.CONSTRAINT_NAME");
        sql.append(" FROM user_constraints AU WHERE AU.table_name=?");
        sql.append(" AND AU.constraint_type IN ('P','R','U') ");
        List<Map<String,Object>> list = this.findBySql(sql.toString(), new Object[]{tableName}, null);
        return list;
    }
    
    /**
     * 根据列名称和表获取这个列的注释
     * @param tableNames
     * @param colName
     * @return
     */
    public String getColumnComments(List<String> tableNames,String colName){
        String dbType = PlatAppUtil.getDbType();
        if("ORACLE".equals(dbType)){
            StringBuffer sql = new StringBuffer("SELECT U.COMMENTS FROM user_col_comments U");
            sql.append(" where u.table_name in");
            String tableArray = PlatStringUtil.getSqlInCondition(tableNames.
                    toArray(new String[tableNames.size()]));
            sql.append(tableArray).append(" AND U.column_name=? ");
            List<String> list = this.getJdbcTemplate().queryForList(sql.toString(), 
                    new Object[]{colName},String.class);
            if(list!=null&&list.size()>0){
                return list.get(0);
            }else{
                return "";
            }
        }else if("MYSQL".equals(dbType)){
            String schemaName = PlatAppUtil.getDbSchema();
            StringBuffer sql = new StringBuffer("select t.COLUMN_COMMENT from");
            sql.append(" information_schema.COLUMNS t where t.TABLE_SCHEMA=? ");
            sql.append(" AND T.table_name IN ");
            String tableArray = PlatStringUtil.getSqlInCondition(tableNames.
                    toArray(new String[tableNames.size()]));
            sql.append(tableArray).append(" AND T.COLUMN_NAME=? ");
            List<String> list = this.getJdbcTemplate().queryForList(sql.toString(), 
                    new Object[]{schemaName,colName},String.class);
            if(list!=null&&list.size()>0){
                return list.get(0);
            }else{
                return "";
            }
        }else if("SQLSERVER".equals(dbType)){
            List<Object> params = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer("SELECT cast(C.value as varchar) AS COLUMN_COMMENT");
            sql.append(" FROM sys.columns B ");
            sql.append("LEFT JOIN information_schema.columns T ");
            sql.append("ON B.NAME=T.COLUMN_NAME LEFT JOIN sys.extended_properties C");
            sql.append("  ON C.major_id = B.object_id AND C.minor_id = B.column_id ");
            sql.append(" WHERE T.TABLE_NAME IN ");
            String tableArray = PlatStringUtil.getSqlInCondition(tableNames.
                    toArray(new String[tableNames.size()]));
            sql.append(tableArray).append(" AND T.COLUMN_NAME=? ");
            params.add(colName);
            sql.append(" order by T.ORDINAL_POSITION ASC");
            List<String> list = this.getJdbcTemplate().queryForList(sql.toString(), 
                    new Object[]{colName},String.class);
            if(list!=null&&list.size()>0){
                return list.get(0);
            }else{
                return "";
            }
        }else{
            return null;
        }
    }
}

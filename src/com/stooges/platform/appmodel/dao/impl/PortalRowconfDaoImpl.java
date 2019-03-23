/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.appmodel.dao.PortalRowconfDao;

/**
 * 描述行组件配置业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 15:47:00
 */
@Repository
public class PortalRowconfDaoImpl extends BaseDaoImpl implements PortalRowconfDao {

    /**
     * 获取组件配置数量
     * @param rowId
     * @return
     */
    public int getCompConfCount(String rowId){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append("PLAT_APPMODEL_PORTALROWCONF WHERE CONF_ROWID=?");
        return this.getIntBySql(sql.toString(), new Object[]{rowId});
    }
    
    /**
     * 判断是否存在该配置
     * @param THEME_ID 主题ID
     * @param CONF_ID 现有配置ID
     * @param CONF_COMPID 组件ID
     * @return
     */
    public boolean isExists(String THEME_ID,String CONF_ID,String CONF_COMPID){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append("PLAT_APPMODEL_PORTALROWCONF C WHERE C.CONF_COMPID=? ");
        sql.append("AND C.CONF_ID!=? AND C.CONF_ROWID IN (");
        sql.append("SELECT R.ROW_ID FROM PLAT_APPMODEL_PORTALROW R");
        sql.append(" WHERE R.ROW_THEMEID=? )");
        int count = this.getIntBySql(sql.toString(),
                new Object[]{CONF_COMPID,CONF_ID,THEME_ID});
        if(count!=0){
            return true;
        }else{
            return false;
        }
    }
}

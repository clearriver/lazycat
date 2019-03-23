/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.core.util.PlatCollectionUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.wsbs.dao.SermaterDao;

/**
 * 描述材料表业务相关dao实现类
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-18 10:40:57
 */
@Repository
public class SermaterDaoImpl extends BaseDaoImpl implements SermaterDao {
    /**
     * 根据事项ID获取最大排序
     * @param serItemId
     * @return
     */
    public int getMaxSn(String serItemId){
        StringBuffer sql = new StringBuffer("SELECT MAX(");
        sql.append("SERMATER_SN) FROM PLAT_WSBS_SERMATER WHERE ");
        sql.append("SERITEM_ID=? ");
        int maxSn = this.getIntBySql(sql.toString()
                ,new Object[]{serItemId});
        return maxSn+1;
    }
    
    /**
     * 描述 更新排序字段
     * @param materIds
     */
    public void updateSn(String[] materIds){
        int[] oldSns = new int[materIds.length];
        StringBuffer sql = new StringBuffer("select SERMATER_SN "
                + "FROM PLAT_WSBS_SERMATER ").append(" WHERE SERMATER_ID=? ");
        for (int i = 0; i < materIds.length; i++) {
            int dicSn = this.getIntBySql(sql.toString(), new Object[] { materIds[i] });
            oldSns[i] = dicSn;
        }
        int[] newSns = PlatCollectionUtil.sortByAsc(oldSns);
        StringBuffer updateSql = new StringBuffer("update PLAT_WSBS_SERMATER ")
                .append(" SET SERMATER_SN=? WHERE SERMATER_ID=? ");
        for (int i = 0; i < materIds.length; i++) {
            getJdbcTemplate().update(updateSql.toString(), new Object[] { newSns[i], materIds[i] });
        }
    }
    
    /**
     * 描述 修改绑定材料是否为必须提供
     * @param isneed
     * @param materIds
     */
    public void updateIsneed(String isneed, String materIds) {
        StringBuffer sql = new StringBuffer("update PLAT_WSBS_SERMATER");
        sql.append(" SET SERMATER_ISNEED = ? WHERE SERMATER_ID IN ");
        sql.append(PlatStringUtil.getValueArray(materIds));
        this.getJdbcTemplate().update(sql.toString(),new Object[]{isneed});
    }
}



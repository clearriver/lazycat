/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.workflow.dao.ButtonBindDao;

/**
 * 描述按钮绑定业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-07 09:49:51
 */
@Repository
public class ButtonBindDaoImpl extends BaseDaoImpl implements ButtonBindDao {

    /**
     * 获取配置的最大排序值
     * @param BTNBIND_FLOWDEFID
     * @param BTNBIND_FLOWVERSION
     * @return
     */
    public int getMaxSn(String BTNBIND_FLOWDEFID,String BTNBIND_FLOWVERSION){
        StringBuffer sql = new StringBuffer("SELECT MAX(T.BTNBIND_SN) ");
        sql.append("FROM JBPM6_BUTTONBIND T WHERE T.BTNBIND_FLOWDEFID=? ");
        sql.append(" AND T.BTNBIND_FLOWVERSION=? ");
        int maxSn = this.getIntBySql(sql.toString(),new Object[]{BTNBIND_FLOWDEFID,
            Integer.parseInt(BTNBIND_FLOWVERSION)});
        return maxSn;
    }
}

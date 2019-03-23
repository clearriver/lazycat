/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.appmodel.dao.SheduleDao;

/**
 * 描述定时任务业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-27 16:37:05
 */
@Repository
public class SheduleDaoImpl extends BaseDaoImpl implements SheduleDao {
    
    /**
     * 根据编码获取数量
     * @param sheduleCode
     * @return
     */
    public int getCountBySheduleCode(String sheduleCode){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append("PLAT_SYSTEM_SHEDULE S WHERE S.SHEDULE_CODE=?");
        int count = this.getIntBySql(sql.toString()
                ,new Object[]{sheduleCode});
        return count;
    }
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.system.dao.WorkdayDao;

/**
 * 描述工作日业务相关dao实现类
 * @author 李俊
 * @version 1.0
 * @created 2017-05-03 09:32:53
 */
@Repository
public class WorkdayDaoImpl extends BaseDaoImpl implements WorkdayDao {

    /**
     * 根据开始日期和结束日期获取工作日数量
     * @param beginDate
     * @param endDate
     * @return
     */
    public int getWorkDayCount(String beginDate,String endDate){
        StringBuffer sql = new StringBuffer("select count(*) from ");
        sql.append("PLAT_SYSTEM_WORKDAY W WHERE W.WORKDAY_DATE>?");
        sql.append(" AND W.WORKDAY_DATE<=? AND W.WORKDAY_SETID=2 ORDER BY W.WORKDAY_DATE ASC");
        int count = this.getIntBySql(sql.toString(),new Object[]{beginDate,endDate});
        return count;
    }
}

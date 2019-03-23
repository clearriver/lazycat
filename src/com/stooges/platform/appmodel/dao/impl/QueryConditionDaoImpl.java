/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.appmodel.dao.QueryConditionDao;

/**
 * 描述 QueryCondition业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-03-14 10:55:40
 */
@Repository
public class QueryConditionDaoImpl extends BaseDaoImpl implements QueryConditionDao {

    /**
     * 根据表单控件ID获取排序条件的最大值
     * @param formControlId
     * @return
     */
    public int getMaxSn(String formControlId){
        StringBuffer sql = new StringBuffer("select max(T.querycondition_sn)");
        sql.append(" from PLAT_APPMODEL_QUERYCONDITION T ");
        sql.append("WHERE T.QUERYCONDITION_FORMCONTROLID=?");
        int maxSn = this.getIntBySql(sql.toString(),
                new Object[]{formControlId});
        return maxSn;
    }
}

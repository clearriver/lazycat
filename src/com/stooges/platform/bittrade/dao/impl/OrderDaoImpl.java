/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.bittrade.dao.OrderDao;

/**
 * 描述委托单业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-16 18:12:13
 */
@Repository
public class OrderDaoImpl extends BaseDaoImpl implements OrderDao {

    /**
     * 获取止盈价列表
     * @param symbol
     * @param userCode
     * @return
     */
    public List<Double> findUserOrderSell(String symbol,String userCode){
        StringBuffer sql = new StringBuffer("SELECT T.ORDER_SELL");
        sql.append(" FROM PLAT_BITTRADE_ORDER T WHERE T.ORDER_USERCODE=?");
        sql.append(" AND T.ORDER_SYMBOL=? ORDER BY T.ORDER_SELL ASC");
        return this.getJdbcTemplate().queryForList(sql.toString(),
                Double.class, new Object[]{userCode,symbol});
    }
}

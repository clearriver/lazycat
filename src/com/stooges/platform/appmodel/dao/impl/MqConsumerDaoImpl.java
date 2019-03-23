/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.appmodel.dao.MqConsumerDao;

/**
 * 描述消息消费者业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-11-24 14:01:26
 */
@Repository
public class MqConsumerDaoImpl extends BaseDaoImpl implements MqConsumerDao {

    /**
     * 获取消息编码的订阅者
     * @param code
     * @return
     */
    public List<String> findTopicConsumer(String code){
        StringBuffer sql = new StringBuffer("SELECT T.MQCONSUMER_JAVA");
        sql.append(" FROM PLAT_APPMODEL_MQCONSUMER T WHERE ");
        sql.append(" T.MQCONSUMER_CODE=? ");
        List<String> list = this.getJdbcTemplate().queryForList(sql.toString(), 
                String.class, new Object[]{code});
        return list;
    }
}

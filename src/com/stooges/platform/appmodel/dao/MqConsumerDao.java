/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao;

import java.util.List;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 消息消费者业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-11-24 14:01:26
 */
public interface MqConsumerDao extends BaseDao {
    
    /**
     * 获取消息编码的订阅者
     * @param code
     * @return
     */
    public List<String> findTopicConsumer(String code);
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.List;

import com.stooges.core.service.BaseService;

/**
 * 描述 消息消费者业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-11-24 14:01:26
 */
public interface MqConsumerService extends BaseService {
    
    /**
     * 获取消息编码的订阅者
     * @param code
     * @return
     */
    public List<String> findTopicConsumer(String code);
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.appmodel.dao.MqConsumerDao;
import com.stooges.platform.appmodel.service.MqConsumerService;

/**
 * 描述 消息消费者业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-11-24 14:01:26
 */
@Service("mqConsumerService")
public class MqConsumerServiceImpl extends BaseServiceImpl implements MqConsumerService {

    /**
     * 所引入的dao
     */
    @Resource
    private MqConsumerDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取消息编码的订阅者
     * @param code
     * @return
     */
    public List<String> findTopicConsumer(String code){
        return dao.findTopicConsumer(code);
    }
  
}

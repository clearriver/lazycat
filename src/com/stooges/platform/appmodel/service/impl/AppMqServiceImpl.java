/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.mq.QueueSender;
import com.stooges.core.mq.TopicSender;
import com.stooges.platform.appmodel.service.AppMqService;

/**
 * @author 胡裕
 *
 * 
 */
@Service("appMqService")
public class AppMqServiceImpl implements AppMqService {
    /**
     * 
     */
    @Autowired   
    private QueueSender queueSender;  
    /**
     * 
     */
    @Autowired   
    private TopicSender topicSender;  
    /**
     * 发送队列消息
     * @param messageCode
     * @param messageContent
     */
    public void sendQueueMessage(String messageCode,String messageContent){
        Map<String,String> content= new HashMap<String,String>();
        content.put("messageCode", messageCode);
        content.put("messageContent", messageContent);
        String json = JSON.toJSONString(content);
        queueSender.send("platqueue", json);
    }
    
    /**
     * 发送订阅消息
     * @param messageCode
     * @param messageContent
     */
    public void sendTopicMessage(String messageCode,String messageContent){
        Map<String,String> content= new HashMap<String,String>();
        content.put("messageCode", messageCode);
        content.put("messageContent", messageContent);
        String json = JSON.toJSONString(content);
        topicSender.send("plattopic", json);
    }
}

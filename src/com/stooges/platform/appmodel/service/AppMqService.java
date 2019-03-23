/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

/**
 * @author 胡裕
 *
 * 
 */
public interface AppMqService {
    /**
     * 发送队列消息
     * @param messageCode
     * @param messageContent
     */
    public void sendQueueMessage(String messageCode,String messageContent);
    
    /**
     * 发送订阅消息
     * @param messageCode
     * @param messageContent
     */
    public void sendTopicMessage(String messageCode,String messageContent);
}

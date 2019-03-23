/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.listener;

import java.lang.reflect.Method;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.platform.appmodel.service.MqConsumerService;
import com.stooges.platform.demo.service.LeaveInfoService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;

/**
 * @author 胡裕
 * 通用队列消息消费者
 * 
 */
@Component
public class CommonQueueReceiver implements MessageListener {
    
    /**
     * 
     */
    @Override
    public void onMessage(Message message) {
        try {
            String content = ((TextMessage)message).getText();
            Map info = JSON.parseObject(content, Map.class);
            String messageCode = (String) info.get("messageCode");
            String messageContent = (String) info.get("messageContent");
            MqConsumerService mqConsumerService = (MqConsumerService) PlatAppUtil
                    .getBean("mqConsumerService");
            Map<String,Object> consumer = mqConsumerService.getRecord("PLAT_APPMODEL_MQCONSUMER",
                    new String[]{"MQCONSUMER_TYPE","MQCONSUMER_CODE"},new Object[]{1,messageCode});
            if(consumer!=null){
                String eventCode = (String) consumer.get("MQCONSUMER_JAVA");
                String beanId = eventCode.split("[.]")[0];
                String method = eventCode.split("[.]")[1];
                Object serviceBean = PlatAppUtil.getBean(beanId);
                if (serviceBean != null) {
                    Method invokeMethod;
                    try {
                        invokeMethod = serviceBean.getClass().getDeclaredMethod(method,
                                new Class[] {String.class});
                        invokeMethod.invoke(serviceBean,
                                new Object[] {messageContent});
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
    }

}

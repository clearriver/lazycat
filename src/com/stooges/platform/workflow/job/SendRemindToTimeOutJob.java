/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.workflow.service.JbpmService;
import com.stooges.platform.workflow.service.RemindsService;

/**
 * @author 胡裕
 * 发送流程催办信息到超期的任务人手上
 * 
 */
public class SendRemindToTimeOutJob implements Job {

    /**
     * 
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // TODO Auto-generated method stub
        RemindsService remindsService = (RemindsService) PlatAppUtil.getBean("remindsService");
        remindsService.sendAutoReminds();
    }

}

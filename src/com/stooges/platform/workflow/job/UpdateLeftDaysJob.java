/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.workflow.service.JbpmService;

/**
 * 描述
 * @author 胡裕
 * @created 2017年6月3日 下午5:35:55
 */
public class UpdateLeftDaysJob implements Job {

    /**
     * 
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JbpmService jbpmService = (JbpmService) PlatAppUtil.getBean("jbpmService");
        jbpmService.updateTimeLimit();
    }

}

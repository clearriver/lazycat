/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.appmodel.service.JobLogService;

/**
 * 描述
 * @author 胡裕
 * @created 2017年4月29日 下午2:38:36
 */
public class QuarzJobListener implements JobListener {
    /**
     * 日志工具
     */
    private static Log logger = LogFactory.getLog(QuarzJobListener.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.quartz.JobListener#getName()
     */
    @Override
    public String getName() {
        return getClass().getSimpleName();

    }
    /**
     * 在被执行之前调用该方法
     */
    public void jobToBeExecuted(JobExecutionContext context) {

    }

    public void jobExecutionVetoed(JobExecutionContext context) {

    }
    /**
     * 在被执行之后调用该方法
     */
    public void jobWasExecuted(JobExecutionContext context,
            JobExecutionException jobException) {
        String jobName = context.getJobDetail().getName();
        JobLogService jobLogService = (JobLogService)PlatAppUtil.getBean("jobLogService");
        jobLogService.saveJobLog(jobName);
        //logger.info(jobName + " was executed");
    }

}

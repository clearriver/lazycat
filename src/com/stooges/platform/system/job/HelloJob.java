/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.job;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stooges.core.util.PlatDateTimeUtil;

/**
 * 描述
 * @author 胡裕
 * @created 2017年4月29日 下午2:25:53
 */
public class HelloJob implements Job {
    
    /**
     * 日志工具
     */
    private static Log logger = LogFactory.getLog(HelloJob.class);

    /* (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // TODO Auto-generated method stub
        logger.info("调用了HelloJob:当前时间是:"+PlatDateTimeUtil.
                formatDate(new Date(), "YYYY-MM-dd HH:mm:ss"));
    }

}

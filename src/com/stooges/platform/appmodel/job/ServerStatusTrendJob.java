/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.job;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.platform.appmodel.service.ServerStatusService;
import com.stooges.platform.bittrade.service.OrderService;

/**
 * 
 * @author 李俊
 *
 *
 */
public class ServerStatusTrendJob implements Job {
    
    /**
     * 日志工具
     */
    private static Log logger = LogFactory.getLog(ServerStatusTrendJob.class);

    /**
     * 
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	/*logger.info("开始收集服务器信息:"+PlatDateTimeUtil.
                formatDate(new Date(), "YYYY-MM-dd HH:mm:ss"));*/
    	ServerStatusService serverStatusService = (ServerStatusService) PlatAppUtil.getBean("serverStatusService");
    	serverStatusService.saveTrend();
    	/*logger.info("结束收集服务器信息:"+PlatDateTimeUtil.
                formatDate(new Date(), "YYYY-MM-dd HH:mm:ss"));*/
    }

}

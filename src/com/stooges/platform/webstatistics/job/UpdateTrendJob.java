/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.job;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.platform.webstatistics.service.SiteInfoService;

/**
 * 描述同步趋势数据
 * @author 李俊
 * @created 2017年4月29日 下午2:25:53
 */
public class UpdateTrendJob implements Job {
    
    /**
     * 日志工具
     */
    private static Log logger = LogFactory.getLog(UpdateTrendJob.class);

    /* (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        // TODO Auto-generated method stub
        Date preDate = PlatDateTimeUtil.getNextTime(new Date(), Calendar.HOUR, -1);
        String preDateStr = PlatDateTimeUtil.formatDate(preDate, "yyyy-MM-dd");
        SiteInfoService siteInfoService = (SiteInfoService) PlatAppUtil.getBean("siteInfoService");
        siteInfoService.saveSiteMapTrendInfo(preDateStr);
    }

}

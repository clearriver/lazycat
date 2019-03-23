/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.metadata.job;

import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.appmodel.service.RedisService;
import com.stooges.platform.metadata.service.DataSerService;

/**
 * @author 胡裕
 *
 * 
 */
public class ClearRedisCacheJob implements Job {

    /* (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        DataSerService dataService = (DataSerService) PlatAppUtil.getBean("dataSerService");
        RedisService redisService = (RedisService) PlatAppUtil.getBean("redisService");
        List<Map<String,Object>> list = dataService.findAutoClearList();
        for(Map<String,Object> data:list){
            String DATASER_CODE = (String) data.get("DATASER_CODE");
            redisService.del(DataSerService.CACHEPRE_CODE+DATASER_CODE);
        }
    }

}

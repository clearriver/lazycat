/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.bittrade.service.CoinCostService;
import com.stooges.platform.bittrade.service.PriceRemindService;

/**
 * @author 胡裕
 * 提醒价格定时器
 * 
 */
public class RemindPriceJob implements Job {

    /* (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        PriceRemindService priceRemindService = (PriceRemindService) 
                PlatAppUtil.getBean("priceRemindService");
        priceRemindService.remindPrice();
        //更新价格
        CoinCostService coinCostService = (CoinCostService) 
                PlatAppUtil.getBean("coinCostService");
        coinCostService.updateCostInfo();
    }

}

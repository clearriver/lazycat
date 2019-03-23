/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import com.stooges.core.service.BaseService;

/**
 * 描述 定时器日志业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-29 16:05:22
 */
public interface JobLogService extends BaseService {
    /**
     * 根据定时器编码保存日志
     * @param sheduleCode
     */
    public void saveJobLog(String sheduleCode);
}

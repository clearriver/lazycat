/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service;

import com.stooges.core.service.BaseService;

/**
 * 描述 入口数据业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 17:50:00
 */
public interface InviewService extends BaseService {
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年7月27日 下午5:53:37
     * @param date
     */
    public void saveInviewData(String date,String staticSiteId);
}

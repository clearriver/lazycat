/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.service;

import com.stooges.core.service.BaseService;

/**
 * 描述 访问来源业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 11:36:14
 */
public interface DomainService extends BaseService {
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年7月27日 上午11:37:21
     * @param date
     */
    public void saveDomainData(String date,String staticSiteId);
}

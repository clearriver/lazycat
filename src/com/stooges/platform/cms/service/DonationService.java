/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 捐赠信息业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-30 11:03:13
 */
public interface DonationService extends BaseService {
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月22日 上午11:56:43
     * @return
     */
    public List<Map<String,Object>> findAllList();
}

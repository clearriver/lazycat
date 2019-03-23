/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service;

import com.stooges.core.service.BaseService;

/**
 * 描述 关注欢迎语业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 15:30:22
 */
public interface WelcomingService extends BaseService {
    /**
     * 获取欢迎语内容
     * @param pubSourceId
     * @return
     */
    public String getWelcomingContent(String pubSourceId);
}

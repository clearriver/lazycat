/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service;

import com.stooges.core.service.BaseService;

/**
 * 描述 催办信息业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-11-16 08:50:10
 */
public interface RemindsService extends BaseService {
    
    /**
     * 发送催办意见
     * @param REMINDS_EXEIDS
     * @param REMINDS_CONTENT
     * @return
     */
    public boolean sendReminds(String REMINDS_EXEIDS,String REMINDS_CONTENT);
    /**
     * 自动发送催办
     */
    public void sendAutoReminds();
}

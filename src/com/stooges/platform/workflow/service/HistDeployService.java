/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service;

import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 流程历史部署业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-04 14:34:24
 */
public interface HistDeployService extends BaseService {
    /**
     * 保存历史部署信息
     * @param oldFlowDef
     */
    public void saveHistDeploy(Map<String,Object> oldFlowDef);
}

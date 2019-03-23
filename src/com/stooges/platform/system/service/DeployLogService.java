/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service;

import com.stooges.core.service.BaseService;

/**
 * 描述 部署日志业务相关service
 * @author HuYu
 * @version 1.0
 * @created 2018-04-19 17:16:44
 */
public interface DeployLogService extends BaseService {
    /**
     * 保存部署记录信息
     * @param email
     * @param name
     * @param pass
     * @param nodeIds
     * @param jarPath
     */
    public void saveDeployLog(String email,String name,String pass,
            String nodeIds,String jarPath,String fileJson);
}

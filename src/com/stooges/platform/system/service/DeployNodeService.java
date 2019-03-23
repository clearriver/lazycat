/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 部署节点列表业务相关service
 * @author HuYu
 * @version 1.0
 * @created 2018-04-19 16:15:13
 */
public interface DeployNodeService extends BaseService {
    /**
     * 获取部署节点列表
     * @param param
     * @return
     */
    public List<Map<String,Object>> findList(String param);
}

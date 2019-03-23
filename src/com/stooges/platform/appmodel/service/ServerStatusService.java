/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 服务器硬件监控信息业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2018-03-20 16:00:36
 */
public interface ServerStatusService extends BaseService {

    /**
     * @param nodes
     * @return
     */
    public Map<String, Object> postGetIntervalInfo(String nodes);

    /**
     * @return
     */
    public Map<String, Object> getServerStatusInfo();

    /**
     * @param nodes
     * @return
     */
    public Map<String, Object> postGetServerStatusInfo(String nodes);

	/**
	 * 保存系统信息情况
	 */
	public void saveTrend();
    
}

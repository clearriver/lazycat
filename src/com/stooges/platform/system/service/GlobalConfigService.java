/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service;

import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 全局配置业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2018-05-09 13:52:03
 */
public interface GlobalConfigService extends BaseService {

	/**
	 * @return
	 */
	public Map<String, Object> getFirstConfigMap();
    
}

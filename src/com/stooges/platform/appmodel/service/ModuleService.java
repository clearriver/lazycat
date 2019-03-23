/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import com.stooges.core.service.BaseService;

/**
 * 描述
 * @author 胡裕
 * @created 2017年2月4日 上午9:22:58
 */
public interface ModuleService extends BaseService {

    /**
     * 删除模块信息，并且级联更新设计的编码
     * @param moduleId
     */
    public void deleteModuleCascade(String moduleId);
    
}

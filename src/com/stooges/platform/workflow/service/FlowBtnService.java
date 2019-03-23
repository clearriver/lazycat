/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 流程按钮业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-07 07:09:57
 */
public interface FlowBtnService extends BaseService {
    /**
     * 获取可选列表的按钮
     * @param param
     * @return
     */
    public List<Map<String,Object>> findByForSelect(String param);
}

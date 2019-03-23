/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;
import com.stooges.platform.workflow.model.FlowNextHandler;

/**
 * 描述 实例预设办理人业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-26 09:04:38
 */
public interface ExePresetService extends BaseService {
    /**
     * 获取预设人员列表
     * @param sqlFilter
     * @return
     */
    public List<Map> findPresetList(SqlFilter sqlFilter);
    /**
     * 获取流程预设办理人列表
     * @param exeId
     * @param nodeKey
     * @return
     */
    public List<FlowNextHandler> findPresetHandler(String exeId,String nodeKey);
}

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
 * 描述 流程绑定文书业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2017-06-02 15:52:39
 */
public interface WordBindService extends BaseService {
    /**
     * 获取可绑定模版
     * @param param
     * @return
     */
    public List<Map<String,Object>> findForSelect(String param);
    /**
     * 根据流程定义获取列表数据
     * @param defId
     * @return
     */
    public List<Map<String,Object>> findByDefId(String defId);
    /**
     * 
     * @param defId
     * @return
     */
    public List<Map<String,Object>> findFilesList(Map<String,Object> map);
    /**
     * 
     * @param filePath
     */
    public void saveWordAfterCallClient(String filePath);
}

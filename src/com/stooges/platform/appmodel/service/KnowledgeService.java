/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 技术知识业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-29 09:57:28
 */
public interface KnowledgeService extends BaseService {
    /**
     * 保存或者更新并且级联索引
     * @param knowledage
     * @return
     */
    public Map<String,Object> saveOrUpdateCascadeIndex(Map<String,Object> knowledage);
    /**
     * 删除并且级联删除索引
     * @param knowledageIds
     */
    public void deleteCascadeIndex(String[] knowledageIds);
}

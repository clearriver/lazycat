/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 事项申报材料列表业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2017-05-18 11:02:38
 */
public interface MaterialsService extends BaseService {

    /** 获取事项需要上传材料列表
     * @param map
     * @return
     */
    public List<Map<String, Object>> findFilesList(Map<String,Object> map);
    
}

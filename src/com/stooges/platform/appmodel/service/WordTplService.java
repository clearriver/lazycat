/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 WORD模版业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-31 10:59:58
 */
public interface WordTplService extends BaseService {
    /**
     * 根据模版编码和参数生成模版
     * @param tplCode
     * @param params
     * @return
     */
    public String genWordByTplCodeAndParams(String tplCode,Map<String,Object> params);
}

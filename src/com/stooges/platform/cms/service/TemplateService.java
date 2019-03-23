/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.service.BaseService;

/**
 * 描述 模版业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-03 17:19:57
 */
public interface TemplateService extends BaseService {
    /**
     * 获取可选模版列表
     * @param param
     * @return
     */
    public List<Map<String,Object>> findForSelect(String param);
    /**
     * 设置首页的数据
     * @param request
     * @return
     */
    public Map<String,Object> getIndexData(HttpServletRequest request);
}

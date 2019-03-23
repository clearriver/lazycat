/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 站点业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-03 19:10:38
 */
public interface WebSiteService extends BaseService {
    /**
     * 可选站点下拉框数据源
     * @param param
     * @return
     */
    public List<Map<String,Object>> findForSelect(String param);
    /**
     * 删除网站静态首页
     * @param siteIds
     */
    public void deleteIndexHtmls(String siteIds);
}

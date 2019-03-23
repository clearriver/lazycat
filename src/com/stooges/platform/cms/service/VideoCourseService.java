/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 模版业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-03 17:19:57
 */
public interface VideoCourseService extends BaseService {
    /**
     * 获取下一个排序值
     * @return
     */
    public int getNextSn();

    /**
     * 描述
     * @created 2017年8月11日 下午9:06:21
     * @param filter
     * @return
     */
    public List<Map<String, Object>> getVideoCourse(SqlFilter filter);

    /**
     * 描述
     * @created 2017年8月17日 下午5:06:05
     * @return
     */
    public int getVideoClickNum();
}

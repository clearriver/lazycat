/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 缓存配置业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2017-05-03 16:14:24
 */
public interface SysEhcacheService extends BaseService {
    /**
     * 获取可编辑表格数据
     * @param filter
     * @return
     */
    public List<Map> findTableColumnByFilter(SqlFilter filter);

    /**
     * @param string
     * @return
     */
    public List<Map<String, Object>> findByStatue(String statue);

    /**
     * @param string
     * @return
     */
    public Set<String> findDelListByStatue(String string);

    /** 
     * @param classname
     * @return
     */
    public List<Map<String, Object>> findRefreshList(String classname);

    /**
     * @param selectColValues
     */
    public void manualReloadEhcache(String selectColValues);

    /**
     * @param selectColValues
     * @param string
     */
    public void updateEhcacheStatue(String selectColValues, String statue);

}

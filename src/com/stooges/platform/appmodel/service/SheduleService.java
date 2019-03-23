/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 定时任务业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-27 16:37:05
 */
public interface SheduleService extends BaseService {
    /**
     * 根据编码判断是否存在记录
     * @param sheduleCode
     * @return
     */
    public boolean isExistsShedule(String sheduleCode);
    /**
     * 更新定时器的状态
     * @param sheduleIds
     * @param status
     */
    public void updateStatus(String sheduleIds,int status);
    /***
     * 启动定时调度池
     */
    public void startAllShedule();
    /**
     * 根据状态获取列表数据
     * @param status
     * @return
     */
    public List<Map<String,Object>> findByByStatus(int status);
    /**
     * 级联删除定时调度
     * @param sheduleIds
     */
    public void deleteCascadeJob(String sheduleIds);
    /**
     * 
     * @param sheduleInfo
     * @return
     */
    public Map<String,Object> saveOrUpdateCascadeJob(Map<String,Object> sheduleInfo);
}

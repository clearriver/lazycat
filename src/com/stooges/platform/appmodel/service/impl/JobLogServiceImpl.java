/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.platform.appmodel.dao.JobLogDao;
import com.stooges.platform.appmodel.service.JobLogService;

/**
 * 描述 定时器日志业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-29 16:05:22
 */
@Service("jobLogService")
public class JobLogServiceImpl extends BaseServiceImpl implements JobLogService {

    /**
     * 所引入的dao
     */
    @Resource
    private JobLogDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据定时器编码保存日志
     * @param sheduleCode
     */
    public void saveJobLog(String sheduleCode){
        Map<String,Object> shedule = dao.getRecord("PLAT_SYSTEM_SHEDULE",
                new String[]{"SHEDULE_CODE"}, new Object[]{sheduleCode});
        if(shedule!=null){
            String SHEDULE_ID = (String) shedule.get("SHEDULE_ID");
            Map<String,Object> log = new HashMap<String,Object>();
            log.put("JOBLOG_SHEDULEID", SHEDULE_ID);
            log.put("JOBLOG_EXETIME",PlatDateTimeUtil.formatDate(new Date(), 
                    "yyyy-MM-dd HH:mm:ss"));
            dao.saveOrUpdate("PLAT_SYSTEM_JOBLOG",log,AllConstants.IDGENERATOR_UUID,null);
        }
    }
  
}

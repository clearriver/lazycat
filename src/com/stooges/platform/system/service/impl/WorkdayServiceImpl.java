/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.platform.system.dao.WorkdayDao;
import com.stooges.platform.system.service.WorkdayService;

/**
 * 描述 工作日业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2017-05-03 09:32:53
 */
@Service("workdayService")
public class WorkdayServiceImpl extends BaseServiceImpl implements WorkdayService {

    /**
     * 所引入的dao
     */
    @Resource
    private WorkdayDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

    /**
     * 获取日期数据
     */
    @Override
    public List<Map<String, Object>> findDataBySqlFilter(SqlFilter sqlFilter) {
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer("select * from PLAT_SYSTEM_WORKDAY T ");
        String exeSql = dao.getQuerySql(sqlFilter, sql.toString(), params);
        List<Map<String, Object>> list = dao.findBySql(exeSql, params.toArray(), null);
        return list;
    }
    /**
     * 测试添加缓存
     */
    @Override
    public String testAdd(String formatDate) {
        PlatLogUtil.println("缓存产生时间:"+PlatDateTimeUtil.formatDate(new Date(), "YYYY-MM-dd HH:mm:ss:mss"));
        return PlatDateTimeUtil.formatDate(new Date(), "YYYY-MM-dd HH:mm:ss:mss");
    }

    /**
     * 
     */
    @Override
    public void testDel() {
        PlatLogUtil.println("缓存删除。"+PlatDateTimeUtil.formatDate(new Date(), "YYYY-MM-DD hh:mm:ss:mss"));
    }
    
    /**
     * 根据开始日期和结束日期获取工作日数量
     * @param beginDate
     * @param endDate
     * @return
     */
    public int getWorkDayCount(String beginDate,String endDate){
        return dao.getWorkDayCount(beginDate, endDate);
    }

}

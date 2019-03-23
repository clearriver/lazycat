/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.wsbs.dao.SercatalogDao;
import com.stooges.platform.wsbs.service.SercatalogService;

/**
 * 描述 目录表业务相关service实现类
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-15 11:26:44
 */
@Service("sercatalogService")
public class SercatalogServiceImpl extends BaseServiceImpl implements SercatalogService {

    /**
     * 所引入的dao
     */
    @Resource
    private SercatalogDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

    /**
     * 获取服务事项目录列表数据源
     * @param queryParamJson
     * @return
     */
    public List<Map<String,Object>> findCatalogList(String queryParamJson){
        StringBuffer sql = new StringBuffer("select T.SERCATALOG_ID AS VALUE,T.SERCATALOG_NAME AS LABEL ");
        sql.append("from PLAT_WSBS_SERCATALOG T ");
        
        sql.append(" ORDER BY T.SERCATALOG_CREATETIME ASC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), null, null);
        if(list==null){
            list = new ArrayList<Map<String,Object>>();
        }
        Map<String,Object> allType = new HashMap<String,Object>();
        allType.put("LABEL","全部服务事项目录");
        allType.put("VALUE","0");
        list.add(0, allType);
        return list;
    }
  
}

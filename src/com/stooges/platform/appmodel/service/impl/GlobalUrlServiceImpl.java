/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.appmodel.dao.GlobalUrlDao;
import com.stooges.platform.appmodel.service.GlobalUrlService;

/**
 * 描述 全局URL业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-23 09:22:54
 */
@Service("globalUrlService")
public class GlobalUrlServiceImpl extends BaseServiceImpl implements GlobalUrlService {

    /**
     * 所引入的dao
     */
    @Resource
    private GlobalUrlDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据过滤类型获取全局URL列表
     * @param filterType
     * @return
     */
    public List<String> findByFilterType(String filterType){
        return dao.findByFilterType(filterType);
    }
    
    /**
     * 获取全部的可匿名访问的URL
     * @return
     */
    public Set<String> getAllAnonUrl(){
        Set<String> grantUrlSet = new HashSet<String>();
        //获取公共URL权限
        List<String> filterUrls = this.findByFilterType("1");
        grantUrlSet.addAll(filterUrls);
        return grantUrlSet;
    }
  
}

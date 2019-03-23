/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.appmodel.dao.InvokelogDao;
import com.stooges.platform.appmodel.service.InvokelogService;

/**
 * 描述 服务调用日志业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-08-03 18:41:18
 */
@Service("invokelogService")
public class InvokelogServiceImpl extends BaseServiceImpl implements InvokelogService {

    /**
     * 所引入的dao
     */
    @Resource
    private InvokelogDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

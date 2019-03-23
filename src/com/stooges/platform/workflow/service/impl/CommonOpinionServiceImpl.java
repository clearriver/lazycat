/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.workflow.dao.CommonOpinionDao;
import com.stooges.platform.workflow.service.CommonOpinionService;

/**
 * 描述 常用意见业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-31 09:04:45
 */
@Service("commonOpinionService")
public class CommonOpinionServiceImpl extends BaseServiceImpl implements CommonOpinionService {

    /**
     * 所引入的dao
     */
    @Resource
    private CommonOpinionDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

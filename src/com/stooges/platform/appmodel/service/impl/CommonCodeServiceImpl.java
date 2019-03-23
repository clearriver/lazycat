/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.appmodel.dao.CommonCodeDao;
import com.stooges.platform.appmodel.service.CommonCodeService;

/**
 * 描述 代码块业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-14 09:15:53
 */
@Service("commonCodeService")
public class CommonCodeServiceImpl extends BaseServiceImpl implements CommonCodeService {

    /**
     * 所引入的dao
     */
    @Resource
    private CommonCodeDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

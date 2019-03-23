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
import com.stooges.platform.appmodel.dao.EmailLogDao;
import com.stooges.platform.appmodel.service.EmailLogService;

/**
 * 描述 邮件发送日志业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-20 15:45:42
 */
@Service("emailLogService")
public class EmailLogServiceImpl extends BaseServiceImpl implements EmailLogService {

    /**
     * 所引入的dao
     */
    @Resource
    private EmailLogDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.iguarantee.dao.ApplyDao;
import com.stooges.platform.iguarantee.service.ApplyService;

/**
 * 描述 实地调查表业务相关service实现类
 * @author river
 * @version 1.0
 * @created 2019-08-18 20:44:57
 */
@Service("applyService")
public class ApplyServiceImpl extends BaseServiceImpl implements ApplyService {

    /**
     * 所引入的dao
     */
    @Resource
    private ApplyDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

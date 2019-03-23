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
import com.stooges.platform.iguarantee.dao.FinfoDao;
import com.stooges.platform.iguarantee.service.FinfoService;

/**
 * 描述 基本情况业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2019-03-14 12:42:17
 */
@Service("finfoService")
public class FinfoServiceImpl extends BaseServiceImpl implements FinfoService {

    /**
     * 所引入的dao
     */
    @Resource
    private FinfoDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

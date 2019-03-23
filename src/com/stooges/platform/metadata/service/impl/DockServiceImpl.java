/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.metadata.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.metadata.dao.DockDao;
import com.stooges.platform.metadata.service.DockService;

/**
 * 描述 服务对接申请业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2018-05-09 14:23:14
 */
@Service("dockService")
public class DockServiceImpl extends BaseServiceImpl implements DockService {

    /**
     * 所引入的dao
     */
    @Resource
    private DockDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

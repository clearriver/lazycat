/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.system.dao.DevManDao;
import com.stooges.platform.system.service.DevManService;

/**
 * 描述 开发者信息业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2018-04-19 14:37:38
 */
@Service("devManService")
public class DevManServiceImpl extends BaseServiceImpl implements DevManService {

    /**
     * 所引入的dao
     */
    @Resource
    private DevManDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
}

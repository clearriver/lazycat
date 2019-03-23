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
import com.stooges.platform.iguarantee.dao.LiabilitiesDao;
import com.stooges.platform.iguarantee.service.LiabilitiesService;

/**
 * 描述 负债情况业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2019-03-14 11:31:06
 */
@Service("liabilitiesService")
public class LiabilitiesServiceImpl extends BaseServiceImpl implements LiabilitiesService {

    /**
     * 所引入的dao
     */
    @Resource
    private LiabilitiesDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

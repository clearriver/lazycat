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
import com.stooges.platform.iguarantee.dao.ReviewDao;
import com.stooges.platform.iguarantee.service.ReviewService;

/**
 * 描述 合规性审查业务相关service实现类
 * @author river
 * @version 1.0
 * @created 2019-08-18 15:14:41
 */
@Service("reviewService")
public class ReviewServiceImpl extends BaseServiceImpl implements ReviewService {

    /**
     * 所引入的dao
     */
    @Resource
    private ReviewDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

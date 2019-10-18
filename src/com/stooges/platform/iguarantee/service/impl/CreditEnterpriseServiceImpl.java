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
import com.stooges.platform.iguarantee.dao.CreditEnterpriseDao;
import com.stooges.platform.iguarantee.service.CreditEnterpriseService;

/**
 * 描述 企业客户信用评分表模型业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2019-08-05 23:45:25
 */
@Service("creditEnterpriseService")
public class CreditEnterpriseServiceImpl extends BaseServiceImpl implements CreditEnterpriseService {

    /**
     * 所引入的dao
     */
    @Resource
    private CreditEnterpriseDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

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
import com.stooges.platform.iguarantee.dao.PersonDao;
import com.stooges.platform.iguarantee.service.PersonService;

/**
 * 描述 个人资料业务相关service实现类
 * @author river
 * @version 1.0
 * @created 2019-09-05 00:31:15
 */
@Service("personService")
public class PersonServiceImpl extends BaseServiceImpl implements PersonService {

    /**
     * 所引入的dao
     */
    @Resource
    private PersonDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

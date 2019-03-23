/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.${PACK_NAME}.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.${PACK_NAME}.dao.${CLASS_NAME}Dao;
import com.stooges.platform.${PACK_NAME}.service.${CLASS_NAME}Service;

/**
 * 描述 ${CN_NAME}业务相关service实现类
 * @author ${AUTHOR}
 * @version 1.0
 * @created ${FILE_CREATETIME}
 */
@Service("${CLASS_NAME?uncap_first}Service")
public class ${CLASS_NAME}ServiceImpl extends BaseServiceImpl implements ${CLASS_NAME}Service {

    /**
     * 所引入的dao
     */
    @Resource
    private ${CLASS_NAME}Dao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}

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
import com.stooges.platform.system.dao.DeployNodeDao;
import com.stooges.platform.system.service.DeployNodeService;

/**
 * 描述 部署节点列表业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2018-04-19 16:15:13
 */
@Service("deployNodeService")
public class DeployNodeServiceImpl extends BaseServiceImpl implements DeployNodeService {

    /**
     * 所引入的dao
     */
    @Resource
    private DeployNodeDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取部署节点列表
     * @param param
     * @return
     */
    public List<Map<String,Object>> findList(String param){
        StringBuffer sql = new StringBuffer("SELECT T.DEPLOYNODE_ID AS VALUE");
        sql.append(",T.DEPLOYNODE_NAME AS LABEL FROM PLAT_SYSTEM_DEPLOYNODE");
        sql.append(" T ORDER BY T.DEPLOYNODE_TIME ASC");
        return dao.findBySql(sql.toString(), null, null);
    }
  
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.platform.workflow.dao.HistDeployDao;
import com.stooges.platform.workflow.service.HistDeployService;

/**
 * 描述 流程历史部署业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-04 14:34:24
 */
@Service("histDeployService")
public class HistDeployServiceImpl extends BaseServiceImpl implements HistDeployService {

    /**
     * 所引入的dao
     */
    @Resource
    private HistDeployDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

    /**
     * 保存历史部署信息
     * @param oldFlowDef
     */
    public void saveHistDeploy(Map<String,Object> oldFlowDef){
        Map<String,Object> deploy =new HashMap<String,Object>();
        deploy.putAll(oldFlowDef);
        dao.saveOrUpdate("JBPM6_HIST_DEPLOY",deploy,AllConstants.IDGENERATOR_UUID,null);
    }
}

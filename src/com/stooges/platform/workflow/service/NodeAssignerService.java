/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.model.NodeAssigner;

/**
 * 描述 下一环节办理人业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-10 17:05:56
 */
public interface NodeAssignerService extends BaseService {
    /**
     * 获取节点配置的办理人信息
     * @param defId
     * @param defVersion
     * @param nodeKey
     * @param nextNodeKey
     * @return
     */
    public NodeAssigner getNodeAssigner(String nextNodeKey,Map<String,Object> postParams,JbpmFlowInfo jbpmFlowInfo);
    
    /**
     * 克隆节点办理人配置表
     * @param oldFlowDefId
     * @param oldFlowDefVersion
     */
    public void copyNodeAssigner(String oldFlowDefId,int oldFlowDefVersion,
            String newFlowDefId,int newFlowDefVersion);
    /**
     * 获取定义ID和版本号列表
     * @param defId
     * @param flowVersion
     * @return
     */
    public List<Map<String,Object>> findByDefIdAndVesion(String defId,int flowVersion);
    
    
}

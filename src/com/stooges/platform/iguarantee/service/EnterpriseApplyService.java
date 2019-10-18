/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.service;

import java.util.Map;

import com.stooges.core.service.BaseService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;

/**
 * 描述 企业委托担保申请信息表业务相关service
 * @author HuYu
 * @version 1.0
 * @created 2019-08-05 00:18:35
 */
public interface EnterpriseApplyService extends BaseService {
    public void genSaveEnterpriseApplyData(Map<String,Object> postParams,JbpmFlowInfo jbpmFlowInfo);
    public void genCreateEnterpriseApplyFile(Map<String,Object> postParams,JbpmFlowInfo jbpmFlowInfo);
}

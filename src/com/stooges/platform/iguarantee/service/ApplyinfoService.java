/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;

/**
 * 描述 个人委托担保申请信息表业务相关service
 * @author HuYu
 * @version 1.0
 * @created 2019-03-24 13:03:35
 */
public interface ApplyinfoService extends BaseService {
    /**
     * 判断天数进行相应的跳转
     * @param flowVars
     * @param jbpmFlowInfo
     * @return
     */
    public Set<String> decideResult(Map<String,Object> flowVars,JbpmFlowInfo jbpmFlowInfo);
    /**
     * 通用保存单表业务数据接口
     * @param postParams
     * @param jbpmFlowInfo
     */
    public void genSaveApplyInfoData(Map<String,Object> postParams,JbpmFlowInfo jbpmFlowInfo);
    public void genCreateApplyInfoFile(Map<String,Object> postParams,JbpmFlowInfo jbpmFlowInfo);
    public void genCreateApplySimpleFile(Map<String,Object> postParams,JbpmFlowInfo jbpmFlowInfo);
    public void genSetApplyInfoData(HttpServletRequest request,JbpmFlowInfo jbpmFlowInfo);
    public void genSetApplySimpleData(HttpServletRequest request,JbpmFlowInfo jbpmFlowInfo);
    public List<Map<String,Object>> findApplyFiles(SqlFilter filter,Map<String,Object> map);
    public void confirmFee(Map<String, Object> postParams, JbpmFlowInfo jbpmFlowInfo);
}

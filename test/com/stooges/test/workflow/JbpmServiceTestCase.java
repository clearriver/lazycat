/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.workflow;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.service.ExecutionService;
import com.stooges.platform.workflow.service.JbpmService;
import com.stooges.platform.workflow.service.JbpmTaskService;

/**
 * 描述
 * @author 胡裕
 * @created 2017年5月25日 下午5:03:22
 */
public class JbpmServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private JbpmService jbpmService;
    /**
     * 
     */
    @Resource
    private ExecutionService executionService;
    
    /**
     * 
     */
    @Test
    public void saveExe(){
        String exeId = executionService.getNextExeId();
        Map<String,Object> exe = new HashMap<String,Object>();
        exe.put("EXECUTION_ID", exeId);
        exe.put("CREATOR_ID","402848a55b6547ec015b6547ec760000");
        exe.put("FLOWDEF_ID","4028d0815f13a781015f13aa8ad10010");
        exe.put("TMPSAVE_MAINTABLENAME","PLAT_DEMO_LEAVEINFO");
        exe.put("CREATOR_NAME","超级管理员");
        exe.put("EXECUTION_SUBJECT","请假流程测试【发起人:超级管理员】");
        exe.put("STATUS","0");
        exe.put("TMPSAVE_RECORDID","4028d0815f7149e0015f71a2672b002e");
        exe.put("EXECUTION_VERSION","1");
        jbpmService.saveOrUpdate("JBPM6_EXECUTION",exe,AllConstants.IDGENERATOR_ASSIGNED,null);
    }
    
    /**
     * 
     */
    @Test
    public void startFlow(){
        Map<String,Object> flowVars = new HashMap<String,Object>();
        flowVars.put("jbpmDefCode","leaveinfo");
        flowVars.put("jbpmOperingHandlerType","1");
        flowVars.put("jbpmCreatorId", "402848a55b6547ec015b6547ec760000");
        flowVars.put("LEAVEINFO_NAME", "百里屠苏");
        flowVars.put("LEAVEINFO_TYPE", "1");
        flowVars.put("LEAVEINFO_CREATETIME",PlatDateTimeUtil.
                formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        flowVars.put("LEAVEINFO_REASON","我的请假理由");
        jbpmService.startFlow(flowVars);
    }
    
    /**
     * 
     */
    @Test
    public void doNextStep(){
        Map<String,Object> flowVars = new HashMap<String,Object>();
        flowVars.put("LEAVEINFO_ID", "4028d0815fc819c4015fc819c4d20000");
        flowVars.put("LEAVEINFO_NAME", "李四");
        flowVars.put("LEAVEINFO_TYPE", "1");
        flowVars.put("LEAVEINFO_REASON","我的请假理由");
        JbpmFlowInfo jbpmFlowInfo  = new JbpmFlowInfo();
        jbpmFlowInfo.setJbpmExeId("FJFDA1711171150520628");
        jbpmFlowInfo.setJbpmOperingTaskId("4028d0815fc81966015fc81f4143000c");
        jbpmFlowInfo.setJbpmHandleOpinion("退回提交意见");
        jbpmFlowInfo.setJbpmIsTempSave("false");
        //退回
        jbpmFlowInfo.setJbpmHandleTaskStatus(String.valueOf(JbpmTaskService.TASKSTATUS_BACK));
        jbpmFlowInfo.setJbpmHandlerId("402848a55b6547ec015b6547ec760000");
        jbpmService.doNextStep(flowVars, jbpmFlowInfo);
    }
    
    /**
     * 
     */
    @Test
    public void updateTimeLimit(){
        jbpmService.updateTimeLimit();
    }
}

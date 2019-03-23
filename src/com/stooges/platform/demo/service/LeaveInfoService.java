/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.demo.service;

import java.util.Map;
import java.util.Set;

import javax.websocket.Session;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;

/**
 * 描述 请假信息业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-05 14:58:45
 */
public interface LeaveInfoService extends BaseService {
    /**
     * 判断天数进行相应的跳转
     * @param flowVars
     * @param jbpmFlowInfo
     * @return
     */
    public Set<String> decideResult(Map<String,Object> flowVars,JbpmFlowInfo jbpmFlowInfo);
    /**
     * 判断是否有暂存按钮
     * @param jbpmFlowInfo
     * @return
     */
    public boolean isHaveTempSave(JbpmFlowInfo jbpmFlowInfo);
    /**
     * 满足办理人是管理员时的办理人配置
     * @param flowVars
     * @param jbpmFlowInfo
     * @return
     */
    public boolean forAdmin(Map<String,Object> flowVars,JbpmFlowInfo jbpmFlowInfo);
    
    /**
     * 满足办理人非管理员时的办理人配置
     * @param flowVars
     * @param jbpmFlowInfo
     * @return
     */
    public boolean forUser(Map<String,Object> flowVars,JbpmFlowInfo jbpmFlowInfo);
    /**
     * 获取饼图测试数据
     * @param sqlFilter
     * @param fieldInfo
     * @return
     */
    public Map<String,Object> getEchartPieData(SqlFilter sqlFilter,Map<String,Object> fieldInfo);
    /***
     * 获取word模版数据
     * @param params
     * @return
     */
    public Map<String,Object> getWordDatas(Map<String,Object> params);
    
    /**
     * 获取折线图例1测试数据
     * @param sqlFilter
     * @param fieldInfo
     * @return
     */
    public Map<String,Object> getEchartLine1Data(SqlFilter sqlFilter,Map<String,Object> fieldInfo);
    
    /**
     * 通用保存单表业务数据接口
     * @param postParams
     * @param jbpmFlowInfo
     */
    public void genSaveBusData(Map<String,Object> postParams,JbpmFlowInfo jbpmFlowInfo);
    /**
     * 
     * @param msgContent
     */
    public void doSomeThing(String msgContent);
    
    /**
     * 
     * @param msgContent
     */
    public void doSomeThing2(String msgContent);
    /**
     * 推送消息
     * @param msgContent
     * @param clientId
     */
    public void sendWebSocketMsg(String msgContent);
    
    /**
     * 测试MySQL的数据源
     */
    public void testMysql();
    /**
     * 测试oracle的数据源
     */
    public void testNativeDb();
    
    /**
     * 测试oracle的数据源
     */
    public void testOracle();
    /**
     * 测试手动切换数据源
     */
    public void dynamicDataTest(String dataCode);
}

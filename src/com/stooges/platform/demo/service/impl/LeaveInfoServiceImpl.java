/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.demo.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.web.socket.PlatWebSocket;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.common.dynadatasource.DataSource;
import com.stooges.platform.demo.dao.LeaveInfoDao;
import com.stooges.platform.demo.service.LeaveInfoService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;

/**
 * 描述 请假信息业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-05 14:58:45
 */
@Service("leaveInfoService")
public class LeaveInfoServiceImpl extends BaseServiceImpl implements LeaveInfoService {

    /**
     * 所引入的dao
     */
    @Resource
    private LeaveInfoDao dao;
    /**
     * 
     */
    @Resource
    private FileAttachService fileAttachService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 判断天数进行相应的跳转
     * @param flowVars
     * @param jbpmFlowInfo
     * @return
     */
    public Set<String> decideResult(Map<String,Object> flowVars,JbpmFlowInfo jbpmFlowInfo){
        //int LEAVEINFO_DAYS = Integer.parseInt(flowVars.get("LEAVEINFO_DAYS").toString());
        Set<String> resutNodeKey = new HashSet<String>();
        resutNodeKey.add("-2");
        /*if(LEAVEINFO_DAYS>=3){
            resutNodeKey.add("-2");
        }else{
            resutNodeKey.add("-5");
        }*/
        return resutNodeKey;
    }
    
    /**
     * 判断是否有暂存按钮
     * @param jbpmFlowInfo
     * @return
     */
    public boolean isHaveTempSave(JbpmFlowInfo jbpmFlowInfo){
        return false;
    }
    
    /**
     * 满足办理人是管理员时的办理人配置
     * @param flowVars
     * @param jbpmFlowInfo
     * @return
     */
    public boolean forAdmin(Map<String,Object> flowVars,JbpmFlowInfo jbpmFlowInfo){
        Map<String,Object> backLoginUser = PlatAppUtil.getBackPlatLoginUser();
        String SYSUSER_ACCOUNT = (String) backLoginUser.get("SYSUSER_ACCOUNT");
        if(SYSUSER_ACCOUNT.equals("admin")){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * 满足办理人非管理员时的办理人配置
     * @param flowVars
     * @param jbpmFlowInfo
     * @return
     */
    public boolean forUser(Map<String,Object> flowVars,JbpmFlowInfo jbpmFlowInfo){
        Map<String,Object> backLoginUser = PlatAppUtil.getBackPlatLoginUser();
        String SYSUSER_ACCOUNT = (String) backLoginUser.get("SYSUSER_ACCOUNT");
        if(SYSUSER_ACCOUNT.equals("admin")){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 获取饼图测试数据
     * @param sqlFilter
     * @param fieldInfo
     * @return
     */
    public Map<String,Object> getEchartPieData(SqlFilter sqlFilter,Map<String,Object> fieldInfo){
        Map<String,Object> result = new HashMap<String,Object>();
        List<String> legendData = new ArrayList<String>();
        legendData.add("直接访问");
        legendData.add("邮件营销");
        legendData.add("联盟广告");
        legendData.add("视频广告");
        legendData.add("搜索引擎");
        Map<String,Object> legend = new HashMap<String,Object>();
        legend.put("data", legendData);
        result.put("legend", legend);
        List<Map<String,Object>> seriesDataList = new ArrayList<Map<String,Object>>();
        Map<String,Object> data1 =new HashMap<String,Object>();
        data1.put("value", 335);
        data1.put("name", "直接访问");
        Map<String,Object> data2 =new HashMap<String,Object>();
        data2.put("value", 310);
        data2.put("name", "邮件营销");
        Map<String,Object> data3 =new HashMap<String,Object>();
        data3.put("value", 234);
        data3.put("name", "联盟广告");
        Map<String,Object> data4 =new HashMap<String,Object>();
        data4.put("value", 135);
        data4.put("name", "视频广告");
        /*Map<String,Object> data5 =new HashMap<String,Object>();
        String name = sqlFilter.getRequest().getParameter("USER_NAME");
        if(StringUtils.isNotEmpty(name)){
            data5.put("value", 1000);
        }else{
            data5.put("value", 1548);
        }
        data5.put("name", "搜索引擎");*/
        seriesDataList.add(data1);
        seriesDataList.add(data2);
        seriesDataList.add(data3);
        seriesDataList.add(data4);
        //seriesDataList.add(data5);
        List<Map<String,Object>> seriesList = new ArrayList<Map<String,Object>>();
        Map<String,Object> series1 = new HashMap<String,Object>();
        series1.put("data", seriesDataList);
        seriesList.add(series1);
        result.put("series",seriesList);
        result.put("success", "true");
        return result;
    }
    
    /***
     * 获取word模版数据
     * @param params
     * @return
     */
    public Map<String,Object> getWordDatas(Map<String,Object> params){
        JbpmFlowInfo jbpmFlowInfo = (JbpmFlowInfo) PlatBeanUtil.mapToBean(new JbpmFlowInfo(),params);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("USER_NAME",params.get("LEAVEINFO_NAME"));
        result.put("AGE",jbpmFlowInfo.getJbpmDefCode());
        return result;
    }
    
    /**
     * 获取折线图例1测试数据
     * @param sqlFilter
     * @param fieldInfo
     * @return
     */
    public Map<String,Object> getEchartLine1Data(SqlFilter sqlFilter,Map<String,Object> fieldInfo){
        Map<String,Object> result = new HashMap<String,Object>();
        List<String> legendData = new ArrayList<String>();
        legendData.add("最高气温");
        legendData.add("最低气温");
        //定义图例数据源
        Map<String,Object> legend = new HashMap<String,Object>();
        legend.put("data", legendData);
        result.put("legend", legend);
        //定义x轴数据源
        Map<String,Object> xAxis = new HashMap<String,Object>();
        xAxis.put("data",new String[]{"周一","周二","周三","周四","周五","周六","周日"});
        result.put("xAxis", xAxis);
        //定义数据源
        List<Map<String,Object>> seriesList = new ArrayList<Map<String,Object>>();
        Map<String,Object> series1 = new HashMap<String,Object>();
        series1.put("data", new int[]{11, 11, 15, 13, 12, 13, 10});
        Map<String,Object> series2 = new HashMap<String,Object>();
        series2.put("data", new int[]{1, -2, 2, 5, 3, 2, 0});
        seriesList.add(series1);
        seriesList.add(series2);
        result.put("series",seriesList);
        result.put("success", "true");
        return result;
    }
    
    /**
     * 通用保存单表业务数据接口
     * @param postParams
     * @param jbpmFlowInfo
     */
    public void genSaveBusData(Map<String,Object> postParams,JbpmFlowInfo jbpmFlowInfo){
        //获取主表名称
        String mainTableName = jbpmFlowInfo.getJbpmMainTableName();
        Map<String,Object> mainRecord = dao.saveOrUpdate(mainTableName,postParams,
                AllConstants.IDGENERATOR_UUID,null);
        String recordId = (String) mainRecord.get(jbpmFlowInfo.getJbpmMainTablePkName());
        jbpmFlowInfo.setJbpmMainTableRecordId(recordId);
        //System.out.println("主表ID:"+jbpmFlowInfo.getJbpmMainTableRecordId());
        String UPLOAD_FILES_JSON = (String) postParams.get("UPLOAD_FILES_JSON");
        fileAttachService.saveFileAttachs(UPLOAD_FILES_JSON, "PLAT_DEMO_LEAVEINFO",recordId,null);
    }
    
    /**
     * 
     * @param msgContent
     */
    public void doSomeThing(String msgContent){
        System.out.println("测试:"+msgContent);
    }
    
    /**
     * 
     * @param msgContent
     */
    public void doSomeThing2(String msgContent){
        System.out.println("订阅:"+msgContent);
    }
    
    /**
     * 推送消息
     * @param msgContent
     * @param clientId
     */
    public void sendWebSocketMsg(String msgContent){
        /*for(String clientId:PlatWebSocket.webSocketMap.keySet()){
            System.out.println("客户端ID:"+clientId);
        }*/
        //PlatWebSocket.sendMessage("059a6bef-ab8f-4663-8afe-a011bb53dd55", "只发送这个客户端...");
        //PlatWebSocket.sendMessage(clientId, "只发送这个客户端...");
        //PlatWebSocket.sendAllClientsMsg(msgContent);
    }
    
    /**
     * 测试MySQL的数据源
     */
    @DataSource("mysql10") 
    public void testMysql(){
        Map<String,Object> data = this.getRecord("JBPM6_TASK",
                new String[]{"TASK_ID"}, new Object[]{"402882a1608b9eb101608ba55ddf0011"});
        System.out.println(data.toString());
    }
    
    /**
     * 测试oracle的数据源
     */
    public void testNativeDb(){
        Map<String,Object> data = this.getRecord("JBPM6_AGENT",
                new String[]{"AGENT_ID"}, new Object[]{"40283f815cd2ee07015cd3abef4f0180"});
        System.out.println(data.toString());
    }
    
    /**
     * 测试oracle的数据源
     */
    @DataSource("oracle168") 
    public void testOracle(){
        Map<String,Object> data = this.getRecord("JBPM6_AGENT",
                new String[]{"AGENT_ID"}, new Object[]{"40283f815cd2ee07015cd3abef4f0180"});
        System.out.println(data.toString());
    }

    /**
     * 
     */
    @Override
    @DataSource("unknown") 
    public void dynamicDataTest(String dataCode) {
        StringBuffer sql = new StringBuffer("");
        sql.append("select t.* from JBPM6_EXECUTION t ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), null, null);
        System.out.println("数据源"+dataCode+"的JBPM6_EXECUTION表的数据条数:"+list.size());
    }
    
    
  
}

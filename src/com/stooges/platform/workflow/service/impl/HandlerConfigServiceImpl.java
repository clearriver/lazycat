/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.system.service.SysUserService;
import com.stooges.platform.workflow.dao.HandlerConfigDao;
import com.stooges.platform.workflow.model.FlowNextHandler;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.model.NodeAssigner;
import com.stooges.platform.workflow.service.HandlerConfigService;

/**
 * 描述 办理人业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-10 15:50:11
 */
@Service("handlerConfigService")
public class HandlerConfigServiceImpl extends BaseServiceImpl implements HandlerConfigService {

    /**
     * 所引入的dao
     */
    @Resource
    private HandlerConfigDao dao;
    /**
     * 
     */
    @Resource
    private SysUserService sysUserService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取指定角色审核人
     * @param flowVars
     * @param nodeAssigner
     * @param jbpmFlowInfo
     * @return
     */
    public List<FlowNextHandler> getAssignRoleHandlers(Map<String,Object> flowVars,
            NodeAssigner nodeAssigner,JbpmFlowInfo jbpmFlowInfo){
        StringBuffer sql = new StringBuffer(sysUserService.getUserInfoPreSql());
        sql.append(" AND T.SYSUSER_ID IN (");
        sql.append("SELECT UR.SYSUSER_ID FROM  PLAT_SYSTEM_SYSUSERROLE UR ");
        sql.append("WHERE UR.ROLE_ID IN ").append(PlatStringUtil.getValueArray(nodeAssigner.getVarValues()));
        sql.append(" )");
        List<Object> params = new ArrayList<Object>();
        params.add(SysUserService.SYSUSER_STATUS_DEL);
        String filterRule = nodeAssigner.getFilterRule();
        if(StringUtils.isNotEmpty(filterRule)&&filterRule.equals(NodeAssigner.FILTERRULE_SAMECOMPANY)){
            //获取当前登录用户
            Map<String,Object> backLoginUser = PlatAppUtil.getBackPlatLoginUser();
            String SYSUSER_COMPANYID = (String) backLoginUser.get("SYSUSER_COMPANYID");
            sql.append(" AND T.SYSUSER_COMPANYID=?");
            params.add(SYSUSER_COMPANYID);
        }
        sql.append(" ORDER BY T.SYSUSER_CREATETIME DESC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(),params.toArray(), null);
        List<FlowNextHandler> handerList = new ArrayList<FlowNextHandler>();
        for(int i=0;i<list.size();i++){
            Map<String,Object> sysUser =list.get(i);
            String SYSUSER_ID = (String) sysUser.get("SYSUSER_ID");
            String SYSUSER_NAME = (String) sysUser.get("SYSUSER_NAME");
            int orderSn = i+1;
            FlowNextHandler nextHandler = new FlowNextHandler();
            nextHandler.setAssignerId(SYSUSER_ID);
            nextHandler.setAssignerName(SYSUSER_NAME);
            if(nodeAssigner.getIsOrder().equals("1")){
                nextHandler.setTaskOrder(orderSn);
            }else{
                nextHandler.setTaskOrder(-1);
            }
            handerList.add(nextHandler);
        }
        return handerList;
    }
    
    /**
     * 获取下拉框数据源
     * @param parmaJson
     * @return
     */
    public List<Map<String,Object>> findForSelect(String parmaJson){
        StringBuffer sql = new StringBuffer("SELECT H.HANDLERCONFIG_CODE ");
        sql.append("AS VALUE,H.HANDLERCONFIG_NAME AS LABEL,");
        sql.append("H.HANDLERCONFIG_TYPE,H.HANDLERCONFIG_INTER ");
        sql.append("FROM JBPM6_HANDLERCONFIG H");
        sql.append(" ORDER BY H.HANDLERCONFIG_CREATETIME ASC ");
        return dao.findBySql(sql.toString(),null,null);
    }
    
    /**
     * 获取发起人作为审核人
     * @param flowVars
     * @param nodeAssigner
     * @param jbpmFlowInfo
     * @return
     */
    public List<FlowNextHandler> getStartHandlers(Map<String,Object> flowVars,
            NodeAssigner nodeAssigner,JbpmFlowInfo jbpmFlowInfo){
        Map<String,Object> sysUser = dao.getRecord("PLAT_SYSTEM_SYSUSER",
                new String[]{"SYSUSER_ID"},new Object[]{jbpmFlowInfo.getJbpmCreatorId()});
        String SYSUSER_ID = (String) sysUser.get("SYSUSER_ID");
        String SYSUSER_NAME = (String) sysUser.get("SYSUSER_NAME");
        List<FlowNextHandler> handerList = new ArrayList<FlowNextHandler>();
        FlowNextHandler nextHandler = new FlowNextHandler();
        nextHandler.setAssignerId(SYSUSER_ID);
        nextHandler.setAssignerName(SYSUSER_NAME);
        nextHandler.setTaskOrder(-1);
        handerList.add(nextHandler);
        return handerList;
    }
    
    /**
     * 获取指定具体用户数据列表
     * @param flowVars
     * @param nodeAssigner
     * @param jbpmFlowInfo
     * @return
     */
    public List<FlowNextHandler> getAssignHandlers(Map<String,Object> flowVars,
            NodeAssigner nodeAssigner,JbpmFlowInfo jbpmFlowInfo){
        String userIds = nodeAssigner.getVarValues();
        List<FlowNextHandler> handerList = new ArrayList<FlowNextHandler>();
        if(StringUtils.isNotEmpty(userIds)){
            String[] userIdArray = userIds.split(",");
            for(String userId:userIdArray){
                Map<String,Object> sysUser = dao.getRecord("PLAT_SYSTEM_SYSUSER",
                        new String[]{"SYSUSER_ID"},new Object[]{userId});
                String SYSUSER_ID = (String) sysUser.get("SYSUSER_ID");
                String SYSUSER_NAME = (String) sysUser.get("SYSUSER_NAME");
                FlowNextHandler nextHandler = new FlowNextHandler();
                nextHandler.setAssignerId(SYSUSER_ID);
                nextHandler.setAssignerName(SYSUSER_NAME);
                nextHandler.setTaskOrder(-1);
                handerList.add(nextHandler);
            }
        }
        return handerList;
    }
  
}

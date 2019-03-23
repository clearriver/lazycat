/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.platform.workflow.dao.NodeAssignerDao;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.model.NodeAssigner;
import com.stooges.platform.workflow.service.NodeAssignerService;
import com.stooges.platform.workflow.service.NodeBindService;

/**
 * 描述 下一环节办理人业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-10 17:05:56
 */
@Service("nodeAssignerService")
public class NodeAssignerServiceImpl extends BaseServiceImpl implements NodeAssignerService {

    /**
     * 所引入的dao
     */
    @Resource
    private NodeAssignerDao dao;
    /**
     * 
     */
    @Resource
    private NodeBindService nodeBindService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取目标配置
     * @param postParams
     * @param jbpmFlowInfo
     * @param list
     * @return
     */
    private Map<String,Object> getTargetConfig(Map<String,Object> postParams,
            JbpmFlowInfo jbpmFlowInfo,List<Map<String,Object>> list){
        Map<String,Object> targetConfig = null;
        for(Map<String,Object> assignerConfig:list){
            String NODEASSIGNER_STAISY = (String) assignerConfig.get("NODEASSIGNER_STAISY");
            String beanId = NODEASSIGNER_STAISY.split("[.]")[0];
            String method = NODEASSIGNER_STAISY.split("[.]")[1];
            Object serviceBean = PlatAppUtil.getBean(beanId);
            boolean isTarget = false;
            if (serviceBean != null) {
                Method invokeMethod;
                try {
                    invokeMethod = serviceBean.getClass().getDeclaredMethod(method,
                            new Class[] { Map.class,JbpmFlowInfo.class});
                    isTarget =  (boolean) invokeMethod.invoke(serviceBean,
                            new Object[] { postParams,jbpmFlowInfo});
                } catch (Exception e) {
                    PlatLogUtil.printStackTrace(e);
                }
            }
            if(isTarget){
                targetConfig = assignerConfig;
                break;
            }
        }
        return targetConfig;
    }
    
    /**
     * 获取节点配置的办理人信息
     * @param defId
     * @param defVersion
     * @param nodeKey
     * @param nextNodeKey
     * @return
     */
    public NodeAssigner getNodeAssigner(String nextNodeKey,Map<String,Object> postParams,JbpmFlowInfo jbpmFlowInfo){
        StringBuffer sql = new StringBuffer("select T.NODEASSIGNER_ISORDER,T.NODEASSIGNER_FILTERRULE");
        sql.append(",T.NODEASSIGNER_STAISY,T.NODEASSIGNER_DEFAULTINTER,T.NODEASSIGNER_VARVALUES,");
        sql.append("H.HANDLERCONFIG_TYPE,H.HANDLERCONFIG_URL,H.HANDLERCONFIG_WIDTH,H.HANDLERCONFIG_HEIGHT,");
        sql.append("T.NODEASSIGNER_TYPE,T.NODEASSIGNER_NEXTNODEKEY,T.NODEASSIGNER_NEXTNODENAME");
        sql.append(" from JBPM6_NODEASSIGNER T LEFT JOIN JBPM6_HANDLERCONFIG H");
        sql.append(" ON T.NODEASSIGNER_HANDLERCODE=H.HANDLERCONFIG_CODE");
        sql.append(" WHERE T.NODEASSIGNER_DEFID=? AND T.NODEASSIGNER_NODEKEY=? AND T.NODEASSIGNER_NEXTNODEKEY=? ");
        sql.append("AND T.NODEASSIGNER_DEFVERSION=? ORDER BY T.NODEASSIGNER_CREATETIME ASC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), new Object[]{jbpmFlowInfo.getJbpmDefId(),
            jbpmFlowInfo.getJbpmOperingNodeKey(),nextNodeKey,jbpmFlowInfo.getJbpmDefVersion()}, null);
        if(list!=null&&list.size()==1){
            Map<String,Object> assignerConfig = list.get(0);
            NodeAssigner assigner = getNodeAssigner(jbpmFlowInfo,
                    assignerConfig);
            return assigner;
        }else if(list!=null&&list.size()>1){
            Map<String,Object> assignerConfig = this.getTargetConfig(postParams, jbpmFlowInfo, list);
            NodeAssigner assigner = getNodeAssigner(jbpmFlowInfo,
                    assignerConfig);
            return assigner;
        }else{
            return null;
        }
    }

    /**
     * @param jbpmFlowInfo
     * @param assignerConfig
     * @return
     */
    private NodeAssigner getNodeAssigner(JbpmFlowInfo jbpmFlowInfo,
            Map<String, Object> assignerConfig) {
        NodeAssigner assigner = new NodeAssigner();
        assigner.setIsOrder((String)assignerConfig.get("NODEASSIGNER_ISORDER"));
        assigner.setFilterRule((String)assignerConfig.get("NODEASSIGNER_FILTERRULE"));
        assigner.setDefaultInter((String)assignerConfig.get("NODEASSIGNER_DEFAULTINTER"));
        assigner.setVarValues((String)assignerConfig.get("NODEASSIGNER_VARVALUES"));
        assigner.setHandlerType((String)assignerConfig.get("HANDLERCONFIG_TYPE"));
        assigner.setHandlerUrl((String)assignerConfig.get("HANDLERCONFIG_URL"));
        assigner.setHandlerWidth((String)assignerConfig.get("HANDLERCONFIG_WIDTH"));
        assigner.setHandlerHeight((String)assignerConfig.get("HANDLERCONFIG_HEIGHT"));
        assigner.setHandlerNature((String)assignerConfig.get("NODEASSIGNER_TYPE"));
        assigner.setNodeKey((String)assignerConfig.get("NODEASSIGNER_NEXTNODEKEY"));
        assigner.setNodeName((String)assignerConfig.get("NODEASSIGNER_NEXTNODENAME"));
        String taskNature = nodeBindService.getTaskNature(jbpmFlowInfo.getJbpmDefId(),
                jbpmFlowInfo.getJbpmDefVersion(),assigner.getNodeKey());
        assigner.setNextAuditType(taskNature);
        return assigner;
    }
    
    /**
     * 克隆节点办理人配置表
     * @param oldFlowDefId
     * @param oldFlowDefVersion
     */
    public void copyNodeAssigner(String oldFlowDefId,int oldFlowDefVersion,
            String newFlowDefId,int newFlowDefVersion){
        Map<String,String> replaceColumn = new HashMap<String,String>();
        String dbType = PlatAppUtil.getDbType();
        if(dbType.equals("MYSQL")){
            replaceColumn.put("NODEASSIGNER_ID","REPLACE(UUID(),'-','')");
        }else if(dbType.equals("ORACLE")){
            replaceColumn.put("NODEASSIGNER_ID","SYS_GUID()");
        }else if(dbType.equals("SQLSERVER")){
            replaceColumn.put("NODEASSIGNER_ID","REPLACE(newId(),'-','')");
        }
        replaceColumn.put("NODEASSIGNER_DEFID","?");
        replaceColumn.put("NODEASSIGNER_DEFVERSION","?");
        String copySql = dao.getCopyTableSql("JBPM6_NODEASSIGNER", replaceColumn);
        StringBuffer sql = new StringBuffer(copySql);
        sql.append(" WHERE NODEASSIGNER_DEFID=? AND NODEASSIGNER_DEFVERSION=?");
        dao.executeSql(sql.toString(), new Object[]{newFlowDefId,
            newFlowDefVersion,oldFlowDefId,oldFlowDefVersion});
    }
    
    /**
     * 获取定义ID和版本号列表
     * @param defId
     * @param flowVersion
     * @return
     */
    public List<Map<String,Object>> findByDefIdAndVesion(String defId,int flowVersion){
        StringBuffer sql = new StringBuffer("SELECT * FROM JBPM6_NODEASSIGNER J");
        sql.append(" WHERE J.NODEASSIGNER_DEFID=? AND J.NODEASSIGNER_DEFVERSION=? ");
        sql.append(" ORDER BY J.NODEASSIGNER_ID DESC ");
        return dao.findBySql(sql.toString(), new Object[]{defId,flowVersion}, null);
    }
  
}

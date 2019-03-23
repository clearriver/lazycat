/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.system.service.SysUserService;
import com.stooges.platform.workflow.dao.AgentDao;
import com.stooges.platform.workflow.service.AgentService;

/**
 * 描述 工作委托业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-27 16:49:15
 */
@Service("agentService")
public class AgentServiceImpl extends BaseServiceImpl implements AgentService {

    /**
     * 所引入的dao
     */
    @Resource
    private AgentDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 判断是否配置过代理
     * @param userId
     * @return
     */
    public boolean isExistsConfig(String userId){
        int count = dao.getCount(userId);
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 根据filter和配置信息获取数据列表
     * @param filter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findList(SqlFilter filter,Map<String,Object> fieldInfo){
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer("SELECT T.AGENT_TITLE,T.AGENT_ID,T.AGENT_TYPE");
        sql.append(",T.AGENT_USERNAME,T.AGENT_ISVALID,T.AGENT_PROXYERNAME,");
        sql.append("T.AGENT_BEGINDATE,T.AGENT_ENDDATE FROM JBPM6_AGENT T");
        //获取当前登录用户
        Map<String,Object> sysUser = PlatAppUtil.getBackPlatLoginUser();
        boolean isAdmin = (boolean) sysUser.get(SysUserService.ISADMIN_KEY);
        if(!isAdmin){
            sql.append(" WHERE T.AGENT_CREATORID=? ");
            String SYSUSER_ID = (String) sysUser.get("SYSUSER_ID");
            params.add(SYSUSER_ID);
        }
        String exeSql = dao.getQuerySql(filter, sql.toString(), params);
        List<Map<String, Object>> list = dao.findBySql(exeSql,
                params.toArray(), filter.getPagingBean());
        return list;
    }
    
    /**
     * 根据用户ID获取代理人ID
     * @param userId
     * @return
     */
    public String getAgentProxyerId(String userId,String defId){
        StringBuffer sql = new StringBuffer("SELECT * FROM ");
        sql.append("JBPM6_AGENT J WHERE J.AGENT_USERID=? ");
        sql.append(" AND J.AGENT_ISVALID=? AND J.AGENT_BEGINDATE<=?");
        sql.append(" AND J.AGENT_ENDDATE>=? ");
        String currentDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        Map<String,Object> agent = dao.getBySql(sql.toString(), new Object[]{userId,"1",currentDate,currentDate});
        if(agent!=null){
            String AGENT_TYPE = (String) agent.get("AGENT_TYPE");
            String AGENT_PROXYERID = (String) agent.get("AGENT_PROXYERID");
            if(AGENT_TYPE.equals("1")){
                return AGENT_PROXYERID;
            }else{
                String AGENT_DEFIDS = (String) agent.get("AGENT_DEFIDS");
                if(AGENT_DEFIDS.contains(defId)){
                    return AGENT_PROXYERID;
                }else{
                    return null;
                }
            }
        }else{
            return null;
        }
    }
    
    /**
     * 获取下一步办理人的代理人IDS
     * @param sourceNextAssignerIds
     * @param defId
     * @return
     */
    public String getNextAssignerProxyerIds(String sourceNextAssignerIds,String defId){
        List<String> newAssignerIdArray = new ArrayList<String>();
        if(StringUtils.isNotEmpty(sourceNextAssignerIds)){
            for(String assignerId:sourceNextAssignerIds.split(",")){
                String proxyerId = this.getAgentProxyerId(assignerId, defId);
                if(StringUtils.isNotEmpty(proxyerId)&&!newAssignerIdArray.contains(proxyerId)){
                    newAssignerIdArray.add(proxyerId);
                }else{
                    newAssignerIdArray.add(assignerId);
                }
            }
        }
        return PlatStringUtil.getListStringSplit(newAssignerIdArray);
    }
  
}

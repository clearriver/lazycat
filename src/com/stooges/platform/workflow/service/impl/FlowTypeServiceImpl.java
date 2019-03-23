/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.system.service.SysUserService;
import com.stooges.platform.workflow.dao.FlowTypeDao;
import com.stooges.platform.workflow.service.FlowDefService;
import com.stooges.platform.workflow.service.FlowTypeService;

/**
 * 描述 流程类别业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-03 16:12:37
 */
@Service("flowTypeService")
public class FlowTypeServiceImpl extends BaseServiceImpl implements FlowTypeService {

    /**
     * 所引入的dao
     */
    @Resource
    private FlowTypeDao dao;
    /**
     * 
     */
    @Resource
    private FlowDefService flowDefService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 删除流程类别级联更新流程定义数据
     * @param typeId
     */
    public void deleteCascadeFlowDef(String typeId){
        dao.deleteRecords("JBPM6_FLOWTYPE","FLOWTYPE_ID",new String[]{typeId});
        StringBuffer sql = new StringBuffer("UPDATE JBPM6_FLOWDEF ");
        sql.append(" SET FLOWTYPE_ID=null WHERE FLOWTYPE_ID=? ");
        dao.executeSql(sql.toString(), new Object[]{typeId});
    }
    
    /**
     * 获取可选流程类别下拉框数据源
     * @param paramJson
     * @return
     */
    public List<Map<String,Object>> findTypeSelect(String paramJson){
        StringBuffer sql = new StringBuffer("select T.FLOWTYPE_ID AS VALUE,T.FLOWTYPE_NAME AS LABEL ");
        sql.append("from JBPM6_FLOWTYPE T ");
        //获取当前登录用户
        Map<String,Object> sysUser = PlatAppUtil.getBackPlatLoginUser();
        String grantTypeIds = (String) sysUser.get(SysUserService.FLOWDEFTYPEIDS_KEY);
        sql.append("WHERE T.FLOWTYPE_ID IN ").append(PlatStringUtil.getSqlInCondition(grantTypeIds));
        sql.append(" ORDER BY T.FLOWTYPE_CREATETIME ASC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), null, null);
        return list;
    }
    
    /**
     * 获取流程类别组列表数据源
     * @param queryParamJson
     * @return
     */
    public List<Map<String,Object>> findGroupList(String queryParamJson){
        StringBuffer sql = new StringBuffer("select T.FLOWTYPE_ID AS VALUE,T.FLOWTYPE_NAME AS LABEL ");
        sql.append("from JBPM6_FLOWTYPE T ");
        //获取当前登录用户
        /*Map<String,Object> sysUser = PlatAppUtil.getBackPlatLoginUser();
        boolean isAdmin = (boolean) sysUser.get(SysUserService.ISADMIN_KEY);
        if(!isAdmin){
            Set<String> grantGroupIds = (Set<String>) sysUser.get(SysUserService.GROUPIDSET_KEY);
            sql.append("WHERE T.GROUP_ID IN ").append(PlatStringUtil.getSqlInCodition(grantGroupIds));
        }*/
        sql.append(" ORDER BY T.FLOWTYPE_CREATETIME ASC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), null, null);
        if(list==null){
            list = new ArrayList<Map<String,Object>>();
        }
        Map<String,Object> allType = new HashMap<String,Object>();
        allType.put("LABEL","全部流程类别");
        allType.put("VALUE","0");
        list.add(0, allType);
        return list;
    }
    
    /**
     * 根据查询参数获取数据列表
     * @param queryvalue
     * @return
     */
    public List<Map<String,Object>> findForSelect(String queryvalue){
        StringBuffer sql = new StringBuffer("select T.FLOWTYPE_ID AS VALUE,T.FLOWTYPE_NAME AS LABEL ");
        sql.append("from JBPM6_FLOWTYPE T ");
        sql.append(" ORDER BY T.FLOWTYPE_CREATETIME ASC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), null, null);
        return list;
    }
    
    /**
     * 获取流程类别列表
     * @return
     */
    public List<Map<String,Object>> findTypeList(){
        StringBuffer sql = new StringBuffer("select T.FLOWTYPE_ID AS VALUE,T.FLOWTYPE_NAME AS LABEL ");
        sql.append("from JBPM6_FLOWTYPE T ");
        //获取当前登录用户
        Map<String,Object> backLoginUser = PlatAppUtil.getBackPlatLoginUser();
        boolean isAdmin = (boolean) backLoginUser.get(SysUserService.ISADMIN_KEY);
        if(!isAdmin){
            String grantTypeDefIds = (String) backLoginUser.get(SysUserService.FLOWDEFTYPEIDS_KEY);
            if(StringUtils.isNotEmpty(grantTypeDefIds)){
                sql.append(" WHERE T.FLOWTYPE_ID IN ");
                sql.append(PlatStringUtil.getSqlInCondition(grantTypeDefIds));
            }else{
                sql.append(" WHERE T.FLOWTYPE_ID='-1' ");
            }
        }
        sql.append(" ORDER BY T.FLOWTYPE_CREATETIME ASC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), null, null);
        return list;
    }
    
    /**
     * 获取流程类别和流程定义树形JSON
     * @param params
     * @return
     */
    public String getTypeAndDefJson(Map<String,Object> params){
        String needCheckIds = (String) params.get("needCheckIds");
        String typeNoCheck = (String) params.get("typeNoCheck");
        Set<String> needCheckIdSet = new HashSet<String>();
        if(StringUtils.isNotEmpty(needCheckIds)){
            needCheckIdSet = new HashSet<String>(Arrays.asList(needCheckIds.split(",")));
        }
        Map<String,Object> rootNode = new HashMap<String,Object>();
        rootNode.put("id", "0");
        rootNode.put("name", "流程事项树");
        rootNode.put("nocheck", true);
        rootNode.put("open",true);
        List<Map<String,Object>> typeList = this.findTypeList();
        for(Map<String,Object> type:typeList){
            String typeId = (String) type.get("VALUE");
            String typeName = (String) type.get("LABEL");
            type.put("id", typeId);
            type.put("name", typeName);
            if(StringUtils.isNotEmpty(typeNoCheck)&&typeNoCheck.equals("true")){
                type.put("nocheck", true);
            }else{
                if(needCheckIdSet.contains(typeId)){
                    type.put("checked", true);
                }
            }
            List<Map<String,Object>> defList = flowDefService.findByTypeId(typeId);
            if(defList!=null&&defList.size()>0){
                for(Map<String,Object> def:defList){
                    String defId = (String) def.get("FLOWDEF_ID");
                    if(needCheckIdSet.contains(defId)){
                        def.put("checked", true);
                    }
                    def.put("id", def.get("FLOWDEF_ID"));
                    def.put("name", def.get("FLOWDEF_NAME"));
                }
                type.put("children", defList);
            }
        }
        rootNode.put("children", typeList);
        return JSON.toJSONString(rootNode);
    }
    
    /**
     * 获取自动补全的流程类别和定义数据
     * @param filter
     * @return
     */
    public List<Map<String,Object>> findAutoTypeDef(SqlFilter filter){
        StringBuffer sql = new StringBuffer("SELECT R.FLOWTYPE_NAME AS value,R.FLOWTYPE_NAME AS label");
        sql.append(" FROM JBPM6_FLOWTYPE R ");
        //获取当前登录用户
        Map<String,Object> backLoginUser = PlatAppUtil.getBackPlatLoginUser();
        boolean isAdmin = (boolean) backLoginUser.get(SysUserService.ISADMIN_KEY);
        if(!isAdmin){
            String grantTypeDefIds = (String) backLoginUser.get(SysUserService.FLOWDEFTYPEIDS_KEY);
            if(StringUtils.isNotEmpty(grantTypeDefIds)){
                sql.append(" WHERE R.FLOWTYPE_ID IN ");
                sql.append(PlatStringUtil.getSqlInCondition(grantTypeDefIds));
            }else{
                sql.append(" WHERE R.FLOWTYPE_ID='-1' ");
            }
        }
        sql.append(" ORDER BY R.FLOWTYPE_CREATETIME DESC");
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> typeList = dao.findBySql(sql.toString(),null,null);
        list.addAll(typeList);
        //获取所有定义数据
        sql = new StringBuffer("SELECT R.FLOWDEF_NAME AS value,R.FLOWDEF_NAME AS label");
        sql.append(" FROM JBPM6_FLOWDEF R ");
        if(!isAdmin){
            String grantTypeDefIds = (String) backLoginUser.get(SysUserService.FLOWDEFTYPEIDS_KEY);
            if(StringUtils.isNotEmpty(grantTypeDefIds)){
                sql.append(" WHERE R.FLOWDEF_ID IN ");
                sql.append(PlatStringUtil.getSqlInCondition(grantTypeDefIds));
            }else{
                sql.append(" WHERE R.FLOWDEF_ID='-1' ");
            }
        }
        sql.append(" ORDER BY R.FLOWDEF_CREATETIME DESC");
        List<Map<String,Object>> defList = dao.findBySql(sql.toString(),null,null);
        list.addAll(defList);
        return list;
    }
  
}

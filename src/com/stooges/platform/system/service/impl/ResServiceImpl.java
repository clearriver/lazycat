/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service.impl;

import java.util.ArrayList;
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
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDbUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.appmodel.service.CommonUIService;
import com.stooges.platform.appmodel.service.GlobalUrlService;
import com.stooges.platform.system.dao.ResDao;
import com.stooges.platform.system.service.ResService;
import com.stooges.platform.system.service.SysUserService;

/**
 * 描述 系统资源业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-05 17:12:34
 */
@Service("resService")
public class ResServiceImpl extends BaseServiceImpl implements ResService {

    /**
     * 所引入的dao
     */
    @Resource
    private ResDao dao;
    /***
     * 
     */
    @Resource
    private CommonUIService commonUIService;
    /**
     * 
     */
    @Resource
    private GlobalUrlService globalUrlService;
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
     * 根据filter获取资源所配置的URL
     * @param filter
     * @return
     */
    public List<Map> findResUrlByFilter(SqlFilter filter){
        String RES_ID = filter.getRequest().getParameter("RES_ID");
        Map<String,Object> resInfo = dao.getRecord("PLAT_SYSTEM_RES",
                new String[]{"RES_ID"},new Object[]{RES_ID});
        if(StringUtils.isNotEmpty(RES_ID)){
            String RES_OPERURLJSON = (String) resInfo.get("RES_OPERURLJSON");
            if(StringUtils.isNotEmpty(RES_OPERURLJSON)){
                List<Map> list = JSON.parseArray(RES_OPERURLJSON,Map.class);
                return list;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    
    /**
     * 根据资源ID删除数据,并且级联删除相关联的数据信息
     * @param resId
     */
    public void deleteAndCascadeAssoical(String resId){
        //级联删除角色资源中级表
        StringBuffer sql = new StringBuffer("DELETE FROM PLAT_SYSTEM_ROLERIGHT");
        sql.append(" WHERE RE_TABLENAME=? AND RE_RECORDID IN ");
        sql.append("(SELECT T.RES_ID FROM PLAT_SYSTEM_RES T ");
        sql.append("WHERE T.RES_PATH LIKE ? )");
        dao.executeSql(sql.toString(), new Object[]{"PLAT_SYSTEM_RES","%."+resId+".%"});
        sql = new StringBuffer("DELETE FROM PLAT_SYSTEM_RES ");
        sql.append("WHERE RES_PATH LIKE ? ");
        dao.executeSql(sql.toString(), new Object[]{"%."+resId+".%"});
        sysUserService.updateRightJsonToNull();
    }
    
    /**
     * 获取下拉资源树数据源
     * @param params
     * @return
     */
    public List<Map<String,Object>> findTreeSelectRes(String params){
        StringBuffer paramsConfig = new StringBuffer("[TABLE_NAME:PLAT_SYSTEM_RES]");
        paramsConfig.append("[TREE_IDANDNAMECOL:RES_ID,RES_NAME]");
        paramsConfig.append("[TREE_QUERYFIELDS:RES_CODE,RES_PARENTID,RES_TYPE,RES_MENUICON,RES_MENUURL,RES_TREESN]");
        paramsConfig.append("[FILTERS:RES_PARENTID_EQ|0]");
        List<Map<String,Object>> list = commonUIService.findGenTreeSelectorDatas(paramsConfig.toString());
        Map<String,Object> rootMap = new HashMap<String,Object>();
        rootMap.put("VALUE","0");
        rootMap.put("LABEL","系统资源树");
        rootMap.put("ISLEAF", "false");
        rootMap.put("TREE_LEVEL",0);
        rootMap.put("RES_ID", "0");
        rootMap.put("RES_NAME", "系统资源树");
        rootMap.put("RES_CODE", "0");
        list.add(0, rootMap);
        return list;
    }
    
    /**
     * 根据用户ID获取用户被授权的资源KEY集合
     * @param userId
     * @return
     */
    public Set<String> getUserGrantResCodeSet(String userId){
        return dao.getUserGrantResCodes(userId);
    }
    
    /**
     * 根据用户ID获取用户被授权的资源编码
     * @param userId
     * @return
     */
    public String getUserGrantResCodes(String userId){
        Set<String> resCodeSet = this.getUserGrantResCodeSet(userId);
        if(resCodeSet!=null&&resCodeSet.size()>0){
            StringBuffer codes = new StringBuffer("");
            for(String code:resCodeSet){
                codes.append(code).append(",");
            }
            codes.deleteCharAt(codes.length()-1);
            return codes.toString();
        }else{
            return "";
        }
    }
    
    /**
     * 获取授权的资源SQL
     * @return
     */
    private String getGrantResSql(){
        return PlatDbUtil.getDiskSqlContent("system/res/003",null);
    }
    
    /**
     * 根据资源ID和用户ID获取孩子资源列表数据
     * @param resId
     * @param userId
     * @return
     */
    private Map<String,Object> getChildGrantResList(Map<String,Object> parentRes,String userId){
        String sql = this.getGrantResSql();
        String resId = (String) parentRes.get("RES_ID");
        List<Map<String,Object>> list = dao.findBySql(sql, new Object[]{
                resId,userId,userId,userId,"1"}, null);
        if(list.size()>0){
            for(Map<String,Object> child:list){
                this.getChildGrantResList(child, userId);
            }
            parentRes.put("childres", list);
        }else{
            parentRes.put("childres", new ArrayList<Map<String,Object>>());
        }
        return parentRes;
    }
    
    /**
     * 获取顶级菜单被授权的资源
     * @return
     */
    private String getTopResSql(String subsyscode){
        return PlatDbUtil.getDiskSqlContent("system/res/002",null);
    } 
    
    /**
     * 根据用户ID获取被授权的资源列表
     * @param userId
     * @return
     */
    public List<Map<String,Object>> findGrantResList(String userId,String subsyscode){
        String sql = this.getTopResSql(subsyscode);
        List params = new ArrayList();
        if(StringUtils.isNotEmpty(subsyscode)){
            Map<String,Object> subSys = dao.getRecord("PLAT_SYSTEM_RES",
                    new String[]{"RES_CODE"},new Object[]{subsyscode});
            params.add(subSys.get("RES_ID").toString());
        }else{
            params.add(1);
        }
        params.add(userId);
        params.add(userId);
        params.add(userId);
        params.add("1");
        List<Map<String,Object>> topList = dao.findBySql(sql,params.toArray(), null);
        for(Map<String,Object> topRes:topList){
            topRes = this.getChildGrantResList(topRes, userId);
        }
        return topList;
    }
    
    /**
     * 获取全部的资源地址集合
     * @return
     */
    public Set<String> getAllResUrl(){
        StringBuffer sql = new StringBuffer("SELECT R.RES_MENUURL,R.RES_OPERURLJSON");
        sql.append(" FROM PLAT_SYSTEM_RES R ");
        Set<String> grantUrlSet = new HashSet<String>();
        List<Map<String,Object>> resList = this.findBySql(sql.toString(),null,null);
        for(Map<String,Object> res:resList){
            String RES_MENUURL = (String) res.get("RES_MENUURL");
            String RES_OPERURLJSON = (String) res.get("RES_OPERURLJSON");
            if(StringUtils.isNotEmpty(RES_MENUURL)){
                grantUrlSet.add(RES_MENUURL);
            }
            if(StringUtils.isNotEmpty(RES_OPERURLJSON)){
                List<Map> urlList = JSON.parseArray(RES_OPERURLJSON,Map.class);
                for(Map url:urlList){
                    String RES_OPERURL = (String) url.get("RES_OPERURL");
                    grantUrlSet.add(RES_OPERURL);
                }
            }
        }
        //获取公共URL权限
        List<String> filterUrls = globalUrlService.findByFilterType("2");
        grantUrlSet.addAll(filterUrls);
        return grantUrlSet;
    }
  
    /**
     * 根据用户ID获取
     * @param userId
     * @return
     */
    public Set<String> getUserGrantResUrlSet(String userId,String grantCodes){
        StringBuffer sql = new StringBuffer("SELECT R.RES_MENUURL,R.RES_OPERURLJSON");
        sql.append(" FROM PLAT_SYSTEM_RES R WHERE R.RES_CODE IN ");
        if(StringUtils.isEmpty(grantCodes)){
            grantCodes = this.getUserGrantResCodes(userId);
        }
        sql.append(PlatStringUtil.getSqlInCondition(grantCodes));
        Set<String> grantUrlSet = new HashSet<String>();
        List<Map<String,Object>> resList = this.findBySql(sql.toString(),null,null);
        for(Map<String,Object> res:resList){
            String RES_MENUURL = (String) res.get("RES_MENUURL");
            String RES_OPERURLJSON = (String) res.get("RES_OPERURLJSON");
            if(StringUtils.isNotEmpty(RES_MENUURL)){
                grantUrlSet.add(RES_MENUURL);
            }
            if(StringUtils.isNotEmpty(RES_OPERURLJSON)){
                List<Map> urlList = JSON.parseArray(RES_OPERURLJSON,Map.class);
                for(Map url:urlList){
                    String RES_OPERURL = (String) url.get("RES_OPERURL");
                    grantUrlSet.add(RES_OPERURL);
                }
            }
        }
        //获取公共URL权限
        List<String> filterUrls = globalUrlService.findByFilterType("2");
        grantUrlSet.addAll(filterUrls);
        return grantUrlSet;
    }

    /**
     * 获取可点击菜单类型的资源
     * @param paramsJson
     * @return
     */
    public List<Map<String,Object>> findUrlList(String paramsJson){
        StringBuffer sql = new StringBuffer("SELECT T.RES_ID AS VALUE,T.RES_NAME,T.RES_CODE ");
        sql.append("FROM PLAT_SYSTEM_RES T WHERE T.RES_TYPE=? AND ");
        sql.append("T.RES_MENUURL IS NOT NULL ORDER BY T.RES_TREESN ASC ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), new Object[]{"1"},null);
        for(Map<String,Object> map:list){
            String RES_NAME = (String) map.get("RES_NAME");
            String RES_CODE = (String) map.get("RES_CODE");
            String LABEL = RES_NAME+"("+RES_CODE+")";
            map.put("LABEL", LABEL);
        }
        return list;
    }
    
    /**
     * 新增更新资源信息
     * @param resInfo
     * @return
     */
    public Map<String,Object> saveUpdateResInfo(Map<String,Object> resInfo){
        String RES_ID = (String) resInfo.get("RES_ID");
        if(StringUtils.isNotEmpty(RES_ID)){
            //获取旧的数据
            Map<String,Object> oldResInfo = dao.getRecord("PLAT_SYSTEM_RES",
                    new String[]{"RES_ID"},new Object[]{RES_ID});
            String oldParentId = (String) oldResInfo.get("RES_PARENTID");
            //获取新的父亲ID
            String newParentId = (String) resInfo.get("RES_PARENTID");
            if(newParentId.equals(oldParentId)){
                resInfo = dao.saveOrUpdateTreeData("PLAT_SYSTEM_RES",
                        resInfo,AllConstants.IDGENERATOR_UUID,null);
            }else{
                //先更新其它字段
                resInfo = dao.saveOrUpdate("PLAT_SYSTEM_RES",
                        resInfo,AllConstants.IDGENERATOR_UUID,null);
                //更新排序字段
                String dragTreeNodeId = RES_ID;
                Map<String,Object> newParentInfo = dao.getRecord("PLAT_SYSTEM_RES",
                        new String[]{"RES_ID"},new Object[]{newParentId});
                String targetNodeId = newParentId;
                int targetNodeLevel = 0;
                if(newParentInfo!=null){
                    targetNodeLevel = Integer.parseInt(newParentInfo.get("RES_LEVEL").toString());
                }
                int dragTreeNodeNewLevel = targetNodeLevel+1;
                this.updateTreeSn("PLAT_SYSTEM_RES", dragTreeNodeId, dragTreeNodeNewLevel, 
                        targetNodeId, targetNodeLevel, "inner");
               
            }
            sysUserService.updateRightJsonToNull();
        }else{
            resInfo = dao.saveOrUpdateTreeData("PLAT_SYSTEM_RES",
                    resInfo,AllConstants.IDGENERATOR_UUID,null);
        }
        return resInfo;
    }
    
    /**
     * 根据资源编码获取被分配的用户ID列表
     * @param resId
     * @return
     */
    public List<String> findGrantedUserIds(String resCode){
        return dao.findGrantedUserIds(resCode);
    }
}

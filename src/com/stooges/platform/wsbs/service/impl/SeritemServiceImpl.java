/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.service.impl;

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
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.system.service.SysUserService;
import com.stooges.platform.wsbs.dao.SeritemDao;
import com.stooges.platform.wsbs.service.SercatalogService;
import com.stooges.platform.wsbs.service.SeritemService;

/**
 * 描述 信息表业务相关service实现类
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-15 11:26:44
 */
@Service("seritemService")
public class SeritemServiceImpl extends BaseServiceImpl implements SeritemService {

    /**
     * 所引入的dao
     */
    @Resource
    private SeritemDao dao;
    /**
     * sercatalogService
     */
    @Resource
    private SercatalogService sercatalogService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

    /**
     * 根据事项编号获取事项信息和流程信息
     * @param sERITEM_CODE
     * @return
     */
    @Override
    public Map<String, Object> getItemAndDefMap(String seritemCode) {
        StringBuffer sql = new StringBuffer("");
        sql.append("SELECT T.SERITEM_ID,T.SERITEM_CODE,T.SERITEM_NAME,D.FLOWDEF_CODE FROM PLAT_WSBS_SERITEM T ");
        sql.append(" LEFT JOIN JBPM6_FLOWDEF D ON T.SERITEM_FLOWDEFID = D.FLOWDEF_ID  ");
        sql.append(" WHERE T.SERITEM_CODE=? ");
        return dao.getBySql(sql.toString(), new Object[]{seritemCode});
    }
    /**
     * 根据服务事项目录ID获取服务事项列表
     * @param SERCATALOG_ID
     * @return
     */
    @Override
    public List<Map<String, Object>> getItemList(String SERCATALOG_ID) {
        StringBuffer sql = new StringBuffer("");
        sql.append("SELECT T.SERITEM_ID,T.SERITEM_CODE,T.SERITEM_NAME FROM PLAT_WSBS_SERITEM T ");
        sql.append(" WHERE T.SERCATALOG_ID=? ");
        return dao.findBySql(sql.toString(), new Object[]{SERCATALOG_ID}, null);
    }
    
    /**
     * 获取树形的服务事项类别和服务事项数据
     * @param request
     * @param response
     */
    public String getTypeAndItemsJson(Map<String,Object> params){
        String needCheckIds = (String) params.get("needCheckIds");
        String typeNoCheck = (String) params.get("typeNoCheck");
        Set<String> needCheckIdSet = new HashSet<String>();
        if(StringUtils.isNotEmpty(needCheckIds)){
            needCheckIdSet = new HashSet<String>(Arrays.asList(needCheckIds.split(",")));
        }
        Map<String,Object> rootNode = new HashMap<String,Object>();
        rootNode.put("id", "0");
        rootNode.put("name", "服务事项树");
        rootNode.put("nocheck", true);
        rootNode.put("open",true);
        List<Map<String,Object>> typeList = sercatalogService.findCatalogList(null);
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
            List<Map<String,Object>> itemList = this.getItemList(typeId);
            if(itemList!=null&&itemList.size()>0){
                for(Map<String,Object> item:itemList){
                    String itemId = (String) item.get("SERITEM_ID");
                    if(needCheckIdSet.contains(itemId)){
                        item.put("checked", true);
                    }
                    item.put("id", item.get("SERITEM_ID"));
                    item.put("name", item.get("SERITEM_NAME"));
                }
                type.put("children", itemList);
            }
        }
        rootNode.put("children", typeList);
        return JSON.toJSONString(rootNode);
    }
    
    /**
     * 根据filter获取网格项目
     * @param sqlFilter
     * @return
     */
    public List<Map<String,Object>> findGridItemList(SqlFilter sqlFilter){
        StringBuffer sql = new StringBuffer(this.getItemInfoPreSql());
        List<Object> params = new ArrayList<Object>();
        String selectedRecordIds = sqlFilter.getRequest().getParameter("selectedRecordIds");
        String iconfont = sqlFilter.getRequest().getParameter("iconfont");
        String itemconf = sqlFilter.getRequest().getParameter("itemconf");
        Map<String,String> getGridItemConf = PlatUICompUtil.getGridItemConfMap(itemconf);
        if(StringUtils.isNotEmpty(selectedRecordIds)){
            sql.append(" AND T.SERITEM_ID IN ");
            sql.append(PlatStringUtil.getSqlInCondition(selectedRecordIds));
            sql.append(" ORDER BY T.SERITEM_CREATETIME DESC");
            List<Map<String,Object>> list = dao.findBySql(sql.toString(),params.toArray(), null);
            list = PlatUICompUtil.getGridItemList("SERITEM_ID", iconfont, getGridItemConf, list);
            return list;
        }else{
            return null;
        }
    }
    
    /**
     * 获取获得事项信息的前缀SQL
     * @return
     */
    public String getItemInfoPreSql(){
        StringBuffer sql = new StringBuffer("select t.seritem_id,t.seritem_code,t.seritem_name from ");
        sql.append("PLAT_WSBS_SERITEM t where 1=1");
        return sql.toString();
    }
}

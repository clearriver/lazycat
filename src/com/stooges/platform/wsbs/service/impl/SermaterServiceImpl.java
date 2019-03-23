/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.wsbs.dao.SermaterDao;
import com.stooges.platform.wsbs.service.SermaterService;

/**
 * 描述 材料表业务相关service实现类
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-15 11:26:44
 */
@Service("sermaterService")
public class SermaterServiceImpl extends BaseServiceImpl implements SermaterService {

    /**
     * 所引入的dao
     */
    @Resource
    private SermaterDao dao;
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
     * 获取材料列表
     * @param sqlFilter
     * @param fieldInfo
     * @return
     */
    @Override
    public List<Map<String,Object>> findBySqlFilter(SqlFilter sqlFilter,Map<String,Object> fieldInfo){
        String SERITEM_ID = sqlFilter.getRequest().getParameter("Q_M.SERITEM_ID_EQ");
        if(StringUtils.isNotEmpty(SERITEM_ID)){
            sqlFilter.addFilter("O_M.SERMATER_SN", "ASC", 2);
            List<Object> params = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer("SELECT M.SERMATER_ID,M.SERMATER_SN,M.SERMATER_CODE,M.SERMATER_NAME,");
            sql.append("M.SERMATER_ISNEED,M.SERMATER_PRAMA FROM PLAT_WSBS_SERMATER M ");
            String exeSql = dao.getQuerySql(sqlFilter, sql.toString(), params);
            List<Map<String, Object>> materList = dao.findBySql(exeSql,
                    params.toArray(), sqlFilter.getPagingBean());
            return materList;
       }else{
           return null;
       }
        
    }
    /**
     * 根据服务事项ID获取最大排序
     * @param serItemId
     * @return
     */
    public int getMaxSn(String serItemId){
        return dao.getMaxSn(serItemId);
    }
    
    /**
     * 描述 更新排序字段
     * @param materIds
     */
    public void updateSn(String[] materIds){
        dao.updateSn(materIds);
    }
    
    /**
     * 保存材料信息,级联附件
     * @param sermater
     * @return
     */
    public Map<String,Object> saveCascadeFileAttach(Map<String,Object> sermater){
        String TPL_FILES_JSON = (String) sermater.get("TPL_FILES_JSON");
        sermater = dao.saveOrUpdate("PLAT_WSBS_SERMATER",
                sermater,AllConstants.IDGENERATOR_UUID,null);
        String SERMATER_ID = (String) sermater.get("SERMATER_ID");
        fileAttachService.saveFileAttachs(TPL_FILES_JSON, "PLAT_WSBS_SERMATER",SERMATER_ID,null);
        return sermater;
    }
    
    /**
     * 描述 修改绑定材料是否为必须提供
     * @param isneed
     * @param materIds
     */
    public void updateIsneed(String isneed, String materIds) {
        dao.updateIsneed(isneed,materIds);
    }
}

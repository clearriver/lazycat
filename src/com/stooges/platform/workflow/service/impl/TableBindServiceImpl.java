/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.workflow.dao.TableBindDao;
import com.stooges.platform.workflow.service.TableBindService;

/**
 * 描述 流程列表绑定业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-12 09:53:34
 */
@Service("tableBindService")
public class TableBindServiceImpl extends BaseServiceImpl implements TableBindService {

    /**
     * 所引入的dao
     */
    @Resource
    private TableBindDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据流程定义和表格类型获取绑定的设计界面代码
     * @param defId
     * @param tableType
     * @return
     */
    public String getTableBindDesignCode(String defId,String tableType){
        StringBuffer sql = new StringBuffer("select T.TABLEBIND_DESIGNCODE FROM");
        sql.append(" JBPM6_TABLEBIND T WHERE T.TABLEBIND_DEFID=? ");
        sql.append(" AND T.TABLEBIND_TYPE=? ORDER BY T.TABLEBIND_CREATETIME ASC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(),
                new Object[]{defId,tableType}, null);
        String designCode = null;
        if(list!=null&&list.size()>0){
            Map<String,Object> result = list.get(0);
            String TABLEBIND_DESIGNCODE = (String) result.get("TABLEBIND_DESIGNCODE");
            designCode = TABLEBIND_DESIGNCODE;
        }
        return designCode;
    }
    
    /**
     * 根据流程定义和版本获取列表数据
     * @param defId
     * @param defVersion
     * @return
     */
    public List<Map<String,Object>> findByDefIdAndVersion(String defId,int defVersion){
        StringBuffer sql = new StringBuffer("SELECT * FROM ");
        sql.append("JBPM6_TABLEBIND J WHERE J.TABLEBIND_DEFID=? ");
        sql.append(" ORDER BY J.TABLEBIND_ID ASC ");
        return dao.findBySql(sql.toString(), new Object[]{defId}, null);
    }
  
}

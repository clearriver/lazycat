/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.appmodel.dao.ModuleDao;
import com.stooges.platform.appmodel.service.ModuleService;

/**
 * 描述
 * @author 胡裕
 * @created 2017年2月4日 上午9:23:11
 */
@Service("moduleService")
public class ModuleServiceImpl extends BaseServiceImpl implements ModuleService {

    /**
     * 所引入的dao
     */
    @Resource
    private ModuleDao dao;

    /**
     * 
     */
    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 删除模块信息，并且级联更新设计的编码
     * @param moduleId
     */
    public void deleteModuleCascade(String moduleId){
        StringBuffer sql = new StringBuffer("UPDATE PLAT_APPMODEL_DESIGN SET DESIGN_MODULEID=?");
        sql.append(" WHERE DESIGN_MODULEID IN (");
        sql.append("SELECT T.MODULE_ID FROM PLAT_APPMODEL_MODULE T ");
        sql.append("WHERE T.MODULE_PATH LIKE ? )");
        dao.executeSql(sql.toString(), new Object[]{"","%."+moduleId+".%"});
        sql = new StringBuffer("DELETE FROM PLAT_APPMODEL_MODULE ");
        sql.append("WHERE MODULE_PATH LIKE ? ");
        dao.executeSql(sql.toString(), new Object[]{"%."+moduleId+".%"});
    }

}

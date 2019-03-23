/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.appmodel.dao.FormcontrolDao;

/**
 * 描述 Formcontrol业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-02-21 17:26:03
 */
@Repository
public class FormcontrolDaoImpl extends BaseDaoImpl implements FormcontrolDao {

    /**
     * 根据设计ID获取控件ID集合
     * @param designId
     * @return
     */
    public List<String> findControlIds(String designId){
        StringBuffer sql = new StringBuffer("select T.FORMCONTROL_ID ");
        sql.append("FROM PLAT_APPMODEL_FORMCONTROL T WHERE T.FORMCONTROL_DESIGN_ID=?");
        List<String> list = this.getJdbcTemplate().queryForList(sql.toString(),
                new Object[]{designId},String.class);
        return list;
    }
    
    /**
     * 根据表单控件ID获取模版代码
     * @param formControlId
     * @return
     */
    public String getTplCode(String formControlId){
        StringBuffer sql = new StringBuffer("SELECT T.FORMCONTROL_TPLCODE FROM ");
        sql.append("PLAT_APPMODEL_FORMCONTROL T WHERE T.FORMCONTROL_ID=?");
        String tplCode = this.getJdbcTemplate().queryForObject(sql.toString(), 
                new Object[]{formControlId}, String.class);
        return tplCode;
    }
}

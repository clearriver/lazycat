/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao;

import java.util.List;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 Formcontrol业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-02-21 17:26:03
 */
public interface FormcontrolDao extends BaseDao {
    /**
     * 根据设计ID获取控件ID集合
     * @param designId
     * @return
     */
    public List<String> findControlIds(String designId);
    /**
     * 根据表单控件ID获取模版代码
     * @param formControlId
     * @return
     */
    public String getTplCode(String formControlId);
}

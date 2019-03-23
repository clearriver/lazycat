/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.appmodel.dao.PortalRowDao;

/**
 * 描述门户行业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 15:07:52
 */
@Repository
public class PortalRowDaoImpl extends BaseDaoImpl implements PortalRowDao {

    /**
     * 根据主题ID获取最大排序值
     * @param themeId
     * @return
     */
    public int getMaxSn(String themeId){
        StringBuffer sql = new StringBuffer("SELECT MAX(T.ROW_SN)");
        sql.append(" FROM PLAT_APPMODEL_PORTALROW T ");
        sql.append("WHERE T.ROW_THEMEID=? ");
        return this.getIntBySql(sql.toString(), new Object[]{themeId});
    }
}

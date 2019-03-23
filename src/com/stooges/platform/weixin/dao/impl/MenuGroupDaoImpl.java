/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.weixin.dao.MenuGroupDao;

/**
 * 描述菜单组业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-21 10:04:25
 */
@Repository
public class MenuGroupDaoImpl extends BaseDaoImpl implements MenuGroupDao {

    /**
     * 获取组的数量
     * @param publicId
     * @return
     */
    public int getGroupCount(String publicId){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append("PLAT_WEIXIN_MENUGROUP T WHERE T.MENUGROUP_PUBID=?");
        int count = this.getIntBySql(sql.toString(), new Object[]{publicId});
        return count;
    }
}

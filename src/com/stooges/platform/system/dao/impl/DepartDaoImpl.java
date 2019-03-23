/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.system.dao.DepartDao;

/**
 * 描述部门业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-09 09:43:59
 */
@Repository
public class DepartDaoImpl extends BaseDaoImpl implements DepartDao {

    /**
     * 根据单位ID和部门编码判断是否存在部门信息
     * @param companyId
     * @param departCode
     * @return
     */
    public boolean isExistsDepart(String companyId,String departCode){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append("PLAT_SYSTEM_DEPART D WHERE D.DEPART_COMPANYID=? ");
        sql.append(" AND D.DEPART_CODE=?");
        int count = this.getIntBySql(sql.toString(),new Object[]{companyId,departCode});
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
    
}

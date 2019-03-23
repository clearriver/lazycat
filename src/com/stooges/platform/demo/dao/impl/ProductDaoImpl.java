/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.demo.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.core.util.PlatDbUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.demo.dao.ProductDao;

/**
 * 描述产品信息业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-10 13:48:25
 */
@Repository
public class ProductDaoImpl extends BaseDaoImpl implements ProductDao {

    /**
     * 
     * @param productIds
     */
    public void updateIsShow(String productIds,String isShow){
        StringBuffer sql = new StringBuffer("UPDATE PLAT_DEMO_PRODUCT");
        sql.append(" SET PRODUCT_ISSHOW=? ");
        sql.append("WHERE PRODUCT_ID IN ").append(PlatStringUtil.getSqlInCondition(productIds));
        this.getJdbcTemplate().update(sql.toString(), new Object[]{isShow});
    }
    
    public void testList(){
        String sql = "select U.NAME as TABLE_NAME,cast(sys.extended_properties.value as varchar) AS COMMENTS from sysobjects U LEFT JOIN sys.extended_properties  on U.id=sys.extended_properties.major_id  WHERE U.TYPE='U' and sys.extended_properties.minor_id='0' ";
        System.out.println(this.getJdbcTemplate().queryForList(sql).size());
    }
}

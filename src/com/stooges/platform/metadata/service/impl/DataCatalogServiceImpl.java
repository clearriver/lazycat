/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.metadata.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.metadata.dao.DataCatalogDao;
import com.stooges.platform.metadata.service.DataCatalogService;

/**
 * 描述 数据目录业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2018-05-07 10:53:23
 */
@Service("dataCatalogService")
public class DataCatalogServiceImpl extends BaseServiceImpl implements DataCatalogService {

    /**
     * 所引入的dao
     */
    @Resource
    private DataCatalogDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据目录ID级联删除子孙目录
     * @param catalogId
     */
    public void deleteCascadeChild(String catalogId){
        StringBuffer sql = new StringBuffer("DELETE FROM PLAT_METADATA_CATALOG ");
        sql.append("WHERE CATALOG_PATH LIKE ? ");
        dao.executeSql(sql.toString(), new Object[]{"%."+catalogId+".%"});
    }
    
    /**
     * 根据资源ID获取目录id集合
     * @param resId
     * @return
     */
    public List<String> findCatalogIds(String resId){
        return dao.findCatalogIds(resId);
    }
}

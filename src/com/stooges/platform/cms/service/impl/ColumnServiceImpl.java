/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.appmodel.service.CommonUIService;
import com.stooges.platform.cms.dao.ColumnDao;
import com.stooges.platform.cms.service.ColumnService;

/**
 * 描述 网站栏目业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-05 15:05:57
 */
@Service("columnService")
public class ColumnServiceImpl extends BaseServiceImpl implements ColumnService {

    /**
     * 所引入的dao
     */
    @Resource
    private ColumnDao dao;
    /**
     * 
     */
    @Resource
    private CommonUIService commonUIService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据站点ID获取可选下拉树数据
     * @param siteId
     * @return
     */
    public List<Map<String,Object>> findSelectTree(String siteId){
        if(StringUtils.isNotEmpty(siteId)){
            StringBuffer paramsConfig = new StringBuffer("[TABLE_NAME:PLAT_CMS_COLUMN]");
            paramsConfig.append("[TREE_IDANDNAMECOL:COLUMN_ID,COLUMN_NAME]");
            paramsConfig.append("[TREE_QUERYFIELDS:COLUMN_PARENTID,COLUMN_PATH]");
            paramsConfig.append("[FILTERS:COLUMN_PARENTID_EQ|0,");
            paramsConfig.append("COLUMN_SITEID_EQ|").append(siteId);
            paramsConfig.append("]");
            return commonUIService.findGenTreeSelectorDatas(paramsConfig.toString());
        }else{
            return null;
        }
    }
  
}

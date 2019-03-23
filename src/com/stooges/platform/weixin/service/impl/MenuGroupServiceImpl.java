/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.weixin.dao.MenuGroupDao;
import com.stooges.platform.weixin.service.MenuGroupService;

/**
 * 描述 菜单组业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-21 10:04:25
 */
@Service("menuGroupService")
public class MenuGroupServiceImpl extends BaseServiceImpl implements MenuGroupService {

    /**
     * 所引入的dao
     */
    @Resource
    private MenuGroupDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取菜单组列表数据源
     * @param queryParamJson
     * @return
     */
    public List<Map<String,Object>> findGroupList(String publicId){
        List<Map<String,Object>> list = null;
        if(StringUtils.isNotEmpty(publicId)){
            StringBuffer sql = new StringBuffer("select T.MENUGROUP_ID AS VALUE,T.MENUGROUP_NAME AS LABEL ");
            sql.append("from PLAT_WEIXIN_MENUGROUP T ");
            sql.append(" WHERE T.MENUGROUP_PUBID=? ");
            sql.append("ORDER BY T.MENUGROUP_SN ASC");
            list = dao.findBySql(sql.toString(),new Object[]{publicId}, null);
        }
        if(list==null){
            list = new ArrayList<Map<String,Object>>();
        }
        return list;
    }
    
    /**
     * 获取组的数量
     * @param publicId
     * @return
     */
    public int getGroupCount(String publicId){
        return dao.getGroupCount(publicId);
    }
  
}

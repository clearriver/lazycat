/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.cms.dao.TemplateDao;
import com.stooges.platform.cms.service.TemplateService;

/**
 * 描述 模版业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-03 17:19:57
 */
@Service("templateService")
public class TemplateServiceImpl extends BaseServiceImpl implements TemplateService {

    /**
     * 所引入的dao
     */
    @Resource
    private TemplateDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取可选模版列表
     * @param param
     * @return
     */
    public List<Map<String,Object>> findForSelect(String param){
        StringBuffer sql = new StringBuffer("SELECT T.TEMPLATE_ID ");
        sql.append("AS VALUE,T.TEMPLATE_NAME AS LABEL FROM ");
        sql.append("PLAT_CMS_TEMPLATE T ORDER BY T.TEMPLATE_CREATETIME ASC");
        return dao.findBySql(sql.toString(), null, null);
    }
    
    /**
     * 设置首页的数据
     * @param request
     * @return
     */
    public Map<String,Object> getIndexData(HttpServletRequest request){
        System.out.println("传递的KEYS："+request.getParameterMap().keySet());
        return new HashMap<String,Object>();
    }
  
}

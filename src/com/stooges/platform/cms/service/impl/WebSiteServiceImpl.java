/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.cms.dao.WebSiteDao;
import com.stooges.platform.cms.service.TemplateService;
import com.stooges.platform.cms.service.WebSiteService;

/**
 * 描述 站点业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-03 19:10:38
 */
@Service("webSiteService")
public class WebSiteServiceImpl extends BaseServiceImpl implements WebSiteService {

    /**
     * 所引入的dao
     */
    @Resource
    private WebSiteDao dao;
    /**
     * 
     */
    @Resource
    private TemplateService templateService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 可选站点下拉框数据源
     * @param param
     * @return
     */
    public List<Map<String,Object>> findForSelect(String param){
        StringBuffer sql = new StringBuffer("SELECT T.SITE_ID");
        sql.append(" AS VALUE,T.SITE_NAME AS LABEL FROM ");
        sql.append("PLAT_CMS_WEBSITE T ORDER BY T.SITE_CREATETIME ASC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), null, null);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("VALUE", "-1");
        map.put("LABEL", "后台管理系统");
        list.add(map);
        return list;
    }
    
    /**
     * 删除网站静态首页
     * @param siteIds
     */
    public void deleteIndexHtmls(String siteIds){
        for(String siteId:siteIds.split(",")){
            Map<String,Object> webSite =  this.getRecord("PLAT_CMS_WEBSITE"
                    ,new String[]{"SITE_ID"},new Object[]{siteId});
            String SITE_INDEXTPLID = (String) webSite.get("SITE_INDEXTPLID");
            Map<String,Object> template = templateService.getRecord("PLAT_CMS_TEMPLATE",
                    new String[]{"TEMPLATE_ID"}, new Object[]{SITE_INDEXTPLID});
            String TEMPLATE_NUM = (String) template.get("TEMPLATE_NUM");
            String appPath = PlatAppUtil.getAppAbsolutePath();
            StringBuffer htmlPath = new StringBuffer(appPath);
            htmlPath.append("webpages/website/genhtmls/").append(TEMPLATE_NUM);
            htmlPath.append(".jsp");
            File htmlFile = new File(htmlPath.toString());
            if(htmlFile.exists()){
                htmlFile.delete();
            }
        }
    }
  
}

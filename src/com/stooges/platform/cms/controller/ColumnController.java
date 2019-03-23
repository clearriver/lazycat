/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.cms.service.ColumnService;
import com.stooges.platform.cms.service.WebSiteService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 网站栏目业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-05 15:05:57
 */
@Controller  
@RequestMapping("/cms/ColumnController")  
public class ColumnController extends BaseController {
    /**
     * 
     */
    @Resource
    private ColumnService columnService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private WebSiteService webSiteService;
    
    /**
     * 删除网站栏目数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String treeNodeId = request.getParameter("treeNodeId");
        columnService.deleteRecords("PLAT_CMS_COLUMN","COLUMN_ID",treeNodeId.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改网站栏目数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> column = PlatBeanUtil.getMapFromRequest(request);
        String COLUMN_ID = (String) column.get("COLUMN_ID");
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        column = columnService.saveOrUpdateTreeData("PLAT_CMS_COLUMN",
                column,AllConstants.IDGENERATOR_UUID,null);
        column.put("success", true);
        this.printObjectJsonString(column, response);
    }
    
    
    /**
     * 跳转到网站栏目列表界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goView")
    public ModelAndView goView(HttpServletRequest request) {
        List<Map<String,Object>> webSiteList = webSiteService.findForSelect(null);
        String selectWebSiteId = (String) webSiteList.get(0).get("VALUE");
        request.setAttribute("WEBSITE_ID",selectWebSiteId);
        return PlatUICompUtil.goDesignUI("columnmanager", request);
    }
    
    /**
     * 跳转到网站栏目表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        String COLUMN_ID = request.getParameter("COLUMN_ID");
        String COLUMN_PARENTID = request.getParameter("COLUMN_PARENTID");
        String WEBSITE_ID = request.getParameter("WEBSITE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> column = null;
        if(StringUtils.isNotEmpty(COLUMN_ID)){
            column = this.columnService.getRecord("PLAT_CMS_COLUMN"
                    ,new String[]{"COLUMN_ID"},new Object[]{COLUMN_ID});
            COLUMN_PARENTID = (String) column.get("COLUMN_PARENTID");
        }
        Map<String,Object> parentColumn = null;
        if(COLUMN_PARENTID.equals("0")){
            parentColumn = new HashMap<String,Object>();
            parentColumn.put("COLUMN_ID",COLUMN_PARENTID);
            parentColumn.put("COLUMN_NAME","网站栏目树");
        }else{
            parentColumn = this.columnService.getRecord("PLAT_CMS_COLUMN",
                    new String[]{"COLUMN_ID"}, new Object[]{COLUMN_PARENTID});
        }
        Map<String,Object> webSite = null;
        if(!WEBSITE_ID.equals("-1")){
            webSite = this.webSiteService.getRecord("PLAT_CMS_WEBSITE", 
                    new String[]{"SITE_ID"},new Object[]{WEBSITE_ID});
        }else{
            webSite = new HashMap<String,Object>();
            webSite.put("SITE_ID", "-1");
            webSite.put("SITE_NAME", "后台管理系统");
        }
        if(column==null){
            column = new HashMap<String,Object>();
        }
        column.put("COLUMN_PARENTID",parentColumn.get("COLUMN_ID"));
        column.put("COLUMN_PARENTNAME",parentColumn.get("COLUMN_NAME"));
        column.put("COLUMN_SITEID",webSite.get("SITE_ID"));
        column.put("COLUMN_SITENAME",webSite.get("SITE_NAME"));
        request.setAttribute("column", column);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

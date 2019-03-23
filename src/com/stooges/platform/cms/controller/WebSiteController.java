/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.controller;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatFileUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.cms.service.TemplateService;
import com.stooges.platform.cms.service.WebSiteService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 站点业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-03 19:10:38
 */
@Controller  
@RequestMapping("/cms/WebSiteController")  
public class WebSiteController extends BaseController {
    /**
     * 
     */
    @Resource
    private WebSiteService webSiteService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private TemplateService templateService;
    
    /**
     * 删除站点静态页
     * @param request
     * @param response
     */
    @RequestMapping(params = "delGenHtml")
    public void delGenHtml(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        webSiteService.deleteIndexHtmls(selectColValues);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 删除站点数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        webSiteService.deleteRecords("PLAT_CMS_WEBSITE","SITE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("站点管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的站点", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改站点数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> webSite = PlatBeanUtil.getMapFromRequest(request);
        String SITE_ID = (String) webSite.get("SITE_ID");
        if(StringUtils.isEmpty(SITE_ID)){
            webSite.put("SITE_CREATETIME", PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        webSite = webSiteService.saveOrUpdate("PLAT_CMS_WEBSITE",
                webSite,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //webSite = webSiteService.saveOrUpdateTreeData("PLAT_CMS_WEBSITE",
        //        webSite,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(SITE_ID)){
            sysLogService.saveBackLog("站点管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+SITE_ID+"]站点", request);
        }else{
            SITE_ID = (String) webSite.get("SITE_ID");
            sysLogService.saveBackLog("站点管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+SITE_ID+"]站点", request);
        }
        webSite.put("success", true);
        this.printObjectJsonString(webSite, response);
    }
    
    /**
     * 跳转到站点表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String SITE_ID = request.getParameter("SITE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> webSite = null;
        if(StringUtils.isNotEmpty(SITE_ID)){
            webSite = this.webSiteService.getRecord("PLAT_CMS_WEBSITE"
                    ,new String[]{"SITE_ID"},new Object[]{SITE_ID});
        }else{
            webSite = new HashMap<String,Object>();
        }
        request.setAttribute("website", webSite);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    
    /**
     * 预览站点
     * @param request
     * @return
     */
    @RequestMapping(params = "preview")
    public ModelAndView preview(HttpServletRequest request) {
        String SITE_ID = request.getParameter("SITE_ID");
        Map<String,Object> webSite =  this.webSiteService.getRecord("PLAT_CMS_WEBSITE"
                ,new String[]{"SITE_ID"},new Object[]{SITE_ID});
        String SITE_INDEXTPLID = (String) webSite.get("SITE_INDEXTPLID");
        Map<String,Object> template = templateService.getRecord("PLAT_CMS_TEMPLATE",
                new String[]{"TEMPLATE_ID"}, new Object[]{SITE_INDEXTPLID});
        String TEMPLATE_NUM = (String) template.get("TEMPLATE_NUM");
        String appPath = PlatAppUtil.getAppAbsolutePath();
        StringBuffer htmlPath = new StringBuffer(appPath);
        htmlPath.append("webpages/website/genhtmls/").append(TEMPLATE_NUM);
        htmlPath.append(".jsp");
        File htmlFile = new File(htmlPath.toString());
        //不存在生成代码
        if(!htmlFile.exists()){
            String TEMPLATE_DATEINTER = (String) template.get("TEMPLATE_DATEINTER");
            Map<String,Object> resultDatas = new HashMap<String,Object>();
            if(StringUtils.isNotEmpty(TEMPLATE_DATEINTER)){
                String beanId = TEMPLATE_DATEINTER.split("[.]")[0];
                String method = TEMPLATE_DATEINTER.split("[.]")[1];
                Object serviceBean = PlatAppUtil.getBean(beanId);
                if (serviceBean != null) {
                    Method invokeMethod;
                    try {
                        invokeMethod = serviceBean.getClass().getDeclaredMethod(method,
                                new Class[] { HttpServletRequest.class });
                        resultDatas = (Map<String,Object>) invokeMethod.invoke(serviceBean,
                                new Object[] { request });
                    } catch (Exception e) {
                        PlatLogUtil.printStackTrace(e);
                    }
                }
            }
            String TEMPLATE_CODE = (String) template.get("TEMPLATE_CODE");
            String resultHtml = PlatStringUtil.getFreeMarkResult(TEMPLATE_CODE, resultDatas);
            PlatFileUtil.writeDataToDisk(resultHtml, htmlPath.toString(), "UTF-8");
        }
        return new ModelAndView("website/genhtmls/"+TEMPLATE_NUM);
    }
}

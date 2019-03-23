/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.controller;

import java.io.File;
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
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.cms.service.ArticleService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 文章信息业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-11 10:47:32
 */
@Controller  
@RequestMapping("/cms/ArticleController")  
public class ArticleController extends BaseController {
    /**
     * 
     */
    @Resource
    private ArticleService articleService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 改变文章置顶信息
     * @param request
     * @param response
     */
    @RequestMapping(params = "updatetop")
    public void updatetop(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        String isTop = request.getParameter("isTop");
        articleService.updateIsTop(selectColValues, isTop);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 删除文章信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        articleService.deleteRecords("PLAT_CMS_ARTICLE","ARTICLE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("文章管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的文章信息", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改文章信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> article = PlatBeanUtil.getMapFromRequest(request);
        String ARTICLE_ID = (String) article.get("ARTICLE_ID");
        article = articleService.saveCascadeImg(article);
        if(StringUtils.isNotEmpty(ARTICLE_ID)){
            sysLogService.saveBackLog("文章管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+ARTICLE_ID+"]文章信息", request);
        }else{
            ARTICLE_ID = (String) article.get("ARTICLE_ID");
            sysLogService.saveBackLog("文章管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+ARTICLE_ID+"]文章信息", request);
        }
        article.put("success", true);
        this.printObjectJsonString(article, response);
    }
    
    /**
     * 更新文章其它信息
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOther")
    public void saveOther(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> article = PlatBeanUtil.getMapFromRequest(request);
        article = articleService.saveOrUpdate("PLAT_CMS_ARTICLE", article,
                AllConstants.IDGENERATOR_UUID, null);
        article.put("success", true);
        this.printObjectJsonString(article, response);
    }
    
    /**
     * 更新文章附加信息
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveAttach")
    public void saveAttach(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> article = PlatBeanUtil.getMapFromRequest(request);
        article = articleService.saveOrUpdate("PLAT_CMS_ARTICLE", article,
                AllConstants.IDGENERATOR_UUID, null);
        articleService.deleteGenHtmlCode(article.get("ARTICLE_ID").toString());
        article.put("success", true);
        this.printObjectJsonString(article, response);
    }
    
    
    /**
     * 跳转到文章信息表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String ARTICLE_ID = request.getParameter("ARTICLE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> article = null;
        if(StringUtils.isNotEmpty(ARTICLE_ID)){
            article = this.articleService.getRecord("PLAT_CMS_ARTICLE"
                    ,new String[]{"ARTICLE_ID"},new Object[]{ARTICLE_ID});
        }else{
            article = new HashMap<String,Object>();
            article.put("ARTICLE_PUBTIME",PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        request.setAttribute("article", article);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 跳转到文章附加信息界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goAttach")
    public ModelAndView goAttach(HttpServletRequest request) {
        String ARTICLE_TYPE = request.getParameter("ARTICLE_TYPE");
        String ARTICLE_ID = request.getParameter("ARTICLE_ID");
        Map<String,Object> article = null;
        if(StringUtils.isNotEmpty(ARTICLE_ID)){
            article = this.articleService.getRecord("PLAT_CMS_ARTICLE"
                    ,new String[]{"ARTICLE_ID"},new Object[]{ARTICLE_ID});
        }
        request.setAttribute("article", article);
        request.setAttribute("DESIGN_CODE","article_new");
        return new ModelAndView("background/cms/article/attachform");
        /*if(ARTICLE_TYPE.equals(ArticleService.TYPE_NEWS)){
            request.setAttribute("DESIGN_CODE","article_new");
            return new ModelAndView("background/cms/article/attachform");
        }else{
            return PlatUICompUtil.goDesignUI("article_new", request);
        }*/
    }
    
    /**
     * 跳转到文章显示界面
     * @param request
     * @return
     */
    @RequestMapping(params = "view")
    public ModelAndView view(HttpServletRequest request) {
        String ARTICLE_SIGN = request.getParameter("ARTICLE_SIGN");
        String appPath = PlatAppUtil.getAppAbsolutePath();
        StringBuffer htmlPath = new StringBuffer(appPath);
        htmlPath.append("webpages/website/genhtmls/article/").append(ARTICLE_SIGN);
        htmlPath.append(".jsp");
        File htmlFile = new File(htmlPath.toString());
        if(!htmlFile.exists()){
            String resultHtml = articleService.getArticeHtmlCode(ARTICLE_SIGN, request);
            PlatFileUtil.writeDataToDisk(resultHtml, htmlPath.toString(), "UTF-8");
        }
        return new ModelAndView("website/genhtmls/article/"+ARTICLE_SIGN);
    }
}

/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.webstatistics.service.DomainService;

/**
 * 
 * 描述 访问来源业务相关Controller
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 11:36:14
 */
@Controller  
@RequestMapping("/webstatistics/DomainController")  
public class DomainController extends BaseController {
    /**
     * 
     */
    @Resource
    private DomainService domainService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除访问来源数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        domainService.deleteRecords("PLAT_WEBSTATISTICS_DOMAIN","DOMAIN_ID",selectColValues.split(","));
        sysLogService.saveBackLog("网站统计",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的访问来源", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改访问来源数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> domain = PlatBeanUtil.getMapFromRequest(request);
        String DOMAIN_DAY = (String) domain.get("DOMAIN_DAY");
       /* domain = domainService.saveOrUpdate("PLAT_WEBSTATISTICS_DOMAIN",
                domain,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //domain = domainService.saveOrUpdateTreeData("PLAT_WEBSTATISTICS_DOMAIN",
        //        domain,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(DOMAIN_ID)){
            sysLogService.saveBackLog("网站统计",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+DOMAIN_ID+"]访问来源", request);
        }else{
            DOMAIN_ID = (String) domain.get("DOMAIN_ID");
            sysLogService.saveBackLog("网站统计",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+DOMAIN_ID+"]访问来源", request);
        }*/
        //domainService.saveDomainData(DOMAIN_DAY);
        domain.put("success", true);
        this.printObjectJsonString(domain, response);
    }
    
    /**
     * 跳转到访问来源表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String DOMAIN_ID = request.getParameter("DOMAIN_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> domain = null;
        if(StringUtils.isNotEmpty(DOMAIN_ID)){
            domain = this.domainService.getRecord("PLAT_WEBSTATISTICS_DOMAIN"
                    ,new String[]{"DOMAIN_ID"},new Object[]{DOMAIN_ID});
        }else{
            domain = new HashMap<String,Object>();
        }
        request.setAttribute("domain", domain);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String DOMAIN_ID = request.getParameter("DOMAIN_ID");
        String DOMAIN_PARENTID = request.getParameter("DOMAIN_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> domain = null;
        if(StringUtils.isNotEmpty(DOMAIN_ID)){
            domain = this.domainService.getRecord("PLAT_WEBSTATISTICS_DOMAIN"
                    ,new String[]{"DOMAIN_ID"},new Object[]{DOMAIN_ID});
            DOMAIN_PARENTID = (String) domain.get("Domain_PARENTID");
        }
        Map<String,Object> parentDomain = null;
        if(DOMAIN_PARENTID.equals("0")){
            parentDomain = new HashMap<String,Object>();
            parentDomain.put("DOMAIN_ID",DOMAIN_PARENTID);
            parentDomain.put("DOMAIN_NAME","访问来源树");
        }else{
            parentDomain = this.domainService.getRecord("PLAT_WEBSTATISTICS_DOMAIN",
                    new String[]{"DOMAIN_ID"}, new Object[]{DOMAIN_PARENTID});
        }
        if(domain==null){
            domain = new HashMap<String,Object>();
        }
        domain.put("DOMAIN_PARENTID",parentDomain.get("DOMAIN_ID"));
        domain.put("DOMAIN_PARENTNAME",parentDomain.get("DOMAIN_NAME"));
        request.setAttribute("domain", domain);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

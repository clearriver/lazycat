/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.controller;

import java.util.Calendar;
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
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.webstatistics.service.NewOldService;
import com.stooges.platform.webstatistics.service.SiteInfoService;

/**
 * 
 * 描述 新建用户对比业务相关Controller
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 20:17:53
 */
@Controller  
@RequestMapping("/webstatistics/NewOldController")  
public class NewOldController extends BaseController {
    /**
     * 
     */
    @Resource
    private NewOldService newOldService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private SiteInfoService siteInfoService;
    
    /**
     * 删除新建用户对比数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        newOldService.deleteRecords("PLAT_WEBSTATISTICS_NEWOLD","NEWOLD_ID",selectColValues.split(","));
        sysLogService.saveBackLog("新建用户",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的新建用户对比", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改新建用户对比数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> newOld = PlatBeanUtil.getMapFromRequest(request);
        String NEWOLD_ID = (String) newOld.get("NEWOLD_ID");
        newOld = newOldService.saveOrUpdate("PLAT_WEBSTATISTICS_NEWOLD",
                newOld,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //newOld = newOldService.saveOrUpdateTreeData("PLAT_WEBSTATISTICS_NEWOLD",
        //        newOld,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(NEWOLD_ID)){
            sysLogService.saveBackLog("新建用户",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+NEWOLD_ID+"]新建用户对比", request);
        }else{
            NEWOLD_ID = (String) newOld.get("NEWOLD_ID");
            sysLogService.saveBackLog("新建用户",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+NEWOLD_ID+"]新建用户对比", request);
        }
        newOld.put("success", true);
        this.printObjectJsonString(newOld, response);
    }
    
    /**
     * 跳转到新建用户对比表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String NEWOLD_ID = request.getParameter("NEWOLD_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> newOld = null;
        if(StringUtils.isNotEmpty(NEWOLD_ID)){
            newOld = this.newOldService.getRecord("PLAT_WEBSTATISTICS_NEWOLD"
                    ,new String[]{"NEWOLD_ID"},new Object[]{NEWOLD_ID});
        }else{
            newOld = new HashMap<String,Object>();
        }
        request.setAttribute("newOld", newOld);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月10日 下午8:36:59
     * @param request
     * @return
     */
    @RequestMapping(params = "newold")
    public ModelAndView newold(HttpServletRequest request) {
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String preDate = PlatDateTimeUtil.formatDate(
                PlatDateTimeUtil.getNextTime(new Date(), Calendar.DATE, -6), "yyyy-MM-dd");
        request.setAttribute("preDate", preDate);
        request.setAttribute("curDate", curDate);
        String defaultSiteId = siteInfoService.getDefaultSiteId();
        request.setAttribute("siteId", defaultSiteId);
        return new ModelAndView("background/webstatistics/newold");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月10日 下午8:37:15
     * @param request
     * @param response
     */
    @RequestMapping(params = "getNewOldData")
    public void getNewOldData(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String startDay = request.getParameter("startDay");
        String endDay = request.getParameter("endDay");
        String type = request.getParameter("type");
        String siteId = request.getParameter("siteId");
        result = newOldService.getNewOldData(startDay,endDay,type);
        this.printObjectJsonString(result, response);
    }
}

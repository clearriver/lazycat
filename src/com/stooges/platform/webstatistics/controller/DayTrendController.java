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

import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.webstatistics.service.DayTrendService;
import com.stooges.platform.webstatistics.service.InviewService;
import com.stooges.platform.webstatistics.service.MapDataService;
import com.stooges.platform.webstatistics.service.NewOldService;
import com.stooges.platform.webstatistics.service.SiteInfoService;

/**
 * 
 * 描述 日期趋势业务相关Controller
 * @author 李俊
 * @version 1.0
 * @created 2017-07-26 21:59:29
 */
@Controller  
@RequestMapping("/webstatistics/DayTrendController")  
public class DayTrendController extends BaseController {
    /**
     * 
     */
    @Resource
    private DayTrendService dayTrendService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private InviewService inviewService;
    /**
     * 
     */
    @Resource
    private NewOldService newOldService;
    /**
     * 
     */
    @Resource
    private MapDataService mapDataService;
    /**
     * 
     */
    @Resource
    private SiteInfoService siteInfoService;
    
    /**
     * 删除日期趋势数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        dayTrendService.deleteRecords("PLAT_WEBSTATISTICS_DAYTREND","DAYTREND_ID",selectColValues.split(","));
        sysLogService.saveBackLog("网站统计",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的日期趋势", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改日期趋势数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> dayTrend = PlatBeanUtil.getMapFromRequest(request);
        dayTrend.put("success", true);
        this.printObjectJsonString(dayTrend, response);
    }
    
    /**
     * 跳转到日期趋势表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String DAYTREND_ID = request.getParameter("DAYTREND_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> dayTrend = null;
        if(StringUtils.isNotEmpty(DAYTREND_ID)){
            dayTrend = this.dayTrendService.getRecord("PLAT_WEBSTATISTICS_DAYTREND"
                    ,new String[]{"DAYTREND_ID"},new Object[]{DAYTREND_ID});
        }else{
            dayTrend = new HashMap<String,Object>();
        }
        request.setAttribute("dayTrend", dayTrend);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月7日 下午9:50:56
     * @param request
     * @return
     */
    @RequestMapping(params = "timeTrend")
    public ModelAndView timeTrend(HttpServletRequest request) {
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String defaultSiteId = siteInfoService.getDefaultSiteId();
        request.setAttribute("curDate", curDate);
        request.setAttribute("siteId", defaultSiteId);
        return new ModelAndView("background/webstatistics/timeTrend");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月8日 下午7:39:30
     * @param request
     * @param response
     */
    @RequestMapping(params = "getTimeTrendData")
    public void getTimeTrendData(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String searchDay = request.getParameter("searchDay");
        String siteId = request.getParameter("siteId");
        result = dayTrendService.getTimeTrendData(searchDay,siteId);
        this.printObjectJsonString(result, response);
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月8日 下午9:38:17
     * @param request
     * @return
     */
    @RequestMapping(params = "dayTrend")
    public ModelAndView dayTrend(HttpServletRequest request) {
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String preDate = PlatDateTimeUtil.formatDate(
                PlatDateTimeUtil.getNextTime(new Date(), Calendar.DATE, -6), "yyyy-MM-dd");
        String defaultSiteId = siteInfoService.getDefaultSiteId();
        request.setAttribute("preDate", preDate);
        request.setAttribute("curDate", curDate);
        request.setAttribute("siteId", defaultSiteId);
        return new ModelAndView("background/webstatistics/dayTrend");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月8日 下午9:38:37
     * @param request
     * @param response
     */
    @RequestMapping(params = "getDayTrendData")
    public void getDayTrendData(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String startDay = request.getParameter("startDay");
        String endDay = request.getParameter("endDay");
        String siteId = request.getParameter("siteId");
        result = dayTrendService.getDayTrendData(startDay,endDay,siteId);
        this.printObjectJsonString(result, response);
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月9日 下午7:16:20
     * @param request
     * @return
     */
    @RequestMapping(params = "topSource")
    public ModelAndView topSource(HttpServletRequest request) {
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String preDate = PlatDateTimeUtil.formatDate(
                PlatDateTimeUtil.getNextTime(new Date(), Calendar.DATE, -6), "yyyy-MM-dd");
        request.setAttribute("preDate", preDate);
        request.setAttribute("curDate", curDate);
        String defaultSiteId = siteInfoService.getDefaultSiteId();
        request.setAttribute("siteId", defaultSiteId);
        return new ModelAndView("background/webstatistics/topSource");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月9日 下午7:17:44
     * @param request
     * @param response
     */
    @RequestMapping(params = "getTopSourceData")
    public void getTopSourceData(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String startDay = request.getParameter("startDay");
        String endDay = request.getParameter("endDay");
        String type = request.getParameter("type");
        String siteId = request.getParameter("siteId");
        result = dayTrendService.getTopSourceData(startDay,endDay,type,siteId);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月9日 下午7:16:20
     * @param request
     * @return
     */
    @RequestMapping(params = "topEntrance")
    public ModelAndView topEntrance(HttpServletRequest request) {
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String preDate = PlatDateTimeUtil.formatDate(
                PlatDateTimeUtil.getNextTime(new Date(), Calendar.DATE, -6), "yyyy-MM-dd");
        request.setAttribute("preDate", preDate);
        request.setAttribute("curDate", curDate);
        return new ModelAndView("background/webstatistics/topEntrance");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月9日 下午7:17:44
     * @param request
     * @param response
     */
    @RequestMapping(params = "getTopEntranceData")
    public void getTopEntranceData(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String startDay = request.getParameter("startDay");
        String endDay = request.getParameter("endDay");
        result = dayTrendService.getTopEntranceData(startDay,endDay);
        this.printObjectJsonString(result, response);
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月17日 下午5:23:22
     * @param request
     * @return
     */
    @RequestMapping(params = "portalTimeTrend")
    public ModelAndView portalTimeTrend(HttpServletRequest request) {
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        request.setAttribute("curDate", curDate);
        return new ModelAndView("background/framework/portalIndex/portalTimeTrend");
    }
    
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月9日 下午7:16:20
     * @param request
     * @return
     */
    @RequestMapping(params = "portalTopSource")
    public ModelAndView portalTopSource(HttpServletRequest request) {
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String preDate = PlatDateTimeUtil.formatDate(
                PlatDateTimeUtil.getNextTime(new Date(), Calendar.DATE, -6), "yyyy-MM-dd");
        request.setAttribute("preDate", preDate);
        request.setAttribute("curDate", curDate);
        return new ModelAndView("background/framework/portalIndex/portalTopSource");
    }
    
    /**
     * 跳转到站内访问排行界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goTopAccess")
    public ModelAndView goTopAccess(HttpServletRequest request) {
        String defaultSiteId = siteInfoService.getDefaultSiteId();
        request.setAttribute("SITE_ID", defaultSiteId);
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String preDate = PlatDateTimeUtil.formatDate(
                PlatDateTimeUtil.getNextTime(new Date(), Calendar.DATE, -6), "yyyy-MM-dd");
        request.setAttribute("preDate", preDate);
        request.setAttribute("curDate", curDate);
        return new ModelAndView("background/webstatistics/topEntrance");
        //return PlatUICompUtil.goDesignUI("topaccessviewlist", request);
    }
}

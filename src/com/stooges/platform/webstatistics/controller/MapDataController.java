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
import com.stooges.platform.webstatistics.service.MapDataService;
import com.stooges.platform.webstatistics.service.SiteInfoService;

/**
 * 
 * 描述 地区数据业务相关Controller
 * @author 李俊
 * @version 1.0
 * @created 2017-07-27 20:42:05
 */
@Controller  
@RequestMapping("/webstatistics/MapDataController")  
public class MapDataController extends BaseController {
    /**
     * 
     */
    @Resource
    private MapDataService mapDataService;
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
     * 删除地区数据数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        mapDataService.deleteRecords("PLAT_WEBSTATISTICS_MAPDATA","MAPDATA_ID",selectColValues.split(","));
        sysLogService.saveBackLog("地区地图数据",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的地区数据", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改地区数据数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> mapData = PlatBeanUtil.getMapFromRequest(request);
        String MAPDATA_ID = (String) mapData.get("MAPDATA_ID");
        mapData = mapDataService.saveOrUpdate("PLAT_WEBSTATISTICS_MAPDATA",
                mapData,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //mapData = mapDataService.saveOrUpdateTreeData("PLAT_WEBSTATISTICS_MAPDATA",
        //        mapData,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(MAPDATA_ID)){
            sysLogService.saveBackLog("地区地图数据",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+MAPDATA_ID+"]地区数据", request);
        }else{
            MAPDATA_ID = (String) mapData.get("MAPDATA_ID");
            sysLogService.saveBackLog("地区地图数据",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+MAPDATA_ID+"]地区数据", request);
        }
        mapData.put("success", true);
        this.printObjectJsonString(mapData, response);
    }
    
    /**
     * 跳转到地区数据表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String MAPDATA_ID = request.getParameter("MAPDATA_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> mapData = null;
        if(StringUtils.isNotEmpty(MAPDATA_ID)){
            mapData = this.mapDataService.getRecord("PLAT_WEBSTATISTICS_MAPDATA"
                    ,new String[]{"MAPDATA_ID"},new Object[]{MAPDATA_ID});
        }else{
            mapData = new HashMap<String,Object>();
        }
        request.setAttribute("mapData", mapData);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月10日 下午9:14:23
     * @param request
     * @return
     */
    @RequestMapping(params = "mapdata")
    public ModelAndView mapdata(HttpServletRequest request) {
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String preDate = PlatDateTimeUtil.formatDate(
                PlatDateTimeUtil.getNextTime(new Date(), Calendar.DATE, -6), "yyyy-MM-dd");
        String defaultSiteId = siteInfoService.getDefaultSiteId();
        request.setAttribute("preDate", preDate);
        request.setAttribute("curDate", curDate);
        request.setAttribute("siteId", defaultSiteId);
        return new ModelAndView("background/webstatistics/mapdata");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月10日 下午9:15:11
     * @param request
     * @param response
     */
    @RequestMapping(params = "getMapData")
    public void getMapData(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String startDay = request.getParameter("startDay");
        String endDay = request.getParameter("endDay");
        String siteId = request.getParameter("siteId");
        result = mapDataService.getMapData(startDay,endDay,siteId);
        this.printObjectJsonString(result, response);
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2017年8月17日 下午7:08:49
     * @param request
     * @return
     */
    @RequestMapping(params = "portalMapdata")
    public ModelAndView portalMapdata(HttpServletRequest request) {
        String curDate = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String preDate = PlatDateTimeUtil.formatDate(
                PlatDateTimeUtil.getNextTime(new Date(), Calendar.DATE, -6), "yyyy-MM-dd");
        request.setAttribute("preDate", preDate);
        request.setAttribute("curDate", curDate);
        return new ModelAndView("background/framework/portalIndex/portalMapdata");
    }
}

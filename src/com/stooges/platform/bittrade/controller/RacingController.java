/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.bittrade.service.RacingService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 赛车数据业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-31 10:43:06
 */
@Controller  
@RequestMapping("/bittrade/RacingController")  
public class RacingController extends BaseController {
    /**
     * 
     */
    @Resource
    private RacingService racingService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除赛车数据数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        racingService.deleteRecords("PLAT_APPMODEL_RACING","RACING_ID",selectColValues.split(","));
        sysLogService.saveBackLog("赛车数据管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的赛车数据", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改赛车数据数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> racing = PlatBeanUtil.getMapFromRequest(request);
        String RACING_ID = (String) racing.get("RACING_ID");
        racing = racingService.saveOrUpdate("PLAT_APPMODEL_RACING",
                racing,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //racing = racingService.saveOrUpdateTreeData("PLAT_APPMODEL_RACING",
        //        racing,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(RACING_ID)){
            sysLogService.saveBackLog("赛车数据管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+RACING_ID+"]赛车数据", request);
        }else{
            RACING_ID = (String) racing.get("RACING_ID");
            sysLogService.saveBackLog("赛车数据管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+RACING_ID+"]赛车数据", request);
        }
        racing.put("success", true);
        this.printObjectJsonString(racing, response);
    }
    
    /**
     * 跳转到赛车数据表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String RACING_ID = request.getParameter("RACING_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> racing = null;
        if(StringUtils.isNotEmpty(RACING_ID)){
            racing = this.racingService.getRecord("PLAT_APPMODEL_RACING"
                    ,new String[]{"RACING_ID"},new Object[]{RACING_ID});
        }else{
            racing = new HashMap<String,Object>();
        }
        request.setAttribute("racing", racing);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 跳转到赛车数据表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goStat")
    public ModelAndView goStat(HttpServletRequest request) {
        return new ModelAndView("background/bittrade/showBrokenLine");
    }
    
    /**
     * getResultChart
     * @param request
     * @param response
     */
    @RequestMapping(params = "getResultChart")
    public void getResultChart(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> racing = PlatBeanUtil.getMapFromRequest(request);
        String BEGIN_DATE = (String)racing.get("BEGIN_DATE");
        String MONEY = (String)racing.get("MONEY");
        Map<String,Object> map = null;
        if(StringUtils.isNotEmpty(BEGIN_DATE)&&StringUtils.isNotEmpty(MONEY)){
            map =  racingService.getBetResult(BEGIN_DATE, Integer.parseInt(MONEY));
        }else{
            map =new HashMap<String,Object>();
        }
        map.put("success", true);
        this.printObjectJsonString(map, response);
    }
}

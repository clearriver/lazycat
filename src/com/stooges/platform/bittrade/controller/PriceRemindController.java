/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.controller;

import java.util.ArrayList;
import java.util.Date;
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
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.bittrade.service.PriceRemindService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 价格提醒业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-18 11:20:54
 */
@Controller  
@RequestMapping("/bittrade/PriceRemindController")  
public class PriceRemindController extends BaseController {
    /**
     * 
     */
    @Resource
    private PriceRemindService priceRemindService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除价格提醒数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        priceRemindService.deleteRecords("PLAT_BITTRADE_PRICEREMIND","PRICEREMIND_ID",selectColValues.split(","));
        sysLogService.saveBackLog("行情管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的价格提醒", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改价格提醒数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> priceRemind = PlatBeanUtil.getMapFromRequest(request);
        String PRICEREMIND_ID = (String) priceRemind.get("PRICEREMIND_ID");
        if(StringUtils.isEmpty(PRICEREMIND_ID)){
            priceRemind.put("PRICEREMIND_TIME",PlatDateTimeUtil.formatDate(new Date()
            , "yyyy-MM-dd HH:mm:ss"));
        }
        priceRemind = priceRemindService.saveOrUpdate("PLAT_BITTRADE_PRICEREMIND",
                priceRemind,AllConstants.IDGENERATOR_UUID,null);
        priceRemind.put("success", true);
        this.printObjectJsonString(priceRemind, response);
    }
    
    /**
     * 跳转到价格提醒表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String PRICEREMIND_ID = request.getParameter("PRICEREMIND_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> priceRemind = null;
        if(StringUtils.isNotEmpty(PRICEREMIND_ID)){
            priceRemind = this.priceRemindService.getRecord("PLAT_BITTRADE_PRICEREMIND"
                    ,new String[]{"PRICEREMIND_ID"},new Object[]{PRICEREMIND_ID});
        }else{
            priceRemind = new HashMap<String,Object>();
        }
        request.setAttribute("priceRemind", priceRemind);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

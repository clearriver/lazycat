/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.controller;

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
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.weixin.service.WelcomingService;

/**
 * 
 * 描述 关注欢迎语业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 15:30:22
 */
@Controller  
@RequestMapping("/weixin/WelcomingController")  
public class WelcomingController extends BaseController {
    /**
     * 
     */
    @Resource
    private WelcomingService welcomingService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除关注欢迎语数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        welcomingService.deleteRecords("PLAT_WEIXIN_WELCOMING","WELCOMING_ID",selectColValues.split(","));
        sysLogService.saveBackLog("基本配置",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的关注欢迎语", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改关注欢迎语数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> welcoming = PlatBeanUtil.getMapFromRequest(request);
        String WELCOMING_ID = (String) welcoming.get("WELCOMING_ID");
        if(StringUtils.isEmpty(WELCOMING_ID)){
            welcoming.put("WELCOMING_TIME",PlatDateTimeUtil.formatDate(new Date(), 
                    "yyyy-MM-dd HH:mm:ss"));
        }
        welcoming = welcomingService.saveOrUpdate("PLAT_WEIXIN_WELCOMING",
                welcoming,AllConstants.IDGENERATOR_UUID,null);
        welcoming.put("success", true);
        this.printObjectJsonString(welcoming, response);
    }
    
    /**
     * 跳转到关注欢迎语表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String WELCOMING_ID = request.getParameter("WELCOMING_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> welcoming = null;
        if(StringUtils.isNotEmpty(WELCOMING_ID)){
            welcoming = this.welcomingService.getRecord("PLAT_WEIXIN_WELCOMING"
                    ,new String[]{"WELCOMING_ID"},new Object[]{WELCOMING_ID});
        }else{
            welcoming = new HashMap<String,Object>();
        }
        request.setAttribute("welcoming", welcoming);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String WELCOMING_ID = request.getParameter("WELCOMING_ID");
        String WELCOMING_PARENTID = request.getParameter("WELCOMING_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> welcoming = null;
        if(StringUtils.isNotEmpty(WELCOMING_ID)){
            welcoming = this.welcomingService.getRecord("PLAT_WEIXIN_WELCOMING"
                    ,new String[]{"WELCOMING_ID"},new Object[]{WELCOMING_ID});
            WELCOMING_PARENTID = (String) welcoming.get("Welcoming_PARENTID");
        }
        Map<String,Object> parentWelcoming = null;
        if(WELCOMING_PARENTID.equals("0")){
            parentWelcoming = new HashMap<String,Object>();
            parentWelcoming.put("WELCOMING_ID",WELCOMING_PARENTID);
            parentWelcoming.put("WELCOMING_NAME","关注欢迎语树");
        }else{
            parentWelcoming = this.welcomingService.getRecord("PLAT_WEIXIN_WELCOMING",
                    new String[]{"WELCOMING_ID"}, new Object[]{WELCOMING_PARENTID});
        }
        if(welcoming==null){
            welcoming = new HashMap<String,Object>();
        }
        welcoming.put("WELCOMING_PARENTID",parentWelcoming.get("WELCOMING_ID"));
        welcoming.put("WELCOMING_PARENTNAME",parentWelcoming.get("WELCOMING_NAME"));
        request.setAttribute("welcoming", welcoming);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

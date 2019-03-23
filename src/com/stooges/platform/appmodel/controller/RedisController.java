/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.appmodel.service.RedisService;
import com.stooges.platform.common.controller.BaseController;

/**
 * 描述
 * @author 胡裕
 * @created 2017年4月4日 下午5:02:22
 */
@Controller
@RequestMapping("/redis/RedisController")
public class RedisController extends BaseController {
    /**
     * 
     */
    @Resource
    private RedisService redisService;
    
    /**
     * 设置成永久有效
     * @param request
     * @param response
     */
    @RequestMapping(params = "persist")
    public void persist(HttpServletRequest request,
            HttpServletResponse response) {
        String keys = request.getParameter("selectColValues");
        redisService.persist(keys.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }

    /**
     * 删除缓存数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String keys = request.getParameter("selectColValues");
        redisService.deleteByKeys(keys.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * @param request
     * @param response
     */
    @RequestMapping(params = "updateTimeLimit")
    public void updateTimeLimit(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> timeLimitSet = PlatBeanUtil.getMapFromRequest(request);
        String keyNames = (String) timeLimitSet.get("keyNames");
        String TIMELIMIT = (String) timeLimitSet.get("TIMELIMIT");
        String DEADLINE_TIME = (String) timeLimitSet.get("DEADLINE_TIME");
        String LIMIT_TYPE = (String) timeLimitSet.get("LIMIT_TYPE");
        if("1".equals(LIMIT_TYPE)){
            redisService.expireKeys(keyNames, Integer.parseInt(TIMELIMIT));
        }else{
            String timestamp = PlatDateTimeUtil.dateToTimeStamp(DEADLINE_TIME,"yyyy-MM-dd HH:mm:ss");
            redisService.expireAtTimeStamp(keyNames,Long.parseLong(timestamp));
        }
        timeLimitSet.put("success", true);
        this.printObjectJsonString(timeLimitSet, response);
    }
    
    /**
     * 跳转到设置有效时间界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goSetTimeLimit")
    public ModelAndView goSetTimeLimit(HttpServletRequest request) {
        String keyNames = request.getParameter("keyNames");
        String currentTime = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
        request.setAttribute("keyNames", keyNames);
        request.setAttribute("MIN_TIME",currentTime);
        return PlatUICompUtil.goDesignUI("redistimelimitform", request);
    }
    
    /**
     * 跳转到编辑界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String keyName = request.getParameter("keyName");
        Map<String,Object> keyInfo = this.redisService.getKeyInfo(keyName);
        String keyType = (String) keyInfo.get("KEY_TYPE");
        String uiCode = null;
        if(keyType.equals("string")){
            String keyValue = redisService.get(keyName);
            keyInfo.put("KEY_VALUE", keyValue);
            uiCode = "redisedit_stringform";
        }else if(keyType.equals("set")){
            uiCode = "redisedit_setform";
        }else if(keyType.equals("hash")){
            uiCode = "redisedit_form";
        }else if(keyType.equals("zset")){
            uiCode = "redisedit_zset";
        }else if(keyType.equals("list")){
            uiCode = "redisedit_setform";
        }
        request.setAttribute("keyInfo", keyInfo);
        return PlatUICompUtil.goDesignUI(uiCode, request);
    }
    
    /**
     * 更新字符串
     * @param request
     * @param response
     */
    @RequestMapping(params = "updateStr")
    public void updateStr(HttpServletRequest request,
            HttpServletResponse response) {
        String KEY_NAME = request.getParameter("KEY_NAME");
        String KEY_VALUE = request.getParameter("KEY_VALUE");
        redisService.set(KEY_NAME, KEY_VALUE);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 更新信息
     * @param request
     * @param response
     */
    @RequestMapping(params = "update")
    public void update(HttpServletRequest request,
            HttpServletResponse response) {
        String KEY_NAME = request.getParameter("KEY_NAME");
        String KEY_TYPE = request.getParameter("KEY_TYPE");
        String VALUE_JSON = request.getParameter("VALUE_JSON");
        redisService.updateNoStringValue(KEY_NAME, KEY_TYPE, VALUE_JSON);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
}

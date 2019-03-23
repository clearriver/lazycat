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
import com.stooges.platform.weixin.service.MsgEngineService;

/**
 * 
 * 描述 消息处理引擎业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 09:24:26
 */
@Controller  
@RequestMapping("/weixin/MsgEngineController")  
public class MsgEngineController extends BaseController {
    /**
     * 
     */
    @Resource
    private MsgEngineService msgEngineService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除消息处理引擎数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        msgEngineService.deleteRecords("PLAT_WEIXIN_MSGENGINE","MSGENGINE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("基本配置",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的消息处理引擎", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改消息处理引擎数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> msgEngine = PlatBeanUtil.getMapFromRequest(request);
        String MSGENGINE_ID = (String) msgEngine.get("MSGENGINE_ID");
        if(StringUtils.isEmpty(MSGENGINE_ID)){
            msgEngine.put("MSGENGINE_TIME",PlatDateTimeUtil.
                    formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        msgEngine = msgEngineService.saveOrUpdate("PLAT_WEIXIN_MSGENGINE",
                msgEngine,AllConstants.IDGENERATOR_UUID,null);
        msgEngine.put("success", true);
        this.printObjectJsonString(msgEngine, response);
    }
    
    /**
     * 跳转到消息处理引擎表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String MSGENGINE_ID = request.getParameter("MSGENGINE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> msgEngine = null;
        if(StringUtils.isNotEmpty(MSGENGINE_ID)){
            msgEngine = this.msgEngineService.getRecord("PLAT_WEIXIN_MSGENGINE"
                    ,new String[]{"MSGENGINE_ID"},new Object[]{MSGENGINE_ID});
        }else{
            msgEngine = new HashMap<String,Object>();
        }
        request.setAttribute("msgEngine", msgEngine);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

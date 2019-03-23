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
import com.stooges.platform.weixin.service.PublicService;

/**
 * 
 * 描述 公众号业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-18 15:51:54
 */
@Controller  
@RequestMapping("/weixin/PublicController")  
public class PublicController extends BaseController {
    /**
     * 
     */
    @Resource
    private PublicService publicService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除公众号数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        publicService.deleteRecords("PLAT_WEIXIN_PUBLIC","PUBLIC_ID",selectColValues.split(","));
        sysLogService.saveBackLog("基本配置",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的公众号", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改公众号数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> publicInfo = PlatBeanUtil.getMapFromRequest(request);
        String PUBLIC_ID = (String) publicInfo.get("PUBLIC_ID");
        if(StringUtils.isEmpty(PUBLIC_ID)){
            publicInfo.put("PUBLIC_CREATETIME",PlatDateTimeUtil.
                    formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        publicInfo = publicService.saveOrUpdate("PLAT_WEIXIN_PUBLIC",
                publicInfo,AllConstants.IDGENERATOR_UUID,null);
        publicInfo.put("success", true);
        this.printObjectJsonString(publicInfo, response);
    }
    
    /**
     * 跳转到公众号表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String PUBLIC_ID = request.getParameter("PUBLIC_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> publicInfo = null;
        if(StringUtils.isNotEmpty(PUBLIC_ID)){
            publicInfo = this.publicService.getRecord("PLAT_WEIXIN_PUBLIC"
                    ,new String[]{"PUBLIC_ID"},new Object[]{PUBLIC_ID});
        }else{
            publicInfo = new HashMap<String,Object>();
        }
        request.setAttribute("publicInfo", publicInfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

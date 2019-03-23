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
import com.stooges.platform.weixin.service.MediaMatterService;

/**
 * 
 * 描述 媒体素材业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-27 16:24:07
 */
@Controller  
@RequestMapping("/weixin/MediaMatterController")  
public class MediaMatterController extends BaseController {
    /**
     * 
     */
    @Resource
    private MediaMatterService mediaMatterService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除媒体素材数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        mediaMatterService.deleteMatters(selectColValues.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改媒体素材数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdateOther")
    public void saveOrUpdateOther(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> mediaMatter = PlatBeanUtil.getMapFromRequest(request);
        String MEDIAMATTER_ID = (String) mediaMatter.get("MEDIAMATTER_ID");
        if(StringUtils.isEmpty(MEDIAMATTER_ID)){
            mediaMatter.put("MEDIAMATTER_TIME",PlatDateTimeUtil.
                    formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        String mediaType = (String) mediaMatter.get("MEDIAMATTER_TYPE");
        Map<String,Object> result = mediaMatterService.saveOtherMatter(mediaMatter, mediaType);
        this.printObjectJsonString(result, response);
    }
    
    
    /**
     * 跳转到媒体素材表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String MEDIAMATTER_ID = request.getParameter("MEDIAMATTER_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> mediaMatter = null;
        if(StringUtils.isNotEmpty(MEDIAMATTER_ID)){
            mediaMatter = this.mediaMatterService.getRecord("PLAT_WEIXIN_MEDIAMATTER"
                    ,new String[]{"MEDIAMATTER_ID"},new Object[]{MEDIAMATTER_ID});
        }else{
            mediaMatter = new HashMap<String,Object>();
        }
        request.setAttribute("mediaMatter", mediaMatter);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

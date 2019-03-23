/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.controller;

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
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.weixin.service.MatterMsgService;

/**
 * 
 * 描述 素材消息群发业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-05 13:48:09
 */
@Controller  
@RequestMapping("/weixin/MatterMsgController")  
public class MatterMsgController extends BaseController {
    /**
     * 
     */
    @Resource
    private MatterMsgService matterMsgService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除素材消息群发数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        matterMsgService.deleteRecords("PLAT_WEIXIN_MATTERMSG","MATTERMSG_ID",selectColValues.split(","));
        sysLogService.saveBackLog("群发消息管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的素材消息群发", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改素材消息群发数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> matterMsg = PlatBeanUtil.getMapFromRequest(request);
        matterMsg = matterMsgService.saveMatterMsg(matterMsg);
        this.printObjectJsonString(matterMsg, response);
    }
    
    /**
     * 跳转到素材消息群发表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String MATTERMSG_ID = request.getParameter("MATTERMSG_ID");
        String publicId = request.getParameter("publicId");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> matterMsg = null;
        if(StringUtils.isNotEmpty(MATTERMSG_ID)){
            matterMsg = this.matterMsgService.getRecord("PLAT_WEIXIN_MATTERMSG"
                    ,new String[]{"MATTERMSG_ID"},new Object[]{MATTERMSG_ID});
        }else{
            matterMsg = new HashMap<String,Object>();
            matterMsg.put("MATTERMSG_PUBID", publicId);
        }
        request.setAttribute("matterMsg", matterMsg);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

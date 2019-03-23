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
import com.stooges.core.util.PlatStringUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.weixin.service.UserService;

/**
 * 
 * 描述 微信用户业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 16:30:21
 */
@Controller  
@RequestMapping("/weixin/UserController")  
public class WeixinUserController extends BaseController {
    /**
     * 
     */
    @Resource(name="weixinUserService")
    private UserService userService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除微信用户数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String userIds = request.getParameter("selectColValues");
        String publicId = request.getParameter("PUBLIC_ID");
        userService.updateUserStatus(publicId, userIds,UserService.STATUS_BLACK);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 删除微信用户数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "removeblack")
    public void removeblack(HttpServletRequest request,
            HttpServletResponse response) {
        String userIds = request.getParameter("selectColValues");
        userService.updateUserStatus(null, userIds,UserService.STATUS_NORMAL);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改微信用户数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> user = PlatBeanUtil.getMapFromRequest(request);
        String USER_ID = (String) user.get("USER_ID");
        String REMARK = (String) user.get("REMARK");
        String USER_TAGIDS = request.getParameter("USER_TAGIDS");
        user = userService.saveOrUpdate("PLAT_WEIXIN_USER",
                user,AllConstants.IDGENERATOR_UUID,null);
        userService.updateUserRemark(USER_ID, REMARK);
        userService.updateUserTags(USER_ID, USER_TAGIDS);
        user.put("success", true);
        this.printObjectJsonString(user, response);
    }
    
    /**
     * 跳转到微信用户表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String USER_ID = request.getParameter("USER_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> user = null;
        if(StringUtils.isNotEmpty(USER_ID)){
            user = this.userService.getRecord("PLAT_WEIXIN_USER"
                    ,new String[]{"USER_ID"},new Object[]{USER_ID});
            String sex = user.get("SEX").toString();
            if(sex.equals("1")){
                user.put("SEX", "男");
            }else{
                user.put("SEX", "女");
            }
            List<String> userTagIds = userService.getUserTagIds(USER_ID);
            user.put("USER_TAGIDS",PlatStringUtil.getListStringSplit(userTagIds));
        }else{
            user = new HashMap<String,Object>();
        }
        request.setAttribute("user", user);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 同步用户数据到微信
     * @param request
     * @param response
     */
    @RequestMapping(params = "refresh")
    public void refresh(HttpServletRequest request,
            HttpServletResponse response) {
        String PUBLIC_ID = request.getParameter("PUBLIC_ID");
        userService.refreshWeixinUserAndTag(PUBLIC_ID);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 跳转到系统角色选择界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goUserGrant")
    public ModelAndView goUserGrant(HttpServletRequest request) {
        String MATTERMSG_ID = request.getParameter("MATTERMSG_ID");
        String publicId = request.getParameter("publicId");
        StringBuffer selectedRecordIds = new StringBuffer("");
        if(StringUtils.isNotEmpty(MATTERMSG_ID)){
            Map<String,Object> matterMsg = userService.getRecord("PLAT_WEIXIN_MATTERMSG",
                    new String[]{"MATTERMSG_ID"}, new Object[]{MATTERMSG_ID});
            String MATTERMSG_REIDS = (String) matterMsg.get("MATTERMSG_REIDS");
            selectedRecordIds.append(MATTERMSG_REIDS);
        }
        request.setAttribute("publicId", publicId);
        request.setAttribute("selectedRecordIds", selectedRecordIds.toString());
        return PlatUICompUtil.goDesignUI("weixinuserselector", request);
    }
}

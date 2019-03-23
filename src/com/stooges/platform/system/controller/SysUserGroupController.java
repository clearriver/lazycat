/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.system.service.SysUserGroupService;
import com.stooges.platform.system.service.SysUserService;

/**
 * 
 * 描述 用户组管理业务相关Controller
 * @author 李俊
 * @version 1.0
 * @created 2018-05-10 22:05:56
 */
@Controller  
@RequestMapping("/system/SysUserGroupController")  
public class SysUserGroupController extends BaseController {
    /**
     * 
     */
    @Resource
    private SysUserGroupService sysUserGroupService;
    /**
     * 
     */
    @Resource
    private SysUserService sysUserService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    
    /**
     * 跳转到用户组管理表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String USERGROUP_ID = request.getParameter("USERGROUP_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        String companyId = request.getParameter("companyId");
        Map<String,Object> userGroup = null;
        if(StringUtils.isNotEmpty(USERGROUP_ID)){
            userGroup = this.sysUserGroupService.getRecord("PLAT_SYSTEM_USERGROUP"
                    ,new String[]{"USERGROUP_ID"},new Object[]{USERGROUP_ID});
        }else{
            userGroup = new HashMap<String,Object>();
            userGroup.put("USERGROUP_COMPANYID", companyId);
        }
        request.setAttribute("userGroup", userGroup);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        
    }
    /**
     * 
     * @param request
     * @return
     */
    @RequestMapping(params = "goUserGrant")
    public ModelAndView goUserGrant(HttpServletRequest request) {
        String USERGROUP_ID = request.getParameter("USERGROUP_ID");
        List<String> userIds = sysUserService.findGroupUserIds(USERGROUP_ID);
        StringBuffer selectedRecordIds = new StringBuffer("");
        for(int i=0;i<userIds.size();i++){
            if(i>0){
                selectedRecordIds.append(",");
            }
            selectedRecordIds.append(userIds.get(i));
        }
        request.setAttribute("selectedRecordIds", selectedRecordIds.toString());
        return PlatUICompUtil.goDesignUI("userselector", request);
    }
    /**
     * 
     * @param request
     * @param response
     */
    @RequestMapping(params = "grantUsers")
    public void grantUsers(HttpServletRequest request,
            HttpServletResponse response) {
        String USERGROUP_ID = request.getParameter("USERGROUP_ID");
        String checkUserIds = request.getParameter("checkUserIds");
        sysUserGroupService.saveUsers(USERGROUP_ID, checkUserIds);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
}

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

import jodd.util.StringUtil;

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
import com.stooges.platform.weixin.service.MenuGroupService;
import com.stooges.platform.weixin.service.PublicService;

/**
 * 
 * 描述 菜单组业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-21 10:04:25
 */
@Controller  
@RequestMapping("/weixin/MenuGroupController")  
public class MenuGroupController extends BaseController {
    /**
     * 
     */
    @Resource
    private MenuGroupService menuGroupService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private PublicService publicService;
    
    /**
     * 删除菜单组数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("groupId");
        //删除配置的菜单
        menuGroupService.deleteRecords("PLAT_WEIXIN_MENU","MENUGROUP_ID", selectColValues.split(","));
        menuGroupService.deleteRecords("PLAT_WEIXIN_MENUGROUP","MENUGROUP_ID",selectColValues.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改菜单组数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> menuGroup = PlatBeanUtil.getMapFromRequest(request);
        String MENUGROUP_ID = request.getParameter("MENUGROUP_ID");
        if(StringUtil.isEmpty(MENUGROUP_ID)){
            String MENUGROUP_PUBID = (String) menuGroup.get("MENUGROUP_PUBID");
            int count = menuGroupService.getGroupCount(MENUGROUP_PUBID);
            if(count>=3){
                menuGroup.put("success", false);
                menuGroup.put("msg", "最大允许创建3个菜单组");
                this.printObjectJsonString(menuGroup, response);
                return;
            }
        }
        menuGroup = menuGroupService.saveOrUpdate("PLAT_WEIXIN_MENUGROUP",
                menuGroup,AllConstants.IDGENERATOR_UUID,null);
        menuGroup.put("success", true);
        this.printObjectJsonString(menuGroup, response);
    }
    
    /**
     * 跳转到菜单组表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String MENUGROUP_ID = request.getParameter("MENUGROUP_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> menuGroup = null;
        if(StringUtils.isNotEmpty(MENUGROUP_ID)){
            menuGroup = this.menuGroupService.getRecord("PLAT_WEIXIN_MENUGROUP"
                    ,new String[]{"MENUGROUP_ID"},new Object[]{MENUGROUP_ID});
        }else{
            menuGroup = new HashMap<String,Object>();
        }
        request.setAttribute("menuGroup", menuGroup);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 跳转到列表界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goList")
    public ModelAndView goList(HttpServletRequest request) {
        String PUBLIC_ID = publicService.firstPublicId();
        request.setAttribute("PUBLIC_ID", PUBLIC_ID);
        return PlatUICompUtil.goDesignUI("menumanager", request);
    }
}

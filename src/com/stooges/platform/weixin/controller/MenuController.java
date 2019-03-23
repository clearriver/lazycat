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
import com.stooges.platform.weixin.service.MenuService;

/**
 * 
 * 描述 菜单业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-21 10:04:25
 */
@Controller  
@RequestMapping("/weixin/MenuController")  
public class MenuController extends BaseController {
    /**
     * 
     */
    @Resource
    private MenuService menuService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除菜单数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        menuService.deleteRecords("PLAT_WEIXIN_MENU","MENU_ID",selectColValues.split(","));
        sysLogService.saveBackLog("基本配置",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的菜单", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改菜单数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> menu = PlatBeanUtil.getMapFromRequest(request);
        String MENU_ID = (String) menu.get("MENU_ID");
        String MENUGROUP_ID = (String) menu.get("MENUGROUP_ID");
        if(StringUtils.isEmpty(MENU_ID)){
            int count = this.menuService.getCountByGroupId(MENUGROUP_ID);
            if(count>=5){
                menu.put("success", false);
                menu.put("msg", "一个菜单组最多配置5个菜单!");
                this.printObjectJsonString(menu, response);
                return;
            }
        }
        menu = menuService.saveOrUpdate("PLAT_WEIXIN_MENU",
                menu,AllConstants.IDGENERATOR_UUID,null);
        menu.put("success", true);
        this.printObjectJsonString(menu, response);
    }
    
    /**
     * 跳转到菜单表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String MENU_ID = request.getParameter("MENU_ID");
        String PUBLIC_ID = request.getParameter("PUBLIC_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> menu = null;
        if(StringUtils.isNotEmpty(MENU_ID)){
            menu = this.menuService.getRecord("PLAT_WEIXIN_MENU"
                    ,new String[]{"MENU_ID"},new Object[]{MENU_ID});
        }else{
            menu = new HashMap<String,Object>();
            menu.put("MENU_PUBID", PUBLIC_ID);
        }
        request.setAttribute("menu", menu);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 同步菜单数据到微信公众号
     * @param request
     * @param response
     */
    @RequestMapping(params = "refresh")
    public void refresh(HttpServletRequest request,
            HttpServletResponse response) {
        String PUBLIC_ID = request.getParameter("PUBLIC_ID");
        menuService.updateAllMenuToWeixin(PUBLIC_ID);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.wsbs.service.SermaterService;

/**
 * 
 * 描述 材料表业务相关Controller
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-15 11:26:44
 */
@Controller  
@RequestMapping("/wsbs/SermaterController")  
public class SermaterController extends BaseController {
    /**
     * 
     */
    @Resource
    private SermaterService sermaterService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除材料表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        sermaterService.deleteRecords("PLAT_WSBS_SERMATER","SERMATER_ID",selectColValues.split(","));
        sysLogService.saveBackLog("服务事项管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的材料表", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改材料表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> sermater = PlatBeanUtil.getMapFromRequest(request);
        String SERMATER_ID = (String)sermater.get("SERMATER_ID");
        String SERITEM_ID = (String)sermater.get("SERITEM_ID");
        if(StringUtils.isEmpty(SERMATER_ID)){
            int SERMATER_SN = sermaterService.getMaxSn(SERITEM_ID);
            sermater.put("SERMATER_SN", SERMATER_SN);
        }
        sermaterService.saveCascadeFileAttach(sermater);
        if(StringUtils.isNotEmpty(SERMATER_ID)){
            sysLogService.saveBackLog("服务事项管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+SERMATER_ID+"]材料表", request);
        }else{
            SERMATER_ID = (String) sermater.get("SERMATER_ID");
            sysLogService.saveBackLog("服务事项管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+SERMATER_ID+"]材料表", request);
        }
        sermater.put("success", true);
        this.printObjectJsonString(sermater, response);
    }
    
    /**
     * 跳转到材料表表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String SERMATER_ID = request.getParameter("SERMATER_ID");
        String SERITEM_ID = request.getParameter("SERITEM_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> sermater = null;
        if(StringUtils.isNotEmpty(SERMATER_ID)){
            sermater = this.sermaterService.getRecord("PLAT_WSBS_SERMATER"
                    ,new String[]{"SERMATER_ID"},new Object[]{SERMATER_ID});
        }else{
            sermater = new HashMap<String,Object>();
            sermater.put("SERITEM_ID", SERITEM_ID);
        }
        request.setAttribute("sermater", sermater);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 更新排序值
     * @param request
     * @param response
     */
    @RequestMapping(params = "updateSn")
    public void updateSn(HttpServletRequest request,
            HttpServletResponse response) {
        String materIds = request.getParameter("materIds");
        sermaterService.updateSn(materIds.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 更新材料是否为必须提供
     * 
     * @param request 传入参数
     */
    @RequestMapping(params = "updateIsneed")
    public void updateIsneed(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        String isneed = request.getParameter("isneed");
        sermaterService.updateIsneed(isneed,selectColValues);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
}

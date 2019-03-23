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
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.wsbs.service.SeritemService;

/**
 * 
 * 描述 信息表业务相关Controller
 * @author Lina Lin
 * @version 1.0
 * @created 2017-05-15 11:26:44
 */
@Controller  
@RequestMapping("/wsbs/SeritemController")  
public class SeritemController extends BaseController {
    /**
     * 
     */
    @Resource
    private SeritemService seritemService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        seritemService.deleteRecords("PLAT_WSBS_SERITEM","SERITEM_ID",selectColValues.split(","));
        //删除关联的材料信息
        seritemService.deleteRecords("PLAT_WSBS_SERMATER","SERITEM_ID",selectColValues.split(","));
        sysLogService.saveBackLog("服务事项管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的信息表", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> seritem = PlatBeanUtil.getMapFromRequest(request);
        String SERITEM_ID = (String) seritem.get("SERITEM_ID");
        seritem = seritemService.saveOrUpdate("PLAT_WSBS_SERITEM",
                seritem,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(SERITEM_ID)){
            sysLogService.saveBackLog("服务事项管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+SERITEM_ID+"]信息表", request);
        }else{
            SERITEM_ID = (String) seritem.get("SERITEM_ID");
            sysLogService.saveBackLog("服务事项管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+SERITEM_ID+"]信息表", request);
        }
        seritem.put("success", true);
        this.printObjectJsonString(seritem, response);
    }
    
    /**
     * 跳转到信息表表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String SERITEM_ID = request.getParameter("SERITEM_ID");
        String SERCATALOG_ID = request.getParameter("SERCATALOG_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> seritem = null;
        if(StringUtils.isNotEmpty(SERITEM_ID)){
            seritem = this.seritemService.getRecord("PLAT_WSBS_SERITEM"
                    ,new String[]{"SERITEM_ID"},new Object[]{SERITEM_ID});
            if(seritem != null){
                SERCATALOG_ID = (String)seritem.get("SERCATALOG_ID");
            }
        }else{
            seritem = new HashMap<String,Object>();
            seritem.put("SERCATALOG_ID", SERCATALOG_ID);
        }
        Map<String,Object> sercatalog = this.seritemService.getRecord("PLAT_WSBS_SERCATALOG"
                ,new String[]{"SERCATALOG_ID"},new Object[]{SERCATALOG_ID});
        if(sercatalog != null){
            seritem.put("SERCATALOG_NAME", sercatalog.get("SERCATALOG_NAME"));
        }
        request.setAttribute("seritem", seritem);
        
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 获取树形的服务事项类别和服务事项数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "typeAndItems")
    public void typeAndItems(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> params= PlatBeanUtil.getMapFromRequest(request);
        String typeDefJson = seritemService.getTypeAndItemsJson(params);
        this.printJsonString(typeDefJson, response);
    }
}

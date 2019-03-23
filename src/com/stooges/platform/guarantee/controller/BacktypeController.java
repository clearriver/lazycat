/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.guarantee.controller;

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
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.guarantee.service.BacktypeService;

/**
 * 
 * 描述 反担保类型业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-03-07 22:09:25
 */
@Controller  
@RequestMapping("/guarantee/BacktypeController")  
public class BacktypeController extends BaseController {
    /**
     * 
     */
    @Resource
    private BacktypeService backtypeService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除反担保类型数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        backtypeService.deleteRecords("PLAT_GUARANTEE_BACKTYPE","BACKTYPE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("反担保类型设置",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的反担保类型", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改反担保类型数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> backtype = PlatBeanUtil.getMapFromRequest(request);
        String BACKTYPE_ID = (String) backtype.get("BACKTYPE_ID");
        backtype = backtypeService.saveOrUpdate("PLAT_GUARANTEE_BACKTYPE",
                backtype,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //backtype = backtypeService.saveOrUpdateTreeData("PLAT_GUARANTEE_BACKTYPE",
        //        backtype,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(BACKTYPE_ID)){
            sysLogService.saveBackLog("反担保类型设置",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+BACKTYPE_ID+"]反担保类型", request);
        }else{
            BACKTYPE_ID = (String) backtype.get("BACKTYPE_ID");
            sysLogService.saveBackLog("反担保类型设置",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+BACKTYPE_ID+"]反担保类型", request);
        }
        backtype.put("success", true);
        this.printObjectJsonString(backtype, response);
    }
    
    /**
     * 跳转到反担保类型表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String BACKTYPE_ID = request.getParameter("BACKTYPE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> backtype = null;
        if(StringUtils.isNotEmpty(BACKTYPE_ID)){
            backtype = this.backtypeService.getRecord("PLAT_GUARANTEE_BACKTYPE"
                    ,new String[]{"BACKTYPE_ID"},new Object[]{BACKTYPE_ID});
        }else{
            backtype = new HashMap<String,Object>();
        }
        request.setAttribute("backtype", backtype);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String BACKTYPE_ID = request.getParameter("BACKTYPE_ID");
        String BACKTYPE_PARENTID = request.getParameter("BACKTYPE_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> backtype = null;
        if(StringUtils.isNotEmpty(BACKTYPE_ID)){
            backtype = this.backtypeService.getRecord("PLAT_GUARANTEE_BACKTYPE"
                    ,new String[]{"BACKTYPE_ID"},new Object[]{BACKTYPE_ID});
            BACKTYPE_PARENTID = (String) backtype.get("Backtype_PARENTID");
        }
        Map<String,Object> parentBacktype = null;
        if(BACKTYPE_PARENTID.equals("0")){
            parentBacktype = new HashMap<String,Object>();
            parentBacktype.put("BACKTYPE_ID",BACKTYPE_PARENTID);
            parentBacktype.put("BACKTYPE_NAME","反担保类型树");
        }else{
            parentBacktype = this.backtypeService.getRecord("PLAT_GUARANTEE_BACKTYPE",
                    new String[]{"BACKTYPE_ID"}, new Object[]{BACKTYPE_PARENTID});
        }
        if(backtype==null){
            backtype = new HashMap<String,Object>();
        }
        backtype.put("BACKTYPE_PARENTID",parentBacktype.get("BACKTYPE_ID"));
        backtype.put("BACKTYPE_PARENTNAME",parentBacktype.get("BACKTYPE_NAME"));
        request.setAttribute("backtype", backtype);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

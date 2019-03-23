/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.controller;

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
import com.stooges.platform.iguarantee.service.ImmovablesService;

/**
 * 
 * 描述 不动产抵押业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-03-14 12:21:47
 */
@Controller  
@RequestMapping("/iguarantee/ImmovablesController")  
public class ImmovablesController extends BaseController {
    /**
     * 
     */
    @Resource
    private ImmovablesService immovablesService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除不动产抵押数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        immovablesService.deleteRecords("PLAT_IGUARANTEE_IMMOVABLES","IMMOVABLES_ID",selectColValues.split(","));
        sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的不动产抵押", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改不动产抵押数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> immovables = PlatBeanUtil.getMapFromRequest(request);
        String IMMOVABLES_ID = (String) immovables.get("IMMOVABLES_ID");
        immovables = immovablesService.saveOrUpdate("PLAT_IGUARANTEE_IMMOVABLES",
                immovables,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //immovables = immovablesService.saveOrUpdateTreeData("PLAT_IGUARANTEE_IMMOVABLES",
        //        immovables,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(IMMOVABLES_ID)){
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+IMMOVABLES_ID+"]不动产抵押", request);
        }else{
            IMMOVABLES_ID = (String) immovables.get("IMMOVABLES_ID");
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+IMMOVABLES_ID+"]不动产抵押", request);
        }
        immovables.put("success", true);
        this.printObjectJsonString(immovables, response);
    }
    
    /**
     * 跳转到不动产抵押表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String IMMOVABLES_ID = request.getParameter("IMMOVABLES_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> immovables = null;
        if(StringUtils.isNotEmpty(IMMOVABLES_ID)){
            immovables = this.immovablesService.getRecord("PLAT_IGUARANTEE_IMMOVABLES"
                    ,new String[]{"IMMOVABLES_ID"},new Object[]{IMMOVABLES_ID});
        }else{
            immovables = new HashMap<String,Object>();
        }
        request.setAttribute("immovables", immovables);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String IMMOVABLES_ID = request.getParameter("IMMOVABLES_ID");
        String IMMOVABLES_PARENTID = request.getParameter("IMMOVABLES_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> immovables = null;
        if(StringUtils.isNotEmpty(IMMOVABLES_ID)){
            immovables = this.immovablesService.getRecord("PLAT_IGUARANTEE_IMMOVABLES"
                    ,new String[]{"IMMOVABLES_ID"},new Object[]{IMMOVABLES_ID});
            IMMOVABLES_PARENTID = (String) immovables.get("Immovables_PARENTID");
        }
        Map<String,Object> parentImmovables = null;
        if(IMMOVABLES_PARENTID.equals("0")){
            parentImmovables = new HashMap<String,Object>();
            parentImmovables.put("IMMOVABLES_ID",IMMOVABLES_PARENTID);
            parentImmovables.put("IMMOVABLES_NAME","不动产抵押树");
        }else{
            parentImmovables = this.immovablesService.getRecord("PLAT_IGUARANTEE_IMMOVABLES",
                    new String[]{"IMMOVABLES_ID"}, new Object[]{IMMOVABLES_PARENTID});
        }
        if(immovables==null){
            immovables = new HashMap<String,Object>();
        }
        immovables.put("IMMOVABLES_PARENTID",parentImmovables.get("IMMOVABLES_ID"));
        immovables.put("IMMOVABLES_PARENTNAME",parentImmovables.get("IMMOVABLES_NAME"));
        request.setAttribute("immovables", immovables);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

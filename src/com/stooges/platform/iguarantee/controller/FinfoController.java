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
import com.stooges.platform.iguarantee.service.FinfoService;

/**
 * 
 * 描述 基本情况业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-03-14 12:42:17
 */
@Controller  
@RequestMapping("/iguarantee/FinfoController")  
public class FinfoController extends BaseController {
    /**
     * 
     */
    @Resource
    private FinfoService finfoService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除基本情况数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        finfoService.deleteRecords("PLAT_IGUARANTEE_FINFO","GUARANTEE_FID",selectColValues.split(","));
        sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的基本情况", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改基本情况数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> finfo = PlatBeanUtil.getMapFromRequest(request);
        String GUARANTEE_FID = (String) finfo.get("GUARANTEE_FID");
        finfo = finfoService.saveOrUpdate("PLAT_IGUARANTEE_FINFO",
                finfo,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //finfo = finfoService.saveOrUpdateTreeData("PLAT_IGUARANTEE_FINFO",
        //        finfo,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(GUARANTEE_FID)){
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+GUARANTEE_FID+"]基本情况", request);
        }else{
            GUARANTEE_FID = (String) finfo.get("GUARANTEE_FID");
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+GUARANTEE_FID+"]基本情况", request);
        }
        finfo.put("success", true);
        this.printObjectJsonString(finfo, response);
    }
    
    /**
     * 跳转到基本情况表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String GUARANTEE_FID = request.getParameter("GUARANTEE_FID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> finfo = null;
        if(StringUtils.isNotEmpty(GUARANTEE_FID)){
            finfo = this.finfoService.getRecord("PLAT_IGUARANTEE_FINFO"
                    ,new String[]{"GUARANTEE_FID"},new Object[]{GUARANTEE_FID});
        }else{
            finfo = new HashMap<String,Object>();
        }
        request.setAttribute("finfo", finfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String GUARANTEE_FID = request.getParameter("GUARANTEE_FID");
        String FINFO_PARENTID = request.getParameter("FINFO_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> finfo = null;
        if(StringUtils.isNotEmpty(GUARANTEE_FID)){
            finfo = this.finfoService.getRecord("PLAT_IGUARANTEE_FINFO"
                    ,new String[]{"GUARANTEE_FID"},new Object[]{GUARANTEE_FID});
            FINFO_PARENTID = (String) finfo.get("Finfo_PARENTID");
        }
        Map<String,Object> parentFinfo = null;
        if(FINFO_PARENTID.equals("0")){
            parentFinfo = new HashMap<String,Object>();
            parentFinfo.put("GUARANTEE_FID",FINFO_PARENTID);
            parentFinfo.put("FINFO_NAME","基本情况树");
        }else{
            parentFinfo = this.finfoService.getRecord("PLAT_IGUARANTEE_FINFO",
                    new String[]{"GUARANTEE_FID"}, new Object[]{FINFO_PARENTID});
        }
        if(finfo==null){
            finfo = new HashMap<String,Object>();
        }
        finfo.put("FINFO_PARENTID",parentFinfo.get("GUARANTEE_FID"));
        finfo.put("FINFO_PARENTNAME",parentFinfo.get("FINFO_NAME"));
        request.setAttribute("finfo", finfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

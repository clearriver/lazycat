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
import com.stooges.platform.iguarantee.service.LiabilitiesService;

/**
 * 
 * 描述 负债情况业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-03-14 11:31:06
 */
@Controller  
@RequestMapping("/iguarantee/LiabilitiesController")  
public class LiabilitiesController extends BaseController {
    /**
     * 
     */
    @Resource
    private LiabilitiesService liabilitiesService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除负债情况数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        liabilitiesService.deleteRecords("PLAT_IGUARANTEE_LIABILITIES","LIABILITIES_ID",selectColValues.split(","));
        sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的负债情况", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改负债情况数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> liabilities = PlatBeanUtil.getMapFromRequest(request);
        String LIABILITIES_ID = (String) liabilities.get("LIABILITIES_ID");
        liabilities = liabilitiesService.saveOrUpdate("PLAT_IGUARANTEE_LIABILITIES",
                liabilities,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //liabilities = liabilitiesService.saveOrUpdateTreeData("PLAT_IGUARANTEE_LIABILITIES",
        //        liabilities,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(LIABILITIES_ID)){
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+LIABILITIES_ID+"]负债情况", request);
        }else{
            LIABILITIES_ID = (String) liabilities.get("LIABILITIES_ID");
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+LIABILITIES_ID+"]负债情况", request);
        }
        liabilities.put("success", true);
        this.printObjectJsonString(liabilities, response);
    }
    
    /**
     * 跳转到负债情况表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String LIABILITIES_ID = request.getParameter("LIABILITIES_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> liabilities = null;
        if(StringUtils.isNotEmpty(LIABILITIES_ID)){
            liabilities = this.liabilitiesService.getRecord("PLAT_IGUARANTEE_LIABILITIES"
                    ,new String[]{"LIABILITIES_ID"},new Object[]{LIABILITIES_ID});
        }else{
            liabilities = new HashMap<String,Object>();
        }
        request.setAttribute("liabilities", liabilities);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String LIABILITIES_ID = request.getParameter("LIABILITIES_ID");
        String LIABILITIES_PARENTID = request.getParameter("LIABILITIES_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> liabilities = null;
        if(StringUtils.isNotEmpty(LIABILITIES_ID)){
            liabilities = this.liabilitiesService.getRecord("PLAT_IGUARANTEE_LIABILITIES"
                    ,new String[]{"LIABILITIES_ID"},new Object[]{LIABILITIES_ID});
            LIABILITIES_PARENTID = (String) liabilities.get("Liabilities_PARENTID");
        }
        Map<String,Object> parentLiabilities = null;
        if(LIABILITIES_PARENTID.equals("0")){
            parentLiabilities = new HashMap<String,Object>();
            parentLiabilities.put("LIABILITIES_ID",LIABILITIES_PARENTID);
            parentLiabilities.put("LIABILITIES_NAME","负债情况树");
        }else{
            parentLiabilities = this.liabilitiesService.getRecord("PLAT_IGUARANTEE_LIABILITIES",
                    new String[]{"LIABILITIES_ID"}, new Object[]{LIABILITIES_PARENTID});
        }
        if(liabilities==null){
            liabilities = new HashMap<String,Object>();
        }
        liabilities.put("LIABILITIES_PARENTID",parentLiabilities.get("LIABILITIES_ID"));
        liabilities.put("LIABILITIES_PARENTNAME",parentLiabilities.get("LIABILITIES_NAME"));
        request.setAttribute("liabilities", liabilities);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

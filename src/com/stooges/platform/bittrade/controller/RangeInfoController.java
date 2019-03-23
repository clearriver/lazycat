/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.controller;

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
import com.stooges.platform.bittrade.service.RangeInfoService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 日涨幅信息业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-14 16:16:54
 */
@Controller  
@RequestMapping("/bittrade/RangeInfoController")  
public class RangeInfoController extends BaseController {
    /**
     * 
     */
    @Resource
    private RangeInfoService rangeInfoService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除日涨幅信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        rangeInfoService.deleteRecords("PLAT_BITTRADE_RANGEINFO","RANGEINFO_ID",selectColValues.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改日涨幅信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> rangeInfo = PlatBeanUtil.getMapFromRequest(request);
        String RANGEINFO_ID = (String) rangeInfo.get("RANGEINFO_ID");
        rangeInfo = rangeInfoService.saveOrUpdate("PLAT_BITTRADE_RANGEINFO",
                rangeInfo,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //rangeInfo = rangeInfoService.saveOrUpdateTreeData("PLAT_BITTRADE_RANGEINFO",
        //        rangeInfo,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(RANGEINFO_ID)){
            sysLogService.saveBackLog("行情管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+RANGEINFO_ID+"]日涨幅信息", request);
        }else{
            RANGEINFO_ID = (String) rangeInfo.get("RANGEINFO_ID");
            sysLogService.saveBackLog("行情管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+RANGEINFO_ID+"]日涨幅信息", request);
        }
        rangeInfo.put("success", true);
        this.printObjectJsonString(rangeInfo, response);
    }
    
    /**
     * 跳转到日涨幅信息表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String RANGEINFO_ID = request.getParameter("RANGEINFO_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> rangeInfo = null;
        if(StringUtils.isNotEmpty(RANGEINFO_ID)){
            rangeInfo = this.rangeInfoService.getRecord("PLAT_BITTRADE_RANGEINFO"
                    ,new String[]{"RANGEINFO_ID"},new Object[]{RANGEINFO_ID});
        }else{
            rangeInfo = new HashMap<String,Object>();
        }
        request.setAttribute("rangeInfo", rangeInfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String RANGEINFO_ID = request.getParameter("RANGEINFO_ID");
        String RANGEINFO_PARENTID = request.getParameter("RANGEINFO_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> rangeInfo = null;
        if(StringUtils.isNotEmpty(RANGEINFO_ID)){
            rangeInfo = this.rangeInfoService.getRecord("PLAT_BITTRADE_RANGEINFO"
                    ,new String[]{"RANGEINFO_ID"},new Object[]{RANGEINFO_ID});
            RANGEINFO_PARENTID = (String) rangeInfo.get("RangeInfo_PARENTID");
        }
        Map<String,Object> parentRangeInfo = null;
        if(RANGEINFO_PARENTID.equals("0")){
            parentRangeInfo = new HashMap<String,Object>();
            parentRangeInfo.put("RANGEINFO_ID",RANGEINFO_PARENTID);
            parentRangeInfo.put("RANGEINFO_NAME","日涨幅信息树");
        }else{
            parentRangeInfo = this.rangeInfoService.getRecord("PLAT_BITTRADE_RANGEINFO",
                    new String[]{"RANGEINFO_ID"}, new Object[]{RANGEINFO_PARENTID});
        }
        if(rangeInfo==null){
            rangeInfo = new HashMap<String,Object>();
        }
        rangeInfo.put("RANGEINFO_PARENTID",parentRangeInfo.get("RANGEINFO_ID"));
        rangeInfo.put("RANGEINFO_PARENTNAME",parentRangeInfo.get("RANGEINFO_NAME"));
        request.setAttribute("rangeInfo", rangeInfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

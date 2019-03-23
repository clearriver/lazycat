/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

import java.util.ArrayList;
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
import com.stooges.platform.appmodel.service.DbConnService;

/**
 * 
 * 描述 数据源信息业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2018-03-30 15:06:38
 */
@Controller  
@RequestMapping("/appmodel/DbConnController")  
public class DbConnController extends BaseController {
    /**
     * 
     */
    @Resource
    private DbConnService dbConnService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除数据源信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        dbConnService.deleteRecords("PLAT_APPMODEL_DBCONN","DBCONN_ID",selectColValues.split(","));
        sysLogService.saveBackLog("元数据管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的数据源信息", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改数据源信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> dbConn = PlatBeanUtil.getMapFromRequest(request);
        String DBCONN_ID = (String) dbConn.get("DBCONN_ID");
        if(StringUtils.isEmpty(DBCONN_ID)){
            dbConn.put("DBCONN_TIME", PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        dbConn = dbConnService.saveOrUpdate("PLAT_APPMODEL_DBCONN",
                dbConn,AllConstants.IDGENERATOR_UUID,null);
        dbConn.put("success", true);
        this.printObjectJsonString(dbConn, response);
    }
    
    /**
     * 跳转到数据源信息表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String DBCONN_ID = request.getParameter("DBCONN_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> dbConn = null;
        if(StringUtils.isNotEmpty(DBCONN_ID)){
            dbConn = this.dbConnService.getRecord("PLAT_APPMODEL_DBCONN"
                    ,new String[]{"DBCONN_ID"},new Object[]{DBCONN_ID});
        }else{
            dbConn = new HashMap<String,Object>();
        }
        request.setAttribute("dbConn", dbConn);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 验证数据源有效性
     * @param request
     * @param response
     */
    @RequestMapping(params = "isValid")
    public void isValid(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> dbConn = PlatBeanUtil.getMapFromRequest(request);
        boolean isValidDb = dbConnService.isValidDb(dbConn);
        dbConn.put("success", isValidDb);
        this.printObjectJsonString(dbConn, response);
    }
}

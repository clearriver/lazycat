/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.metadata.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.AllConstants;
import com.stooges.core.util.BrowserUtils;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.metadata.service.DataResService;
import com.stooges.platform.metadata.service.DataSerService;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 数据资源信息业务相关Controller
 * 
 * @author 胡裕
 * @version 1.0
 * @created 2018-05-07 17:35:52
 */
@Controller
@RequestMapping("/metadata/DataResController")
public class DataResController extends BaseController {
    /**
     * 
     */
    @Resource
    private DataResService dataResService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;

    /**
     * 删除数据资源信息数据
     * 
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        dataResService.deleteCascade(selectColValues);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }

    /**
     * 新增或者修改数据资源信息数据
     * 
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String, Object> dataRes = PlatBeanUtil.getMapFromRequest(request);
        
        String DATARES_ID = (String) dataRes.get("DATARES_ID");
        dataRes = dataResService.saveOrUpdate("PLAT_METADATA_DATARES", dataRes,
                AllConstants.IDGENERATOR_UUID, null);
        dataRes.put("success", true);
        this.printObjectJsonString(dataRes, response);
    }

    /**
     * 跳转到数据资源信息表单界面
     * 
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String DATARES_ID = request.getParameter("DATARES_ID");
        // 获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String, Object> dataRes = null;
        if (StringUtils.isNotEmpty(DATARES_ID)) {
            dataRes = this.dataResService.getRecord("PLAT_METADATA_DATARES",
                    new String[] { "DATARES_ID" }, new Object[] { DATARES_ID });
            dataRes.putAll(dataResService.getCatalogInfo(DATARES_ID));
        } else {
            dataRes = new HashMap<String, Object>();
        }
        request.setAttribute("dataRes", dataRes);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }

    /**
     * 保存资源基本信息
     * 
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveBase")
    public void saveBase(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String, Object> dataRes = PlatBeanUtil.getMapFromRequest(request);
        String DATARES_ID = (String) dataRes.get("DATARES_ID");
        if (StringUtils.isEmpty(DATARES_ID)) {
            dataRes.put("DATARES_TIME", PlatDateTimeUtil.formatDate(new Date(),
                    "yyyy-MM-dd HH:mm:ss"));
        }
        dataRes = dataResService.saveOrUpdate("PLAT_METADATA_DATARES", dataRes,
                AllConstants.IDGENERATOR_UUID, null);
        // 获取资源的ID
        String resId = (String) dataRes.get("DATARES_ID");
        // 获取选择目录的IDS
        String DATARES_CATALOGIDS = request.getParameter("DATARES_CATALOGIDS");
        dataResService.saveOrUpdateResCatalog(resId, DATARES_CATALOGIDS);
        dataRes.put("success", true);
        this.printObjectJsonString(dataRes, response);
    }

    /**
     * 保存附加信息例如返回结果列和排序
     * 
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveAttachInfo")
    public void saveAttachInfo(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String, Object> dataRes = PlatBeanUtil.getMapFromRequest(request);
        dataRes = dataResService.saveOrUpdate("PLAT_METADATA_DATARES", dataRes,
                AllConstants.IDGENERATOR_UUID, null);
        dataRes.put("success", true);
        this.printObjectJsonString(dataRes, response);
    }
    
    /**
     * 统一数据资源调度
     * @param request
     * @param response
     */
    @RequestMapping(params = "invokeRes")
    public void invokeRes(HttpServletRequest request,
            HttpServletResponse response) {
        //获取请求的资源编码
        String PLAT_RESCODE = request.getParameter("PLAT_RESCODE");
        Map<String,Object> result = new HashMap<String,Object>();
        //获取资源信息
        Map<String,Object> resInfo = this.dataResService.getRecord("PLAT_METADATA_DATARES",
                new String[] { "DATARES_CODE" }, new Object[] {PLAT_RESCODE });
        if(resInfo!=null){
            String resId = (String) resInfo.get("DATARES_ID");
            try {
                result = dataResService.invokeRes(request, resId);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                result.put("success", false);
                result.put("msg", "系统内部错误!");
            }
        }else{
            result.put("success", false);
            result.put("msg", "所调度资源不存在!");
        }
        this.printObjectJsonString(result, response);
    }
}

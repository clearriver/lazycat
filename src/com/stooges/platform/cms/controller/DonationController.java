/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.controller;

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
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.cms.service.DonationService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 捐赠信息业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-30 11:03:13
 */
@Controller  
@RequestMapping("/cms/DonationController")  
public class DonationController extends BaseController {
    /**
     * 
     */
    @Resource
    private DonationService donationService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除捐赠信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        donationService.deleteRecords("PLAT_CMS_DONATION","DONATION_ID",selectColValues.split(","));
        sysLogService.saveBackLog("捐赠管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的捐赠信息", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改捐赠信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> donation = PlatBeanUtil.getMapFromRequest(request);
        String DONATION_ID = (String) donation.get("DONATION_ID");
        if(StringUtils.isEmpty(DONATION_ID)){
            donation.put("DONATION_TIME",PlatDateTimeUtil.formatDate(new Date(),
                    "yyyy-MM-dd HH:mm:ss"));
        }
        donation = donationService.saveOrUpdate("PLAT_CMS_DONATION",
                donation,AllConstants.IDGENERATOR_UUID,null);
        donation.put("success", true);
        this.printObjectJsonString(donation, response);
    }
    
    /**
     * 跳转到捐赠信息表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String DONATION_ID = request.getParameter("DONATION_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> donation = null;
        if(StringUtils.isNotEmpty(DONATION_ID)){
            donation = this.donationService.getRecord("PLAT_CMS_DONATION"
                    ,new String[]{"DONATION_ID"},new Object[]{DONATION_ID});
        }else{
            donation = new HashMap<String,Object>();
        }
        request.setAttribute("donation", donation);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String DONATION_ID = request.getParameter("DONATION_ID");
        String DONATION_PARENTID = request.getParameter("DONATION_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> donation = null;
        if(StringUtils.isNotEmpty(DONATION_ID)){
            donation = this.donationService.getRecord("PLAT_CMS_DONATION"
                    ,new String[]{"DONATION_ID"},new Object[]{DONATION_ID});
            DONATION_PARENTID = (String) donation.get("Donation_PARENTID");
        }
        Map<String,Object> parentDonation = null;
        if(DONATION_PARENTID.equals("0")){
            parentDonation = new HashMap<String,Object>();
            parentDonation.put("DONATION_ID",DONATION_PARENTID);
            parentDonation.put("DONATION_NAME","捐赠信息树");
        }else{
            parentDonation = this.donationService.getRecord("PLAT_CMS_DONATION",
                    new String[]{"DONATION_ID"}, new Object[]{DONATION_PARENTID});
        }
        if(donation==null){
            donation = new HashMap<String,Object>();
        }
        donation.put("DONATION_PARENTID",parentDonation.get("DONATION_ID"));
        donation.put("DONATION_PARENTNAME",parentDonation.get("DONATION_NAME"));
        request.setAttribute("donation", donation);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

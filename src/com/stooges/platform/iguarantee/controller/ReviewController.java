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
import com.stooges.platform.iguarantee.service.ReviewService;

/**
 * 
 * 描述 合规性审查业务相关Controller
 * @author river
 * @version 1.0
 * @created 2019-08-18 15:14:41
 */
@Controller  
@RequestMapping("/iguarantee/ReviewController")  
public class ReviewController extends BaseController {
    /**
     * 
     */
    @Resource
    private ReviewService reviewService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除合规性审查数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        reviewService.deleteRecords("PLAT_IGUARANTEE_REVIEW","REVIEW_ID",selectColValues.split(","));
        sysLogService.saveBackLog("合规性审查（个人）",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的合规性审查", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改合规性审查数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> review = PlatBeanUtil.getMapFromRequest(request);
        if(review.containsKey("ENTERPRISE_APPLY_ID")) {
            review.put("IGUARANTEE_ID", review.get("ENTERPRISE_APPLY_ID"));
        }
        String REVIEW_ID = (String) review.get("REVIEW_ID");
        review = reviewService.saveOrUpdate("PLAT_IGUARANTEE_REVIEW",
                review,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //review = reviewService.saveOrUpdateTreeData("PLAT_IGUARANTEE_REVIEW",
        //        review,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(REVIEW_ID)){
            sysLogService.saveBackLog("合规性审查（个人）",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+REVIEW_ID+"]合规性审查", request);
        }else{
            REVIEW_ID = (String) review.get("REVIEW_ID");
            sysLogService.saveBackLog("合规性审查（个人）",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+REVIEW_ID+"]合规性审查", request);
        }
        review.put("success", true);
        this.printObjectJsonString(review, response);
    }
    
    /**
     * 跳转到合规性审查表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String REVIEW_ID = request.getParameter("REVIEW_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> review = null;
        
        if(StringUtils.isNotEmpty(REVIEW_ID)){
            review = this.reviewService.getRecord("PLAT_IGUARANTEE_REVIEW"
                    ,new String[]{"REVIEW_ID"},new Object[]{REVIEW_ID});
        }else{
            review = new HashMap<String,Object>();
        }
        String IGUARANTEE_ID =request.getParameter("IGUARANTEE_ID");
        Map<String,Object> applyinf = null;
        if(StringUtils.isNotEmpty(IGUARANTEE_ID)){
        	applyinf = this.reviewService.getRecord("PLAT_IGUARANTEE_APPLYINFO"
                    ,new String[]{"IGUARANTEE_ID"},new Object[]{IGUARANTEE_ID});
        }else{
        	applyinf = new HashMap<String,Object>();
        }
        String ENTERPRISE_APPLY_ID =request.getParameter("ENTERPRISE_APPLY_ID");
        Map<String,Object> enterprise = null;
        if(StringUtils.isNotEmpty(IGUARANTEE_ID)){
        	enterprise = this.reviewService.getRecord("PLAT_IGUARANTEE_ENTERPRISE_APPLY"
                    ,new String[]{"ENTERPRISE_APPLY_ID"},new Object[]{ENTERPRISE_APPLY_ID});
        }else{
        	enterprise = new HashMap<String,Object>();
        }
        request.setAttribute("review", review);
        request.setAttribute("applyinf",applyinf);
        request.setAttribute("enterprise",enterprise);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String REVIEW_ID = request.getParameter("REVIEW_ID");
        String REVIEW_PARENTID = request.getParameter("REVIEW_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> review = null;
        if(StringUtils.isNotEmpty(REVIEW_ID)){
            review = this.reviewService.getRecord("PLAT_IGUARANTEE_REVIEW"
                    ,new String[]{"REVIEW_ID"},new Object[]{REVIEW_ID});
            REVIEW_PARENTID = (String) review.get("Review_PARENTID");
        }
        Map<String,Object> parentReview = null;
        if(REVIEW_PARENTID.equals("0")){
            parentReview = new HashMap<String,Object>();
            parentReview.put("REVIEW_ID",REVIEW_PARENTID);
            parentReview.put("REVIEW_NAME","合规性审查树");
        }else{
            parentReview = this.reviewService.getRecord("PLAT_IGUARANTEE_REVIEW",
                    new String[]{"REVIEW_ID"}, new Object[]{REVIEW_PARENTID});
        }
        if(review==null){
            review = new HashMap<String,Object>();
        }
        review.put("REVIEW_PARENTID",parentReview.get("REVIEW_ID"));
        review.put("REVIEW_PARENTNAME",parentReview.get("REVIEW_NAME"));
        request.setAttribute("review", review);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

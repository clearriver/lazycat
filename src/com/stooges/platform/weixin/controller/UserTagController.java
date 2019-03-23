/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.controller;

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
import com.stooges.core.model.SqlFilter;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.service.UserTagService;

/**
 * 
 * 描述 用户标签业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-29 11:11:42
 */
@Controller  
@RequestMapping("/weixin/UserTagController")  
public class UserTagController extends BaseController {
    /**
     * 
     */
    @Resource
    private UserTagService userTagService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private PublicService publicService;
    
    /**
     * 删除用户标签数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String userTagId = request.getParameter("userTagId");
        boolean delResult = userTagService.deleteTag(userTagId);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success",delResult);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改用户标签数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> userTag = PlatBeanUtil.getMapFromRequest(request);
        Map<String,Object> result = userTagService.saveOrUpdateTag(userTag);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 跳转到用户标签表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String USERTAG_ID = request.getParameter("USERTAG_ID");
        String PUBLIC_ID = request.getParameter("PUBLIC_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> userTag = null;
        if(StringUtils.isNotEmpty(USERTAG_ID)){
            userTag = this.userTagService.getRecord("PLAT_WEIXIN_USERTAG"
                    ,new String[]{"USERTAG_ID"},new Object[]{USERTAG_ID});
        }else{
            userTag = new HashMap<String,Object>();
            userTag.put("USERTAG_PUBID", PUBLIC_ID);
        }
        request.setAttribute("userTag", userTag);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 跳转到管理界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goView")
    public ModelAndView goView(HttpServletRequest request) {
        String PUBLIC_ID = publicService.firstPublicId();
        request.setAttribute("PUBLIC_ID", PUBLIC_ID);
        return PlatUICompUtil.goDesignUI("weiusermanage", request);
    }
    
    /**
     * 验证字段的唯一性
     * @param request
     * @param response
     */
    @RequestMapping(params = "checkUnique")
    public void checkUnique(HttpServletRequest request,
            HttpServletResponse response) {
        String USERTAG_NAME = request.getParameter("USERTAG_NAME");
        String RECORD_ID = request.getParameter("RECORD_ID");
        String PUB_ID = request.getParameter("PUB_ID");
        Map<String,String> result = new HashMap<String,String>();
        boolean isExists = userTagService.isExists(USERTAG_NAME, PUB_ID,RECORD_ID);
        if(isExists){
            result.put("error", "该标签已经存在,请重新输入!");  
        }
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 跳转到用户批量打上标签
     * @param request
     * @return
     */
    @RequestMapping(params = "goMulSign")
    public ModelAndView goMulSign(HttpServletRequest request) {
        String USERTAG_PUBID = request.getParameter("USERTAG_PUBID");
        request.setAttribute("USERTAG_PUBID", USERTAG_PUBID);
        return PlatUICompUtil.goDesignUI("weixinmultagsform", request);
    }
    
    /**
     * 用户批量打上标签
     * @param request
     * @param response
     */
    @RequestMapping(params = "mulSignTags")
    public void mulSignTags(HttpServletRequest request,
            HttpServletResponse response) {
        String USER_IDS = request.getParameter("USER_IDS");
        String USER_TAGIDS = request.getParameter("USER_TAGIDS");
        userTagService.saveSignUsersTags(USER_IDS, USER_TAGIDS);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 自动数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "autoTagAndUser")
    public void autoTagAndUser(HttpServletRequest request,
            HttpServletResponse response) {
        //如果自动补全的类型为2,那么获取到key之后进行判断过滤
        //String keyword = request.getParameter("keyword");
        SqlFilter sqlFilter = new SqlFilter(request);
        List<Map<String,Object>> list = userTagService.findAutoTagUser(sqlFilter);
        String json = JSON.toJSONString(list);
        this.printJsonString(json.toLowerCase(), response);
    }
    
    /**
     * 获取树形的数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "tagAndUsers")
    public void groupAndRoles(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> params= PlatBeanUtil.getMapFromRequest(request);
        String departJson = userTagService.getTreeJson(params);
        this.printJsonString(departJson, response);
    }
}

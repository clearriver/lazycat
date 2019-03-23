/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.octo.captcha.service.CaptchaServiceException;
import com.stooges.core.security.SessionContext;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.BrowserUtils;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatDbUtil;
import com.stooges.core.util.PlatFileUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatNetWorkUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.UUIDGenerator;
import com.stooges.core.web.socket.PlatWebSocket;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.jcaptcha.util.CaptchaServiceSingleton;
import com.stooges.platform.system.service.GlobalConfigService;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.system.service.SysUserService;

/**
 * 描述
 * @author 胡裕
 * @created 2017年4月4日 下午5:02:22
 */
@Controller
@RequestMapping("/security/LoginController")
public class LoginController extends BaseController {
	/**
     * 
     */
    @Resource
    private GlobalConfigService globalConfigService;
    /***
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private SysUserService sysUserService;
    
    /**
     * 后台注销方法的实现
     * @param request
     * @return
     */
    @RequestMapping("/backLogoff")
    public ModelAndView backLogoff(HttpServletRequest request,HttpServletResponse response){
        if(PlatAppUtil.getSession()!=null){
            String sessionId = PlatAppUtil.getSession().getId();
            Map<String,Map<String,Object>> onlineUserMap = PlatAppUtil.getOnlineUser();
            if(onlineUserMap!=null&&onlineUserMap.get(sessionId)!=null){
                PlatWebSocket.webSocketMap.remove(onlineUserMap.get(sessionId).get("SYSUSER_ID").toString());
                onlineUserMap.put(sessionId, null);
                sysLogService.saveBackLog("登录模块",SysLogService.OPER_TYPE_LOGOFF,"登出了系统", request);
            }
        }
        PlatAppUtil.getSession().setAttribute(AllConstants.BACKPLAT_USERSESSIONKEY, null);
        //跳转到后台登录界面
        Map<String,Object> globalConfig = this.globalConfigService.getFirstConfigMap();
    	String loginUrl = "background/login";
    	if(globalConfig!=null){
    		loginUrl = (String)globalConfig.get("CONFIG_LOGINURL");
    		String globalLogoUrl = (String)globalConfig.get("CONFIG_LOGOURL");
    		String globalProjectName = (String)globalConfig.get("CONFIG_PROJECTNAME");
    		PlatAppUtil.getSession().setAttribute("globalProjectName", globalProjectName);
    		PlatAppUtil.getSession().setAttribute("globalLogoUrl", globalLogoUrl);
    	}
    	PlatNetWorkUtil.deleteCookie(response, AllConstants.SSO_COOKIENAME);
    	//获取单点登录回调的URL地址
        String service = request.getParameter("service");
        if(StringUtils.isNotEmpty(service)){
            request.setAttribute("service", service);
        }
        return new ModelAndView(loginUrl);
    }
    
    /**
     * 进行登录操作
     * @param sysUser
     */
    private void doLogin(Map<String,Object> sysUser,String USERNAME,HttpServletRequest request){
        PlatAppUtil.getSession().setAttribute(AllConstants.BACKPLAT_USERSESSIONKEY, sysUser);
        /*String userJson  = JSON.toJSONString(sysUser);
        PlatAppUtil.getSession().setAttribute(AllConstants.BACKPLAT_USERSESSIONJSON,
                StringEscapeUtils.escapeHtml3(userJson));*/
        //获取是否允许相同账号重复登录
        Map<String,Object> globalConfig = globalConfigService.getFirstConfigMap();
        String allowSameUserLogin = (String) globalConfig.get("CONFIG_ALOWSAMELOGIN");
        if(allowSameUserLogin.equals("-1")&&!USERNAME.equals("admin")){
            PlatAppUtil.kickoutSameOnlineUser(USERNAME);
        }
        PlatAppUtil.addOnlineUser(sysUser);
        sysLogService.saveBackLog("登录模块",SysLogService.OPER_TYPE_LOGIN,"登录了系统", request);
    }
    
    
    /**
     * 跳转到后台登录界面
     * @param request
     * @return
     */
    @RequestMapping("/goBackLogin")
    public ModelAndView goBackLogin(HttpServletRequest request){
    	Map<String,Object> globalConfig = this.globalConfigService.getFirstConfigMap();
    	String loginUrl = "background/login";
    	if(globalConfig!=null){
    		loginUrl = (String)globalConfig.get("CONFIG_LOGINURL");
    		String globalLogoUrl = (String)globalConfig.get("CONFIG_LOGOURL");
    		String globalProjectName = (String)globalConfig.get("CONFIG_PROJECTNAME");
    		String CONFIG_FAVICONURL= (String) globalConfig.get("CONFIG_FAVICONURL");
    		PlatAppUtil.getSession().setAttribute("globalProjectName", globalProjectName);
    		PlatAppUtil.getSession().setAttribute("globalLogoUrl", globalLogoUrl);
    		PlatAppUtil.getSession().setAttribute("CONFIG_FAVICONURL", CONFIG_FAVICONURL);
    	}
    	//获取单点登录回调的URL地址
    	String service = request.getParameter("service");
    	if(StringUtils.isNotEmpty(service)){
    	    request.setAttribute("service", service);
    	}
        return new ModelAndView(loginUrl);
    }

    /**
     * 后台登录方法实现
     * @param request
     * @return
     */
    @RequestMapping("/backLogin")
    public void backLogin(HttpServletRequest request,
            HttpServletResponse response) {
        String USERNAME = request.getParameter("USERNAME");
        String PASSWORD = request.getParameter("PASSWORD");
        Map<String,Object> result = new HashMap<String,Object>();
        String captchaId = request.getSession().getId();
        String jcaptcha = request.getParameter("jcaptcha");
        boolean valid = false;
        Map<String,Object> globalConfig = globalConfigService.getFirstConfigMap();
        String backloginValidCode = (String) globalConfig.get("CONFIG_BACKVALIDCODE");
        if(backloginValidCode.equals("1")){
            try {
                jcaptcha = jcaptcha.toLowerCase();
                valid = CaptchaServiceSingleton.getInstance().validateResponseForID(captchaId,
                        jcaptcha);
            } catch (CaptchaServiceException e) {
                e.printStackTrace();
            }
        }else{
            valid = true;
        }
        if(valid){
            Map<String,Object> sysUser = PlatAppUtil.getBackPlatLoginUser();
            if(sysUser!=null){
                result.put("success", true);
            }else{
                sysUser = sysUserService.checkAccountAndPass(USERNAME,PASSWORD);
                if(sysUser==null){
                    result.put("success", false);
                    result.put("msg", "用户名或者密码错误!");
                }else{
                    int sysUserStatus = Integer.parseInt(sysUser.get("SYSUSER_STATUS").toString());
                    if(sysUserStatus==SysUserService.SYSUSER_STATUS_DEL){
                        result.put("success", false);
                        result.put("msg", "用户名或者密码错误!");
                    }else if(sysUserStatus==SysUserService.SYSUSER_STATUS_DISABLED){
                        result.put("success", false);
                        result.put("msg", "该用户已经被禁用!");
                    }else if(sysUserStatus==SysUserService.SYSUSER_STATUS_LOCKED){
                        result.put("success", false);
                        result.put("msg", "该用户已经被锁定!");
                    }else{
                        result.put("success", true);
                    }
                }
                boolean success = (boolean) result.get("success");
                if(success){
                    this.doLogin(sysUser, USERNAME, request);
                }
            }
        }else{
            result.put("success", false);
            result.put("msg", "验证码错误!");
        }
        String service = request.getParameter("service");
        boolean success = (boolean) result.get("success");
        PlatNetWorkUtil.addCookie(response, AllConstants.SSO_COOKIENAME, USERNAME, 0);
        if(StringUtils.isNotEmpty(service)&&success==true){
            long time = System.currentTimeMillis();
            String timeString = USERNAME + time;
            AllConstants.SSO_TICKETANDNAME.put(timeString, USERNAME);
            result.put("redirecturl", service);
        }
        this.printObjectJsonString(result, response);
    }
    
    
    /**
     * 强制退出
     * @param request
     * @param response
     */
    @RequestMapping(params = "forceLogout")
    public void forceLogout(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        String[] sessionIds = selectColValues.split(",");
        Map<String,Map<String,Object>> onlineUserMap = PlatAppUtil.getOnlineUser();
        for(String sessionId:sessionIds){
            if(onlineUserMap.get(sessionId)!=null){
                onlineUserMap.put(sessionId, null);
            }
            HttpSession session = SessionContext.getSession(sessionId);
            if(session!=null){
                session.setAttribute(AllConstants.BACKPLAT_USERSESSIONKEY,null);
                session.setAttribute(AllConstants.BACKPLAT_USERSTATUSKEY,"forcelogout");
            }
        }
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
}

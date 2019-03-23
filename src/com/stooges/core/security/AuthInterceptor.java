/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.system.service.SysUserService;

/**
 * @author 胡裕
 * 权限拦截器
 * 
 */
public class AuthInterceptor implements HandlerInterceptor {
    
    /**
     * 
     */
    private String backLoginUrl;

    /**
     * @return the backLoginUrl
     */
    public String getBackLoginUrl() {
        return backLoginUrl;
    }

    /**
     * @param backLoginUrl the backLoginUrl to set
     */
    public void setBackLoginUrl(String backLoginUrl) {
        this.backLoginUrl = backLoginUrl;
    }

    /**
     * 
     */
    @Override
    public void afterCompletion(HttpServletRequest arg0,
            HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    @Override
    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
            Object arg2, ModelAndView arg3) throws Exception {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    @Override
    public boolean preHandle(HttpServletRequest request, 
            HttpServletResponse response, Object obj) throws Exception {
        // TODO Auto-generated method stub
        String requestPath = PlatAppUtil.getAbsoluteRequestPath(request);
        String webRootPath = PlatAppUtil.getWebBasePath(request);
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        //获取可匿名访问的URL集合
        Set<String> anonUrlSet = PlatAppUtil.getAllAnonUrlSet();
        anonUrlSet.add(this.getBackLoginUrl());
        if(anonUrlSet.contains(requestPath)||StringUtils.isEmpty(requestPath)){
            return true;
        }else{
            Map<String,Object> sysUser = PlatAppUtil.getBackPlatLoginUser();
            if(sysUser!=null){
                //获取被授权的URL集合
                Set<String> grantUrlSet = (Set<String>) sysUser.get(SysUserService.GRANTURLS_KEY);
                boolean isAllowAccess = false;
                if(grantUrlSet!=null){
                    String url = PlatAppUtil.getAbsoluteRequestPath(request);
                    Set<String> allUrlSet = PlatAppUtil.getAllResUrlSet();
                    if(url.contains("&")){
                        url = url.substring(0, url.indexOf("&"));
                    }
                    if(allUrlSet.contains(url)){
                        isAllowAccess = grantUrlSet.contains(url);
                    }else{
                        isAllowAccess= true;
                    }
                }else{
                    isAllowAccess= false; 
                }
                if(!isAllowAccess){
                    PrintWriter out;
                    try {
                        out = response.getWriter();
                        StringBuilder builder = new StringBuilder();  
                        builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");  
                        builder.append("alert(\"当前用户不具备URL访问权限,被管理员强制下线!\");");  
                        builder.append("window.top.location.href=\""); 
                        builder.append(webRootPath).append(backLoginUrl);
                        builder.append("\";</script>");  
                        out.print(builder.toString());  
                        out.close();  
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }else{
                    return true;
                }
            }else{
                PrintWriter out;
                try {
                    out = response.getWriter();
                    StringBuilder builder = new StringBuilder();  
                    String header = request.getHeader("X-Requested-With");
                    //获取后台登录用户KEY
                    String userStatus = null;
                    String backPlatUserStatus = (String) PlatAppUtil.getSession().
                            getAttribute(AllConstants.BACKPLAT_USERSTATUSKEY);
                    if(StringUtils.isNotEmpty(backPlatUserStatus)){
                        userStatus = backPlatUserStatus;
                    }else{
                        userStatus = "timeout";
                    }
                    if(StringUtils.isNotEmpty(header)&&header.equalsIgnoreCase("XMLHttpRequest")){
                        response.setHeader("backplatsessionstatus",userStatus);
                        builder.append("sessiontimeout");
                        out.print(builder.toString());  
                        out.close();  
                    }else{
                        builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");  
                        if(userStatus.equals("timeout")){
                            builder.append("alert(\"会话过期或凭证无效，请重新登录!\");");  
                        }else if(userStatus.equals("forcelogout")){
                            builder.append("alert(\"当前用户被管理员强制下线!\");");  
                        }else if(userStatus.equals("sameuserlogout")){
                            builder.append("alert(\"检测到相同用户在异地登录,当前用户被强制下线!\");"); 
                        }
                        builder.append("window.top.location.href=\""); 
                        builder.append(webRootPath).append(backLoginUrl);
                        builder.append("\";</script>");  
                        out.print(builder.toString());  
                        out.close();  
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        
        
    }

}

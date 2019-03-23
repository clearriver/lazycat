/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.sso;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

/**
 * @author 胡裕
 *
 * 
 */
public class SSOClientFilter implements Filter {
    /**
     * 匿名URL
     */
    private static String ANONURLS = null;
    /**
     * 单点登录服务器URL
     */
    private static String SSOSERVERURL = null;
    /**
     * 登录成功后的回调
     */
    private static String INVOKESERVICE = null;
    /**
     * 单点登录过滤器所构建的账号
     */
    public static final String SSO_USERACCOUNT = "STOOGES_SSOUSERACCOUNT";

    @Override
    public void destroy() {
    }
    
    /**
    *
    * <p>
    * getRequestURL是否包含在URL之内
    * </p>
    *
    * @param request
    * @param url
    *            参数为以';'分割的URL字符串
    * @return
    */
   public static boolean inContainURL(HttpServletRequest request, String url) {
       boolean result = false;
       if(StringUtils.isNotEmpty(url)){
           String[] urlArr = url.split(";");
           String reqUrl = request.getRequestURI();
           if(reqUrl.length()>request.getContextPath().length()){
               reqUrl = reqUrl
                       .substring(request.getContextPath().length() + 1);// 去掉项目路径
           }
           if(url.contains("blank")&&StringUtils.isEmpty(reqUrl)){
               return true;
           }
           for(String ignoreUrl:urlArr){
               if(reqUrl.startsWith(ignoreUrl)){
                   result = true;
                   break;
               }
           }
       }
       return result;
   }
   
   /**
    * 获得请求路径,带上问号的参数
    * 
    * @param request
    * @return
    */
   public String getRequestPath(HttpServletRequest request) {
       String requestPath = request.getRequestURL() + "?"
               + request.getQueryString();
       return requestPath;
   }
   
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        boolean anonAble = inContainURL(request, ANONURLS);
        if(anonAble){
            filterChain.doFilter(request, response);
        }else{
            String username = (String) session.getAttribute(SSO_USERACCOUNT);
            String url = URLEncoder.encode(getRequestPath(request), "UTF-8");
            if(StringUtils.isNotEmpty(username)){
                Cookie[] cookies = request.getCookies();
                //定义用户的账号
                String value = null;
                if (null != cookies) {
                    for (Cookie cookie : cookies) {
                        if ("PLATSTOOGES_SSO".equals(cookie.getName())) {
                            value = cookie.getValue();
                            break;
                        }
                    }
                }
                if(StringUtils.isNotEmpty(value)){
                    filterChain.doFilter(request, response);
                }else{
                    response.sendRedirect(SSOSERVERURL+"security/LoginController/goBackLogin.do?service=" + url);
                }
            }else{
                //获取票据
                String ticket = request.getParameter("ticket");
                if(StringUtils.isNotEmpty(ticket)){
                    PostMethod postMethod = new PostMethod(SSOSERVERURL+"PlatSSOTicket");
                    postMethod.addParameter("ticket", ticket);
                    HttpClient httpClient = new HttpClient();
                    try {
                        httpClient.executeMethod(postMethod);
                        username = postMethod.getResponseBodyAsString();
                        postMethod.releaseConnection();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (StringUtils.isNotEmpty(username)) {
                        session.setAttribute(SSO_USERACCOUNT, username);
                        if(StringUtils.isNotEmpty(INVOKESERVICE)){
                            String className = INVOKESERVICE.substring(0, INVOKESERVICE.lastIndexOf("."));
                            String methodName = INVOKESERVICE.substring(INVOKESERVICE.lastIndexOf(".")+1,
                                    INVOKESERVICE.length());
                            try {
                                Class<?> invokeClass = Class.forName(className);
                                Method method = invokeClass.getMethod(methodName,HttpSession.class,String.class);//得到方法对象
                                Object invokeObj = (Object) invokeClass.newInstance();
                                method.invoke(invokeObj,session,username);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        filterChain.doFilter(request, response);
                    } else {
                        response.sendRedirect(SSOSERVERURL+"security/LoginController/goBackLogin.do?service=" + url);
                    }
                }else {
                    response.sendRedirect(SSOSERVERURL+"security/LoginController/goBackLogin.do?service=" + url);
                }
            }
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        ANONURLS = config.getInitParameter("anonurls");
        SSOSERVERURL = config.getInitParameter("ssoserverurl");
        INVOKESERVICE= config.getInitParameter("invokeservice");
    }

}

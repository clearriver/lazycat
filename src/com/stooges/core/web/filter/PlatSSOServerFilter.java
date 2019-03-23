/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatNetWorkUtil;

/**
 * @author 胡裕
 * 单点登录产生票据过滤器
 * 
 */
public class PlatSSOServerFilter implements Filter {

    @Override
    public void destroy() {
    }
    
    /**
     * 
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String reqUrl = request.getRequestURI();
        if(reqUrl.length()>request.getContextPath().length()){
            reqUrl = reqUrl
                    .substring(request.getContextPath().length() + 1);// 去掉项目路径
        }
        if(reqUrl.startsWith("security/LoginController/backLogoff.do")){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String service = request.getParameter("service");
        String ticket = request.getParameter("ticket");
        //定义用户的账号
        String userAccount = PlatNetWorkUtil.getCookieValue(request, AllConstants.SSO_COOKIENAME);
        if(StringUtils.isEmpty(service)&&StringUtils.isEmpty(ticket)){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if(StringUtils.isNotEmpty(userAccount)){
            long time = System.currentTimeMillis();
            String timeString = userAccount + time;
            AllConstants.SSO_TICKETANDNAME.put(timeString, userAccount);
            StringBuilder url = new StringBuilder();
            url.append(service);
            if (0 <= service.indexOf("?")) {
                url.append("&");
            } else {
                url.append("?");
            }
            url.append("ticket=").append(timeString);
            response.sendRedirect(url.toString());
        }else{
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        
    }

}

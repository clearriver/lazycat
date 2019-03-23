/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 胡裕
 *
 * 2017年10月13日
 */
public class ResCacheFilter implements Filter {
    /**
     * 
     */
    private FilterConfig filterConfig;
    
    /**
     * 
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    /**
     * 
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        //1.获取用户想访问的资源
        String uri = request.getRequestURI(); 
        //2.得到用户想访问的资源的后缀名
        String ext = uri.substring(uri.lastIndexOf(".")+1); 
        //得到资源需要缓存的时间
        String time = filterConfig.getInitParameter(ext);
        if(time!=null){
            long t = Long.parseLong(time)*3600*1000;
            //设置浏览器缓存
            response.setDateHeader("expires", System.currentTimeMillis() + t);
        } 
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        
    }

}

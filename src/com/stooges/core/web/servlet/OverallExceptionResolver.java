/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.PlatLogUtil;

/**
 * 描述
 * @author 胡裕
 * @created 2017年3月2日 上午11:39:53
 */
@Controller
public class OverallExceptionResolver implements HandlerExceptionResolver {

    /**
     * 进行全局异常的过滤和处理
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex) {
        //handler为当前处理器适配器执行的对象
        String message = null;
        //判断是否为系统自定义异常。
        if(ex instanceof Exception) {
            message = ExceptionUtils.getStackTrace(ex);
            PlatLogUtil.printStackTrace(ex);
        }
        ModelAndView modelAndView = new ModelAndView();
        //跳转到相应的处理页面
        request.setAttribute("errorMsg", message);
        modelAndView.setViewName("common/500");
        return modelAndView;
    }

}

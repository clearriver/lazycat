/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.weixin.service.MsgEngineService;
import com.stooges.platform.weixin.util.SignUtil;

/**
 * @author 胡裕
 *
 * 
 */
@WebServlet(name="WeixinServlet",urlPatterns="/WeixinServlet")
public class WeixinServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1335986257026691468L;

    /**
     * 请求校验（确认请求来自微信服务器）
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 微信加密签名
        String signature = request.getParameter("signature");
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 随机字符串
        String echostr = request.getParameter("echostr");

        PrintWriter out = response.getWriter();
        // 请求校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            out.print(echostr);
        }
        out.close();
        out = null;
    }

    /**
     * 请求校验与处理
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        // 接收参数微信加密签名、 时间戳、随机数
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        // 请求校验
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            // 调用核心服务类接收处理请求
            MsgEngineService msgEngineService = (MsgEngineService) PlatAppUtil.getBean("msgEngineService");
            String respXml = msgEngineService.invokeMsg(request);
            if(StringUtils.isNotEmpty(respXml)){
                PrintWriter out = response.getWriter();
                out.print(respXml);
                out.close();
            }
        }
    }

}

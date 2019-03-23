/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.stooges.core.util.AllConstants;

/**
 * @author 胡裕
 * 产生票据的servlet
 * 
 */
@WebServlet(name = "PlatSSOTicket",urlPatterns = "/PlatSSOTicket")  
public class PlatSSOTicket extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ticket = request.getParameter("ticket");
        String userAccount = AllConstants.SSO_TICKETANDNAME.get(ticket);
        AllConstants.SSO_TICKETANDNAME.remove(ticket);
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(StringUtils.isNotEmpty(userAccount)){
            pw.write(userAccount);
        }else{
            pw.write("");
        }
        pw.flush();
        pw.close();
    }
}

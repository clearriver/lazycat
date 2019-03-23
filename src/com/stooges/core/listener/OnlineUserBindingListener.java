/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.stooges.core.security.SessionContext;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatLogUtil;

/**
 * @author 胡裕 在线用户列表监听器
 * 
 */
public class OnlineUserBindingListener implements HttpSessionListener  {

    /**
     * 
     */
    @Override
    public void sessionCreated(HttpSessionEvent event) {
        // TODO Auto-generated method stub
        //System.out.println("新建session:"+event.getSession().getId());
        SessionContext.addSession(event.getSession());
    }

    /**
     * 
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        String sessionId = session.getId();
        try{
            Map<String,Map<String,Object>> onlineUser = PlatAppUtil.getOnlineUser();
            if(onlineUser.get(sessionId)!=null){
                onlineUser.put(sessionId, null);
            }
        }catch(Exception e){
            PlatLogUtil.doNothing(e);
        }
        
    }
}

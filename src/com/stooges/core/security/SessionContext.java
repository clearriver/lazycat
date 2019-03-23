/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.security;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

/**
 * @author 胡裕
 *
 * 
 */
public class SessionContext {
    /**
     * 
     */
    private static HashMap mymap = new HashMap();
    
    /**
     * 
     * @param session
     */
    public static synchronized void addSession(HttpSession session) {
        if (session != null) {
            mymap.put(session.getId(), session);
        }
    }
    
    /**
     * 
     * @param session
     */
    public static synchronized void delSession(HttpSession session) {
        if (session != null) {
            mymap.remove(session.getId());
        }
    }

    /**
     * 
     * @param session_id
     * @return
     */
    public static synchronized HttpSession getSession(String session_id) {
        if (session_id == null)
        return null;
        return (HttpSession) mymap.get(session_id);
    }

}

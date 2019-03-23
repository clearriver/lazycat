/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述 定义全局常用常量值
 * @author 胡裕
 * @created 2017年1月3日 下午2:52:31
 */
public class AllConstants {
    /**
     * 主键生成策略:使用UUID
     */
    public static final int IDGENERATOR_UUID = 1;
    /**
     * 主键生成策略:自增长
     */
    public static final int IDGENERATOR_AUTOINCREMENT = 2;
    /**
     * 主键生成策略:分配模式
     */
    public static final int IDGENERATOR_ASSIGNED = 3;
    /**
     * 主键生成策略:序列
     */
    public static final int IDGENERATOR_SEQ = 4;
    /**
     * 后台登录用户sessionkey
     */
    public static final String BACKPLAT_USERSESSIONKEY = "__BACKPLATUSER";
    /**
     * 后台登录用户状态KEY
     */
    public static final String BACKPLAT_USERSTATUSKEY = "__BACKUSERSTATUS";
    /**
     * 后台登录用户JSON字符串
     */
    public static final String BACKPLAT_USERSESSIONJSON = "__BACKPLATUSERJSON";
    /**
     * 改变表单控件权限和必填属性的MAPKEY
     */
    public static final String CHANGECOMPAUTHMAP_KEY = "__CHANGECOMPAUTHMAP";
    /**
     * 改变自定义控件的权限的MAPKEY
     */
    public static final String CHANGEDEFCTRLAUTHMAP_KEY = "__CHANGEDEFCTRLAUTHMAP";
    /**
     * 会话强制退出KEY
     */
    public static final String SESSION_FORCE_LOGOUT_KEY = "session.force.logout";
    /**
     * SSO Cookie名称
     */
    public static final String SSO_COOKIENAME = "PLATSTOOGES_SSO";
    /**
     * 在线用户MAP
     */
    public static final String ONLINEUSERS_KEY = "__ONLINEUSERS";
    /**
     * SSO票据和名称
     */
    public static final Map<String,String> SSO_TICKETANDNAME = new HashMap<String,String>();
    
}

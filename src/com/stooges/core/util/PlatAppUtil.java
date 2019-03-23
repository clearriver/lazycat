/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.stooges.core.security.SessionContext;

/**
 * 描述 封装平台应用程序接口
 * @author 胡裕
 * @created 2017年1月5日 下午4:08:38
 */
public class PlatAppUtil implements ApplicationContextAware{

    /**
     * 定义数据库类型
     */
    private static String dbType = "";
    /**
     * 定义数据库schema
     */
    private static String dbSchema="";
    /**
     * application
     */
    private static ApplicationContext applicationContext;
    /**
     * servletCOntext
     */
    private static ServletContext servletContext;
    /**
     * 定义定时调度线程池
     */
    private static Scheduler scheduler;
    /**
     * 客户端验证规则
     */
    private static Map<String,String[]> validRules = new HashMap<String,String[]>();
    /**
     * 所有资源URL集合
     */
    private static Set<String> allResUrlSet = new HashSet<String>();
    /**
     * 所有有效的token集合
     */
    private static Set<String> validLoginTokenSet = new HashSet<String>();
    /**
     * 所有匿名的URL集合
     */
    private static Set<String> allAnonUrlSet = new HashSet<String>();
   
    /**
     * 主键集合MAP
     */
    private static Map<String,List<String>> primaryKeyMap = new HashMap<String,List<String>>();
    /**
     * 表字段集合MAP
     */
    private static Map<String,List<String>> tableColumnMap = new HashMap<String,List<String>>();
    /**
     * 当前数据源
     */
    private static String currentDbSource;
    /**
     * 缺省数据库类型
     */
    private static String defaultDbType;
    /**
     * 缺省数据库schema
     */
    private static String defaultDbSchema;

    /**
     * 
     */
    public PlatAppUtil(){
        String configDbType = PlatDbUtil.getDefaultDbType();
        PlatAppUtil.setDbType(configDbType);
        PlatAppUtil.setDbSchema(PlatPropUtil.getPropertyValue("conf/config.properties", "dbschema"));
        PlatAppUtil.setDefaultDbType(configDbType);
        PlatAppUtil.setDefaultDbSchema(PlatPropUtil.getPropertyValue("conf/config.properties", "dbschema"));
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            PlatLogUtil.printStackTrace(e);
        }
    }
    
    /**
     * 获取后台当前登录用户对象
     * 除了拥有数据库中所设计的字段属性,还拥有以下字段
     * RESCODESET:被授权的资源编码集合,是一个Set
     * RESCODES:被授权的资源编码字符串,是一个字符串
     * GRANTURLS:被授权的访问URL,是一个Set
     * ROLECODESET:被授权的角色编码集合,是一个Set
     * ROLECODES:被授权的角色编码字符串用逗号拼接
     * FLOWDEFTYPEIDS:被授权的流程定义和类别ID
     * @return
     */
    public static Map<String,Object> getBackPlatLoginUser(){
        Map<String,Object> sysUser = (Map<String, Object>) PlatAppUtil.getSession().
                getAttribute(AllConstants.BACKPLAT_USERSESSIONKEY);
        return sysUser;
    }
    /**
     * 设置会话级别的缓存
     * @param key:key
     * @param obj:对象
     */
    public static void setSessionCache(String key,Object obj){
        getSession().setAttribute(key, obj);
    }
    
    /**
     * 获取会话级别的缓存
     * @param key
     * @return
     */
    public static Object getSessionCache(String key){
        Object target = getSession().getAttribute(key);
        return target;
    }
    /**
     * 删除指定的会话缓存
     * @param key
     */
    public static void removeSessionCache(String key){
        getSession().removeAttribute(key);
    }
    
    /**
     * 获取request对象
     * 描述
     * @author 胡裕
     * @created 2014年9月11日 下午4:15:26
     * @return
     */
    public static HttpServletRequest getRequest() {
        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();
            return request;
        }catch(Exception e){
            PlatLogUtil.doNothing(e);
        }
        return null;
        
    }
    /**
     * 获取session对象
     * 描述
     * @author 胡裕
     * @created 2014年9月11日 下午4:15:42
     * @return
     */
    public static HttpSession getSession() {
        HttpSession session = getRequest().getSession();
        return session;
    }
    
    /**
     * 获取spring Bean对象
     * @param cls
     * @return
     */
    public static Object getBean(Class cls) {
        return applicationContext.getBean(cls);
    }
    
    /**
     * 根据beanId实例化bean
     * 描述
     * @created 2014年9月11日 下午4:14:25
     * @param beanId
     * @return
     */
    public static Object getBean(String beanId) {
        return applicationContext.getBean(beanId);
    }
    
    /**
     * 返回数据库类型
     */
    public static String getDbType() {
        return dbType;
    }

    /**
     * 设置数据库类型
     */
    public static void setDbType(String dbType) {
        PlatAppUtil.dbType = dbType;
    }
    
    /**
     * 返回数据库schema
     */
    public static String getDbSchema() {
        return dbSchema;
    }

    /**
     * 设置数据库schema
     * @param dbSchema
     */
    public static void setDbSchema(String dbSchema) {
        PlatAppUtil.dbSchema = dbSchema;
    }
    
    /**
     * @return the applicationContext
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 
     */
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * @return the servletContext
     */
    public static ServletContext getServletContext() {
        return servletContext;
    }
    
    /**
     * 初始化应用容器
     * @param _servletContext
     */
    public static void init(ServletContext _servletContext) {
        servletContext = _servletContext;
    }
    
    /**
     * @return the scheduler
     */
    public static Scheduler getScheduler() {
        return scheduler;
    }
    
    /**
     * 获取web应用的绝对物理路径
     * 描述
     * @author 胡裕
     * @created 2014年9月11日 下午4:14:41
     * @return
     */
    public static String getAppAbsolutePath() {
        return servletContext.getRealPath("/");
    }
    
    /**
     * 获取完整的请求路径,不包含WEB项目路径,不包括参数
     * @param request
     * @return
     */
    public static String getAbsoluteRequestPath(HttpServletRequest request){
        String requestPath = request.getRequestURI();
        //如果开放以下代码,那么将获取包含参数的请求
        /*String queryParams = request.getQueryString();
        if(StringUtils.isNotEmpty(queryParams)){
            requestPath+=("?"+queryParams);
        }*/
        if(requestPath.length()>request.getContextPath().length()){
            requestPath = requestPath
                    .substring(request.getContextPath().length() + 1);// 去掉项目路径
        }
        return requestPath;
    }
    
    /**
     * 获取项目的根路径
     * @param request
     * @return
     */
    public static String getWebBasePath(HttpServletRequest request){
        String path = request.getContextPath(); 
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; 
        return basePath;
    }
    /**
     * 获取本地IP地址
     * @return
     */
    public static String getLocalHostIp(){
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            PlatLogUtil.printStackTrace(e);
        }
        return ip;
    }
    
    /**
     * 项目代码项目路径
     * @return
     */
    public static String getCodeProjectPath(){
       return PlatPropUtil.getPropertyValue("conf/config.properties","genCodeProject");
    }
    
    /**
     * 根据request对象获取服务器基本请求路径
     * @param request
     * @return
     */
    public static String getServerBasePath(HttpServletRequest request){
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+
                request.getServerPort()+request.getContextPath()+"/";
        return basePath;
    }
    
    /**
     * @return the validRules
     */
    public static Map<String, String[]> getValidRules() {
        return validRules;
    }

    /**
     * @param validRules the validRules to set
     */
    public static void setValidRules(Map<String, String[]> validRules) {
        PlatAppUtil.validRules = validRules;
    }
    
    /**
     * @return the allResUrlSet
     */
    public static Set<String> getAllResUrlSet() {
        return allResUrlSet;
    }

    /**
     * @param allResUrlSet the allResUrlSet to set
     */
    public static void setAllResUrlSet(Set<String> allResUrlSet) {
        PlatAppUtil.allResUrlSet = allResUrlSet;
    }
    
    /**
     * @return the validLoginTokenSet
     */
    public static Set<String> getValidLoginTokenSet() {
        return validLoginTokenSet;
    }

    /**
     * @param validLoginTokenSet the validLoginTokenSet to set
     */
    public static void setValidLoginTokenSet(Set<String> validLoginTokenSet) {
        PlatAppUtil.validLoginTokenSet = validLoginTokenSet;
    }
    
    /**
     * @return the primaryKeyMap
     */
    public static Map<String, List<String>> getPrimaryKeyMap() {
        return primaryKeyMap;
    }

    /**
     * @param primaryKeyMap the primaryKeyMap to set
     */
    public static void setPrimaryKeyMap(Map<String, List<String>> primaryKeyMap) {
        PlatAppUtil.primaryKeyMap = primaryKeyMap;
    }
    

    /**
     * @return the tableColumnMap
     */
    public static Map<String, List<String>> getTableColumnMap() {
        return tableColumnMap;
    }

    /**
     * @param tableColumnMap the tableColumnMap to set
     */
    public static void setTableColumnMap(Map<String, List<String>> tableColumnMap) {
        PlatAppUtil.tableColumnMap = tableColumnMap;
    }
    
    /**
     * 获取cookie的值
     * @param cookieName
     * @param request
     */
    public static String getCookieValue(String cookieName,
            HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String cookieValue = null;
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName) && null != cookie.getValue()) {
                    cookieValue = cookie.getValue();
                }
            }
        }
        return cookieValue;
    }
    
    /**
     * @return the allAnonUrlSet
     */
    public static Set<String> getAllAnonUrlSet() {
        return allAnonUrlSet;
    }

    /**
     * @param allAnonUrlSet the allAnonUrlSet to set
     */
    public static void setAllAnonUrlSet(Set<String> allAnonUrlSet) {
        PlatAppUtil.allAnonUrlSet = allAnonUrlSet;
    }
    
    /**
     * 添加在线用户
     * @param sysUser
     */
    public static void addOnlineUser(Map<String,Object> sysUser){
        // 把用户名放入在线列表
        ServletContext application = PlatAppUtil.getSession().getServletContext();
        Map<String,Map<String,Object>> onlineUserMap = (Map<String, Map<String, Object>>) 
                application.getAttribute(AllConstants.ONLINEUSERS_KEY);
        if(onlineUserMap==null){
            onlineUserMap = new HashMap<String,Map<String,Object>>();
            application.setAttribute(AllConstants.ONLINEUSERS_KEY,onlineUserMap);
        }
        HttpSession session= PlatAppUtil.getSession();
        Date lastDate = new Date(session.getLastAccessedTime());
        sysUser.put("SESSION_ID", session.getId());
        sysUser.put("HOST", BrowserUtils.getIpAddr(PlatAppUtil.getRequest()));
        sysUser.put("LASTACCESSTIME",PlatDateTimeUtil.formatDate(lastDate, "yyyy-MM-dd HH:mm:ss"));
        onlineUserMap.put(session.getId(), sysUser);
    }
    
    /**
     * 获取当前在线用户集合
     * @return
     */
    public static Map<String,Map<String,Object>> getOnlineUser(){
        ServletContext application = PlatAppUtil.getSession().getServletContext();
        return (Map<String, Map<String, Object>>) 
                application.getAttribute(AllConstants.ONLINEUSERS_KEY);
    }
    
    /**
     * 提出相同账号登录的用户
     * @param userAccount
     */
    public static void kickoutSameOnlineUser(String userAccount){
      //获取当前登录用户集合
        Map<String,Map<String,Object>> onlineUserMap = PlatAppUtil.getOnlineUser();
        if(onlineUserMap!=null){
            Iterator it = onlineUserMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,Map<String,Object>> entry = (Map.Entry<String,Map<String,Object>>) 
                        it.next();
                String sessionId = entry.getKey();
                Map<String,Object> loginUser = entry.getValue();
                if(loginUser!=null){
                    String SYSUSER_ACCOUNT = (String) loginUser.get("SYSUSER_ACCOUNT");
                    if(SYSUSER_ACCOUNT.equals(userAccount)){
                        if(onlineUserMap.get(sessionId)!=null){
                            onlineUserMap.put(sessionId, null);
                        }
                        HttpSession session = SessionContext.getSession(sessionId);
                        if(session!=null){
                            session.setAttribute(AllConstants.BACKPLAT_USERSESSIONKEY,null);
                            session.setAttribute(AllConstants.BACKPLAT_USERSTATUSKEY,"sameuserlogout");
                        }
                    }
                }
                
            }
        }
    }
    
    /**
     * 获取websocket服务器路径
     * @param request
     * @return
     */
    public static String getWebSocketServerUrl(HttpServletRequest request){
        String path = request.getContextPath();
        String basePath = "ws://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        return basePath+"platwebsocket";
    }
    
    /**
     * 获得请求路径,带上问号的参数
     * 
     * @param request
     * @return
     */
    public static String getRequestPath(HttpServletRequest request) {
        String requestPath = request.getRequestURI() + "?"
                + request.getQueryString();
        if (requestPath.indexOf("&") > -1) {// 去掉其他参数
            requestPath = requestPath.substring(0, requestPath.indexOf("&"));
        }
        requestPath = requestPath
                .substring(request.getContextPath().length() + 1);// 去掉项目路径
        return requestPath;
    }
    
    /**
     * @return the currentDbSource
     */
    public static String getCurrentDbSource() {
        return currentDbSource;
    }

    /**
     * @param currentDbSource the currentDbSource to set
     */
    public static void setCurrentDbSource(String currentDbSource) {
        PlatAppUtil.currentDbSource = currentDbSource;
    }
    /**
     * @return the defaultDbType
     */
    public static String getDefaultDbType() {
        return defaultDbType;
    }

    /**
     * @param defaultDbType the defaultDbType to set
     */
    public static void setDefaultDbType(String defaultDbType) {
        PlatAppUtil.defaultDbType = defaultDbType;
    }

    /**
     * @return the defaultDbSchema
     */
    public static String getDefaultDbSchema() {
        return defaultDbSchema;
    }

    /**
     * @param defaultDbSchema the defaultDbSchema to set
     */
    public static void setDefaultDbSchema(String defaultDbSchema) {
        PlatAppUtil.defaultDbSchema = defaultDbSchema;
    }


    
}

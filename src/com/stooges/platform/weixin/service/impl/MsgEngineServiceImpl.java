/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.weixin.dao.MsgEngineDao;
import com.stooges.platform.weixin.message.resp.Article;
import com.stooges.platform.weixin.service.MsgEngineService;
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.service.UserService;
import com.stooges.platform.weixin.service.WelcomingService;
import com.stooges.platform.weixin.util.Menu;
import com.stooges.platform.weixin.util.MessageUtil;

/**
 * 描述 消息处理引擎业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 09:24:26
 */
@Service("msgEngineService")
public class MsgEngineServiceImpl extends BaseServiceImpl implements MsgEngineService {

    /**
     * 所引入的dao
     */
    @Resource
    private MsgEngineDao dao;
    /**
     * 
     */
    @Resource
    private WelcomingService welcomingService;
    /**
     * 
     */
    @Resource
    private PublicService publicService;
    /**
     * 
     */
    @Resource
    private UserService userService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 处理消息
     * @param request
     * 发送方帐号  FromUserName
     * 发送消息内容 Content
     * @return
     */
    public String invokeMsg(HttpServletRequest request){
        // 调用parseXml方法解析请求消息
        Map<String, String> requestMap;
        String responseMsg = null;
        try {
            requestMap = MessageUtil.parseXml(request);
            // 开发者微信号
            String toUserName = requestMap.get("ToUserName");
            // 用户账号
            //String fromUserName = requestMap.get("FromUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            String handlerMsgInter = dao.getInvokeMsgInter(toUserName, msgType);
            if(StringUtils.isNotEmpty(handlerMsgInter)){
                String beanId = handlerMsgInter.split("[.]")[0];
                String method = handlerMsgInter.split("[.]")[1];
                Object serviceBean = PlatAppUtil.getBean(beanId);
                if (serviceBean != null) {
                    Method invokeMethod = serviceBean.getClass().getDeclaredMethod(method,
                            new Class[] { Map.class,HttpServletRequest.class});
                    responseMsg =  (String) invokeMethod.invoke(serviceBean,
                            new Object[] { requestMap,request});
                }
            }else{
                responseMsg = null;
            }
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseMsg;
    }
    
    /**
     * 处理文本消息接口
     * @param requestMap 
     *  发送方帐号 fromUserName
     *  开发者微信号 toUserName
     *  消息类型 toUserName
     * @param request
     * @return
     */
    public String handleTextMsg(Map<String, String> requestMap,HttpServletRequest request){
        String fromUserName = requestMap.get("FromUserName");
        //获取公众原始ID
        String toUserName = requestMap.get("ToUserName");
        return MessageUtil.getTextMessageXml(fromUserName, toUserName, "您发送的是文本消息!");
    }
    
    /**
     * 处理图片消息接口
     * @param requestMap
     * @param request
     * @return
     */
    public String handleImgMsg(Map<String, String> requestMap,HttpServletRequest request){
        String fromUserName = requestMap.get("FromUserName");
        String toUserName = requestMap.get("ToUserName");
        Article article = new Article();
        article.setTitle("Cocachina");
        article.setDescription("房贷收紧开发商的发电技术开发三顿饭都是反对数据库");
        article.setPicUrl("http://huyubisheng.imwork.net/szfoa/tx.jpg");
        article.setUrl("https://www.baidu.com");
        Article article2 = new Article();
        article2.setTitle("第二篇文章");
        article2.setDescription("第二批文章分的时间里发生");
        article2.setPicUrl("http://huyubisheng.imwork.net/szfoa/tx.jpg");
        article2.setUrl("https://www.baidu.com");
        Article article3 = new Article();
        article3.setTitle("第三篇文章");
        article3.setDescription("第第三篇文章分的时间里发生");
        article3.setPicUrl("http://huyubisheng.imwork.net/szfoa/tx.jpg");
        article3.setUrl("https://www.baidu.com");
        List<Article> articleList = new ArrayList<Article>();
        articleList.add(article);
        articleList.add(article2);
        articleList.add(article3);
        return MessageUtil.getSendNewsMessageXml(fromUserName, toUserName, articleList);
    }
    
    /**
     * 处理事件消息接口
     * @param requestMap
     * @param request
     * @return
     */
    public String handleEventMsg(Map<String, String> requestMap,HttpServletRequest request){
        String fromUserName = requestMap.get("FromUserName");
        String pubSourceId = requestMap.get("ToUserName");
        // 事件类型
        String eventType = requestMap.get("Event");
        // 订阅
        if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
            String welComeContent = welcomingService.getWelcomingContent(pubSourceId);
            userService.saveUserInfo(fromUserName, pubSourceId);
            if(StringUtils.isNotEmpty(welComeContent)){
                return MessageUtil.getTextMessageXml(fromUserName, pubSourceId,welComeContent);
            }
        }else if(eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)){
            Map<String,Object> publicInfo = dao.getRecord("PLAT_WEIXIN_PUBLIC",
                    new String[]{"PUBLIC_SOURCEID"}, new Object[]{pubSourceId});
            userService.deleteRecord("PLAT_WEIXIN_USER",new String[]{"OPEN_ID","PUBLIC_ID"},
                    new Object[]{fromUserName,publicInfo.get("PUBLIC_ID").toString()});
        }else if(eventType.equals(MessageUtil.EVENT_TYPE_CLICK)){
            // 事件KEY值，与创建菜单时的key值对应
            String eventKey = requestMap.get("EventKey");
            Map<String,Object> publicInfo = dao.getRecord("PLAT_WEIXIN_PUBLIC",
                    new String[]{"PUBLIC_SOURCEID"}, new Object[]{pubSourceId});
            String publicId = (String) publicInfo.get("PUBLIC_ID");
            Map<String,Object> menu = dao.getRecord("PLAT_WEIXIN_MENU",
                    new String[]{"MENU_TYPE","MENU_KEY","MENU_PUBID"},
                    new Object[]{Menu.TYPE_CLICK,eventKey,publicId});
            if(menu!=null){
                String MENU_INTER = (String) menu.get("MENU_INTER");
                if(StringUtils.isNotEmpty(MENU_INTER)){
                    String beanId = MENU_INTER.split("[.]")[0];
                    String method = MENU_INTER.split("[.]")[1];
                    Object serviceBean = PlatAppUtil.getBean(beanId);
                    if (serviceBean != null) {
                        try {
                            Method invokeMethod = serviceBean.getClass().getDeclaredMethod(method,
                                    new Class[] { Map.class,HttpServletRequest.class});
                            return (String) invokeMethod.invoke(serviceBean,
                                    new Object[] { requestMap,request});
                        } catch (NoSuchMethodException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (SecurityException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
            
        }
        return null;
    }
    
    /**
     * 点击菜单处理例子接口
     * @param requestMap
     * @param request
     * @return
     */
    public String demoMenuClick(Map<String, String> requestMap,HttpServletRequest request){
        String fromUserName = requestMap.get("FromUserName");
        //获取公众原始ID
        String toUserName = requestMap.get("ToUserName");
        String eventKey = requestMap.get("EventKey");
        return MessageUtil.getTextMessageXml(fromUserName, toUserName, 
                "您点击了KEY为"+eventKey+"的按钮!");
    }
    
}

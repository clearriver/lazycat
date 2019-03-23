/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.service.BaseService;

/**
 * 描述 消息处理引擎业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 09:24:26
 */
public interface MsgEngineService extends BaseService {
    /**
     * 处理消息
     * @param request
     * @return
     */
    public String invokeMsg(HttpServletRequest request);
    /**
     * 处理文本消息接口
     * @param requestMap
     * @param request
     * @return
     */
    public String handleTextMsg(Map<String, String> requestMap,HttpServletRequest request);
    /**
     * 处理图片消息接口
     * @param requestMap
     * @param request
     * @return
     */
    public String handleImgMsg(Map<String, String> requestMap,HttpServletRequest request);
    /**
     * 处理事件消息接口
     * @param requestMap
     * @param request
     * @return
     */
    public String handleEventMsg(Map<String, String> requestMap,HttpServletRequest request);
    
    /**
     * 点击菜单处理例子接口
     * @param requestMap
     * @param request
     * @return
     */
    public String demoMenuClick(Map<String, String> requestMap,HttpServletRequest request);
}

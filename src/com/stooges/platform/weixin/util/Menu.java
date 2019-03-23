/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 胡裕
 *
 * 
 */
public class Menu {
    
    /**
     * 类型:点击
     */
    public static final String TYPE_CLICK = "click";
    /**
     * 类型:跳转URL
     */
    public static final String TYPE_VIEW = "view";
    /**
     * 类型:扫码推事件
     */
    public static final String TYPE_SCANPUSH = "scancode_push";
    /**
     * 类型:扫码带提示
     */
    public static final String TYPE_SCANMSG = "scancode_waitmsg";
    /**
     * 类型:调用摄像头
     */
    public static final String TYPE_SYSPHOTO = "pic_sysphoto";
    /**
     * 类型:选择图片
     */
    public static final String TYPE_PIC = "pic_photo_or_album";
    /**
     * 类型:微信相册
     */
    public static final String TYPE_WEIXINPIC = "pic_weixin";
    /**
     * 类型:地理位置
     */
    public static final String TYPE_LOC  = "location_select";
    /**
     * 类型:下发媒体信息
     */
    public static final String TYPE_MEDIA = "media_id";
    /**
     * 类型:跳转素材
     */
    public static final String TYPE_VIEWLIMITED = "view_limited";
    /**
     * 
     */
    private List<Map<String,Object>> button = new ArrayList<Map<String,Object>>();
    /**
     * @return the button
     */
    public List<Map<String, Object>> getButton() {
        return button;
    }
    /**
     * @param button the button to set
     */
    public void setButton(List<Map<String, Object>> button) {
        this.button = button;
    }
}

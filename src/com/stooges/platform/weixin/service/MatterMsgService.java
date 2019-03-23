/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 素材消息群发业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-05 13:48:09
 */
public interface MatterMsgService extends BaseService {
    /**
     * 文本消息
     */
    public static final String TYPE_TEXT = "text";
    /**
     * 图文消息
     */
    public static final String TYPE_NEWS = "news";
    /**
     * 语言消息
     */
    public static final String TYPE_VOICE = "voice";
    /**
     * 图片消息
     */
    public static final String TYPE_IMAGE = "image";
    /**
     * 视频消息
     */
    public static final String TYPE_VIDEO = "video";
    /**
     * 根据类型和公众号ID获取素材列表
     * @param typeAndPublicId
     * @return
     */
    public List<Map<String,Object>> findMatterList(String typeAndPublicId);
    /**
     * 进行消息的群发
     * @param matterMsg
     */
    public Map<String,Object> saveMatterMsg(Map<String,Object> matterMsg);
}

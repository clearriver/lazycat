/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service;

import com.stooges.core.service.BaseService;

/**
 * 描述 文本素材业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 14:31:15
 */
public interface TextMatterService extends BaseService {
    /**
     * 发送消息给用户
     * @param openId
     * @param sourceId
     */
    public String sendMsgToUsers(String[] openIds,String sourceId,String content);
    /**
     * 发送消息给所有用户
     * @param publicId
     * @param content
     * @return
     */
    public String sendMsgToAllUsers(String publicId,String content);
    /**
     * 发送消息给标签下用户
     * @param tagId
     * @param publicId
     * @param content
     * @return
     */
    public String sendMsgToTagUsers(String tagId,String publicId,String content);
}

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
 * 描述 媒体素材业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-27 16:24:07
 */
public interface MediaMatterService extends BaseService {
    /**
     * 保存其它类型素材
     * @param mediaMatter
     * @param mediaType
     * @return
     */
    public Map<String,Object> saveOtherMatter(Map<String,Object> mediaMatter,String mediaType);
    /**
     * 删除素材信息
     * @param token
     * @param mediaId
     * @return
     */
    public boolean deleteMatter(String token,String mediaId);
    /**
     * 删除素材信息
     * @param matterIds
     */
    public void deleteMatters(String[] matterIds);
    /**
     * 获取微信服务器的素材列表
     * @param sourceId
     * @param matterType
     * @return
     */
    public List<Map> findWeixinMatterList(String sourceId,String matterType); 
    /**
     * 获取可选的素材类型列表
     * @param param
     * @return
     */
    public List<Map<String,Object>> findMediaTypeList(String param);
    /**
     * 获取素材列表
     * @param matterType
     * @return
     */
    public List<Map<String,Object>> findMatterList(String typeAndPublicId);
    
    /**
     * 发送媒体素材给用户
     * @param openId
     * @param sourceId
     */
    public String sendMsgToUsers(String[] openIds,String sourceId,
            String mediaId,String mediaType);
    /**
     * 发送消息给用户
     * @param tagId
     * @param publicId
     * @param mediaId
     * @param mediaType
     * @return
     */
    public String sendMsgToUsers(String tagId,String publicId,
            String mediaId,String mediaType);
    /**
     * 发送视频消息给用户
     * @param tagId
     * @param MEDIAMATTER_ID
     * @return
     */
    public String sendVideoMsgToUsers(String tagId,String MEDIAMATTER_ID);
    /**
     * 发送视频消息给用户
     * @param openIds
     * @param MEDIAMATTER_ID
     * @return
     */
    public String sendVideoMsgToUsers(String[] openIds,String MEDIAMATTER_ID);
    
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月1日 下午3:56:16
     * @param type
     * @param PublicId
     * @return
     */
    public List<Map<String,Object>> findMatterListByType(String type,String publicId);
}

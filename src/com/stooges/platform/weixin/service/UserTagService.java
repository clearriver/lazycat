/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 用户标签业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-29 11:11:42
 */
public interface UserTagService extends BaseService {
    /**
     * 获取标签列表
     * @param queryParamJson
     * @return
     */
    public List<Map<String,Object>> findTagList(String publicId);
    /**
     * 创建微信用户标签
     * @param publicId
     * @param tagName
     * @return
     */
    public String createWeixinUserTag(String publicId,String tagName);
    /**
     * 更新微信用户标签
     * @param publicId
     * @param weixinTagId
     * @param tagName
     * @return
     */
    public String updateWeixinUserTag(String publicId,int weixinTagId,String tagName);
    /**
     * 删除微信用户标签
     * @param publicId
     * @param weixinTagId
     * @return
     */
    public boolean delWeixinUserTag(String publicId,int weixinTagId);
    /**
     * 判断是否存在该标签
     * @param userTagName
     * @param pubId
     * @param userTagId
     * @return
     */
    public boolean isExists(String userTagName,String pubId,String userTagId);
    /**
     * 新增或者保存用户标签
     * @param userTagInfo
     * @return
     */
    public Map<String,Object> saveOrUpdateTag(Map<String,Object> userTagInfo);
    /**
     * 删除用户标签
     * @param userTagId
     * @return
     */
    public boolean deleteTag(String userTagId);
    /**
     * 为用户批量打上标签
     * @param userOpenIds
     * @param tagId
     * @return
     */
    public boolean signUsersTag(String[] userOpenIds,int tagId,String publicId);
    /**
     * 批量取消用户的标签
     * @param userOpenIds
     * @param tagId
     * @param publicId
     * @return
     */
    public boolean unsignUsersTag(String[] userOpenIds,int tagId,String publicId);
    
    /**
     * 获取标签列表
     * @param queryParamJson
     * @return
     */
    public List<Map<String,Object>> findSelectTagList(String publicId);
    /**
     * 获取微信的标签MAP
     * @param publicId
     * @return
     */
    public Map<Integer,String> getWeixinTagMap(String publicId);
    /**
     * 批量打上标签
     * @param USER_IDS
     * @param USER_TAGIDS
     */
    public void saveSignUsersTags(String USER_IDS,String USER_TAGIDS);
    /**
     * 获取公众号微信的标签列表
     * @param publicId
     * @return
     */
    public List<Map> findWeixinTagList(String publicId);
    /**
     * 和微信的标签进行同步操作
     * @param publicId
     */
    public void refreshWeixinTag(String publicId);
    
    /**
     * 获取本地存在的微信标签ID
     * @param userId
     * @return
     */
    public List<String> findLocalUserWeixinTagId(String userId);
    
    /**
     * 获取自动补全的数据
     * @param filter
     * @return
     */
    public List<Map<String,Object>> findAutoTagUser(SqlFilter filter);
    
    /**
     * 获取树形JSON
     * @param params
     * @return
     */
    public String getTreeJson(Map<String,Object> params);
}

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
 * 描述 微信用户业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 16:30:21
 */
public interface UserService extends BaseService {
    /**
     * 状态:正常
     */
    public static final int STATUS_NORMAL = 1;
    /**
     * 状态:拉黑
     */
    public static final int STATUS_BLACK = -1;
    /**
     * 获取用户信息
     * @param openId
     * @param sourceId
     * @return
     */
    public Map<String,Object> getUserInfo(String openId,String sourceId);
    /**
     * 保存用户信息
     * @param openId
     * @param sourceId
     */
    public void saveUserInfo(String openId,String sourceId);
    /**
     * 获取所有关注用户列表
     * @return
     */
    public List<Map<String,Object>> findUserList(String publicId);
    /**
     * 获取用户列表
     * @param publicId
     * @param tagId
     * @return
     */
    public List<Map<String,Object>> findUserList(String publicId,String tagId);
    /**
     * 发送邮件提醒给用户
     * @param subject
     * @param content
     * @param sourceId
     */
    public void sendEmailToUsers(String subject,String content,String sourceId);
    /**
     * 更新用户的微信备注
     * @param userId
     */
    public void updateUserRemark(String userId,String remark);
    
    /**
     * 根据filter和配置信息获取数据列表
     * @param filter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findList(SqlFilter filter,Map<String,Object> fieldInfo);
    
    /**
     * 获取用户被打的标签IDS
     * @param userId
     * @return
     */
    public List<String> getUserTagIds(String userId);
    /**
     * 更新用户的标签
     * @param userId
     * @param tagIds
     */
    public void updateUserTags(String userId,String tagIds);
    /**
     * 获取公众号关注的用户OPENIDS
     * @param publicId
     * @return
     */
    public List<String> getWeixinOpenIds(String publicId);
    /**
     * 重新刷新微信用户信息
     * @param publicId
     */
    public void refreshWeixinUserInfo(String publicId);
    /**
     * 同步微信用户标签和用户信息
     * @param publicId
     */
    public void refreshWeixinUserAndTag(String publicId);
    /**
     * 拉黑微信用户
     * @param publicId
     * @param openIds
     * @return
     */
    public boolean batchWeixinBlackUsers(String publicId,String[] openIds);
    /**
     * 取消拉黑微信用户
     * @param publicId
     * @param openIds
     * @return
     */
    public boolean unWeixinBlackUsers(String publicId,String[] openIds);
    /**
     * 更新用户的状态
     * @param publicId
     * @param userIds
     * @param status
     */
    public void updateUserStatus(String publicId,String userIds,int status);
    /**
     * 根据filter获取网格项目
     * @param sqlFilter
     * @return
     */
    public List<Map<String,Object>> findGridItemList(SqlFilter sqlFilter);
    
    /**
     * 获取用户的openId列表
     * @param userIds
     * @return
     */
    public List<String> findUserOpenIdList(String userIds);
    /**
     * 获取用户的IDS
     * @param publicId
     * @return
     */
    public List<String> findUserIds(String publicId);
    /**
     * 根据公众ID获取openIds
     * @param publicId
     * @return
     */
    public List<String> findUserOpenIdsByPublicId(String publicId);
    
}

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
 * 描述 公众号业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-18 15:51:54
 */
public interface PublicService extends BaseService {
    
    /**
     * 
     * 描述 根据查询参数JSON获取数据列表
     * @created 2016年3月27日 上午11:16:25
     * @param queryParamsJson
     * @return
     */
    public List<Map<String,Object>> findList(String queryParamsJson);
    /**
     * 获取素材数据列表
     * @param matterType
     * @return
     */
    public List<Map<String,Object>> findMatterList(String matterType);
    /**
     * 根据原始ID获取token
     * @param sourceId
     * @return
     */
    public String getToken(String sourceId);
    /**
     * 根据公众号ID获取token
     * @param publicId
     * @return
     */
    public String getTokenByPublicId(String publicId);
    
    /**
     * 获取token
     * @param appId
     * @param secret
     * @return
     */
    public String getToken(String appId,String secret);
    /**
     * 获取最早配置的公众号ID
     * @return
     */
    public String firstPublicId();
    
}

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
 * 描述 图文素材业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2018-02-22 16:09:23
 */
public interface TextPictureMatterService extends BaseService {

    /**
     * 描述
     * @author 李俊
     * @created 2018年2月22日 下午4:12:20
     * @param publicId
     * @return
     */
    public List<Map<String, Object>> findListByPublicId(String publicId);
    
}

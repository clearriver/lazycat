/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.dao;

import com.stooges.core.dao.BaseDao;

/**
 * 
 * 描述 消息处理引擎业务相关dao
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 09:24:26
 */
public interface MsgEngineDao extends BaseDao {
    /**
     * 获取处理公众号的消息接口
     * @param publicSourceId
     * @param msgType
     * @return
     */
    public String getInvokeMsgInter(String publicSourceId,String msgType);
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.weixin;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.weixin.service.TextMatterService;

/**
 * @author 胡裕
 *
 * 
 */
public class TextMatterTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private TextMatterService textMatterService;
    
    /**
     * 
     */
    @Test
    public void sendMsgToAllUser(){
        String result = textMatterService.sendMsgToAllUsers
                ("402882a16068a9c4016068ae69c90066","测试公众号内容");
        System.out.println(result);
    }
    
    @Test
    public void sendMsgToTagUsers(){
        String result = textMatterService.sendMsgToTagUsers("106",
                "402882a16068a9c4016068ae69c90066", "测试公众号内容");
        System.out.println(result);
    }
}

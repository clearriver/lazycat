/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.weixin;

import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.weixin.service.UserService;

/**
 * @author 胡裕
 *
 * 
 */
public class UserTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private UserService userService;
    
    /**
     * 
     */
    @Test
    public void getUserInfo(){
        Map<String,Object> userInfo = userService.getUserInfo(
                "oBJDjsyhsuIN9tVHWD_n23J1bpZA", "gh_e898cf9f6221");
        //System.out.println(JSON.toJSONString(userInfo));
    }
    
    /**
     * 
     */
    @Test
    public void getWeixinOpenIds(){
        userService.getWeixinOpenIds("402882a16068a9c4016068ae69c90066");
    }
    
    /**
     * 
     */
    @Test
    public void refreshWeixinUserInfo(){
        userService.refreshWeixinUserInfo("402882a16068a9c4016068ae69c90066");
    }
    
}

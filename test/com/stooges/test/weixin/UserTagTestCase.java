/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.weixin;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.weixin.service.UserTagService;

/**
 * @author 胡裕
 *
 * 
 */
public class UserTagTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private UserTagService userTagService;
    
    /**
     * 
     */
    @Test
    public void createUserTag(){
        String tagId = userTagService.createWeixinUserTag("402882a16068a9c4016068ae69c90066"
                ,"测试标签3");
        System.out.println("标签ID："+tagId);
    }
    
    /**
     * 
     */
    @Test
    public void findWeixinTagList(){
        List<Map> list = userTagService.findWeixinTagList("402882a16068a9c4016068ae69c90066");
        System.out.println(JSON.toJSONString(list));
    }
    
    /**
     * 
     */
    @Test
    public void getWeixinTagMap(){
        Map<Integer,String> tagMap = userTagService.
                getWeixinTagMap("402882a16068a9c4016068ae69c90066");
        System.out.println(JSON.toJSONString(tagMap));
    }
    
    /**
     * 
     */
    @Test
    public void delWeixinUserTag(){
        userTagService.delWeixinUserTag("402882a16068a9c4016068ae69c90066",100);
        userTagService.delWeixinUserTag("402882a16068a9c4016068ae69c90066",101);
        userTagService.delWeixinUserTag("402882a16068a9c4016068ae69c90066",102);
    }
    
    /**
     * 
     */
    @Test
    public void refreshWeixinTag(){
        userTagService.refreshWeixinTag("402882a16068a9c4016068ae69c90066");
    }
}

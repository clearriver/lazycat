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
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.util.WeixinCoreUtil;

/**
 * @author 胡裕
 *
 * 
 */
public class PublicServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private PublicService publicService;
    
    /**
     * 
     */
    @Test
    public void testAddMater(){
        String token = publicService.getToken("gh_e898cf9f6221");
        /*String mediaId = WeixinCoreUtil.addMaterials(token,
                WeixinCoreUtil.MATTER_IMG,"http://localhost/szfoa/tx.jpg");*/
        Map<String,Object> result = WeixinCoreUtil.addVideoMatter(token, "http://localhost/szfoa/my.mp4",
                "测试标题", "视频描述");
        System.out.println("结果:"+JSON.toJSONString(result));
    }
    
    /**
     * 
     */
    @Test
    public void uploadImg(){
        String token = publicService.getToken("gh_e898cf9f6221");
        String url = WeixinCoreUtil.uploadImg(token,
                "http://localhost/szfoa/touxiang1.jpg");
        System.out.println(url);
    }
}

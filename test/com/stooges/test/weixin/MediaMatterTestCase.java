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
import com.stooges.platform.weixin.service.MatterMsgService;
import com.stooges.platform.weixin.service.MediaMatterService;
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.util.WeixinCoreUtil;

/**
 * @author 胡裕
 *
 * 
 */
public class MediaMatterTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private MediaMatterService mediaMatterService;
    /**
     * 
     */
    @Resource
    private PublicService publicService;
    
    /**
     * 
     */
    @Test
    public void deleteMatter(){
        String token = publicService.getToken("gh_e898cf9f6221");
        boolean result = mediaMatterService.deleteMatter(token,
                "mU35X6htuBvUi6rNDXXA2wKYQy5_tscC_FtmvLGaKL8");
        System.out.println("结果："+result);
    }
    
    /**
     * 
     */
    @Test
    public void findWeixinMatterList(){
        List<Map> list = mediaMatterService.findWeixinMatterList("gh_e898cf9f6221",
                WeixinCoreUtil.MATTER_VIDEO);
        System.out.println("字符串:"+JSON.toJSONString(list));
    }
    
    /**
     * 
     */
    @Test
    public void sendMsgToUsers(){
        String result = mediaMatterService.sendMsgToUsers("106","402882a16068a9c4016068ae69c90066",
                "mU35X6htuBvUi6rNDXXA28U9Fhodyf0TLJgtwPh0-yI", MatterMsgService.TYPE_VOICE);
        System.out.println("结果:"+result);
    }
    
    
    /**
     * 
     */
    @Test
    public void sendVideoMsgToUsers(){
        String result = mediaMatterService.sendVideoMsgToUsers("106","4028d081609f9f5401609fa6267b0029");
        System.out.println("结果:"+result);
    }
}

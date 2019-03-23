/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.platform.weixin.dao.TextMatterDao;
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.service.TextMatterService;
import com.stooges.platform.weixin.service.UserService;

/**
 * 描述 文本素材业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 14:31:15
 */
@Service("textMatterService")
public class TextMatterServiceImpl extends BaseServiceImpl implements TextMatterService {

    /**
     * 所引入的dao
     */
    @Resource
    private TextMatterDao dao;
    /**
     * 
     */
    @Resource
    private UserService userService;
    /**
     * 
     */
    @Resource
    private PublicService publicService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 发送消息给用户
     * @param openId
     * @param sourceId
     */
    public String sendMsgToUsers(String[] openIds,String sourceId,String content){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("touser", openIds);
        map.put("msgtype", "text");
        Map<String,Object> text = new HashMap<String,Object>();
        text.put("content",content);
        map.put("text", text);
        String body = JSON.toJSONString(map);
        String access_token = publicService.getToken(sourceId);
        String httpUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token="+access_token;
        String result = PlatHttpUtil.postParams(httpUrl, body);
        return result;
    }
    
    /**
     * 发送消息给所有用户
     * @param publicId
     * @param content
     * @return
     */
    public String sendMsgToAllUsers(String publicId,String content){
        Map<String,Object> publicInfo = this.getRecord("PLAT_WEIXIN_PUBLIC",
                new String[]{"PUBLIC_ID"}, new Object[]{publicId});
        String sourceId = (String) publicInfo.get("PUBLIC_SOURCEID");
        List<String> openIds = userService.findUserOpenIdsByPublicId(publicId);
        return this.sendMsgToUsers(openIds.toArray(new String[openIds.size()]), sourceId, content);
    }
    
    /**
     * 发送消息给标签下用户
     * @param tagId
     * @param publicId
     * @param content
     * @return
     */
    public String sendMsgToTagUsers(String tagId,String publicId,String content){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msgtype", "text");
        Map<String,Object> filter = new HashMap<String,Object>();
        filter.put("is_to_all", true);
        filter.put("tag_id",Integer.parseInt(tagId));
        map.put("filter", filter);
        Map<String,Object> text = new HashMap<String,Object>();
        text.put("content",content);
        map.put("text", text);
        String body = JSON.toJSONString(map);
        String access_token = publicService.getTokenByPublicId(publicId);
        String httpUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+access_token;
        String result = PlatHttpUtil.postParams(httpUrl, body);
        return result;
    }
    
  
}
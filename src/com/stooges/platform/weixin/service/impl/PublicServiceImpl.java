/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.weixin.dao.PublicDao;
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.util.PublicToken;
import com.stooges.platform.weixin.util.WeixinCoreUtil;

/**
 * 描述 公众号业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-18 15:51:54
 */
@Service("publicService")
public class PublicServiceImpl extends BaseServiceImpl implements PublicService {

    /**
     * 所引入的dao
     */
    @Resource
    private PublicDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 
     * 描述 根据查询参数JSON获取数据列表
     * @created 2016年3月27日 上午11:16:25
     * @param queryParamsJson
     * @return
     */
    public List<Map<String,Object>> findList(String queryParamsJson){
        StringBuffer sql = new StringBuffer("SELECT T.PUBLIC_ID AS VALUE,T.PUBLIC_NAME AS LABEL ");
        sql.append(" FROM PLAT_WEIXIN_PUBLIC T");
        sql.append(" ORDER BY T.PUBLIC_CREATETIME DESC ");
        return dao.findBySql(sql.toString(),null, null);
    }
    
    /**
     * 获取素材数据列表
     * @param matterType
     * @return
     */
    public List<Map<String,Object>> findMatterList(String matterType){
        StringBuffer sql = null;
        if(StringUtils.isNotEmpty(matterType)){
            if("text".equals(matterType)){
                sql = new StringBuffer("SELECT T.TEXTMATTER_ID AS VALUE,T.TEXTMATTER_NAME AS LABEL ");
                sql.append(" FROM PLAT_WEIXIN_TEXTMATTER T");
                sql.append(" ORDER BY T.TEXTMATTER_TIME DESC ");
            }
            return dao.findBySql(sql.toString(), null, null);
        }else{
            return null;
        }
    }
    
    /**
     * 根据原始ID获取token
     * @param sourceId
     * @return
     */
    public String getToken(String sourceId){
        Map<String,PublicToken> tokenMap = WeixinCoreUtil.tokenTimeOut;
        Date nowDate = new Date();
        if(tokenMap.get(sourceId)!=null){
            PublicToken publicToken = tokenMap.get(sourceId);
            Date timeoutDate = publicToken.getTimeoutDate();
            if(timeoutDate.before(nowDate)){
                return this.getPublicToken(sourceId, nowDate);
            }else{
                return publicToken.getToken();
            }
        }else{
            return this.getPublicToken(sourceId, nowDate);
        }
        
    }
    
    /**
     * 获取token
     * @param sourceId
     * @param nowDate
     * @return
     */
    private String getPublicToken(String sourceId,Date nowDate){
        Map<String,PublicToken> tokenMap = WeixinCoreUtil.tokenTimeOut;
        Map<String,Object> publicInfo = dao.getRecord("PLAT_WEIXIN_PUBLIC",
                new String[]{"PUBLIC_SOURCEID"},new Object[]{sourceId});
        String PUBLIC_APPID = (String) publicInfo.get("PUBLIC_APPID");
        String PUBLIC_SECRET = (String) publicInfo.get("PUBLIC_SECRET");
        String token = this.getToken(PUBLIC_APPID, PUBLIC_SECRET);
        PublicToken publicToken = new PublicToken();
        publicToken.setTimeoutDate(PlatDateTimeUtil.getNextTime(nowDate, Calendar.MINUTE, 90));
        publicToken.setToken(token);
        tokenMap.put(sourceId, publicToken);
        return token;
    }
    
    /**
     * 获取token
     * @param appId
     * @param secret
     * @return
     */
    public String getToken(String appId,String secret){
        DefaultHttpClient httpclient = new DefaultHttpClient(
                new ThreadSafeClientConnManager());
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
        String httpUrl = url+"&appid="+appId+"&secret="+secret;
        Map<String,Object> params = new HashMap<String,Object>();
        String jsonResult = PlatHttpUtil.sendPostParams(httpUrl, params, httpclient);
        httpclient.getConnectionManager().shutdown();
        Map result = JSON.parseObject(jsonResult, Map.class);
        return (String) result.get("access_token");
    }
    
    /**
     * 获取最早配置的公众号ID
     * @return
     */
    public String firstPublicId(){
        return dao.firstPublicId();
    }
    
    /**
     * 根据公众号ID获取token
     * @param publicId
     * @return
     */
    public String getTokenByPublicId(String publicId){
        Map<String,Object> publicInfo = dao.getRecord("PLAT_WEIXIN_PUBLIC",
                new String[]{"PUBLIC_ID"}, new Object[]{publicId});
        String sourceId = (String) publicInfo.get("PUBLIC_SOURCEID");
        return this.getToken(sourceId);
    }
    
  
}

/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.service.BaseService;

/**
 * 描述 文章信息业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-11 10:47:32
 */
public interface ArticleService extends BaseService {
    /**
     * 文章类型:新闻
     */
    public static final String TYPE_NEWS = "1";
    /**
     * 文章类型:图库
     */
    public static final String TYPE_PICS = "2";
    /**
     * 文章类型:视频
     */
    public static final String TYPE_VIDEO = "3";
    /**
     * 文章类型:下载
     */
    public static final String TYPE_DOWNLOAD = "4";
    /**
     * 保存文章信息
     * @param articleInfo
     * @return
     */
    public Map<String,Object> saveCascadeImg(Map<String,Object> articleInfo);
    /**
     * 获取文章类型列表
     * @param paramJson
     * @return
     */
    public List<Map<String,Object>> findTypeList(String paramJson);
    /**
     * 获取后台通知公告数据
     * @param request
     * @return
     */
    public Map<String,Object> getPlatNoticeInfo(HttpServletRequest request);
    /**
     * 更新文章是否置顶
     * @param articleIds
     * @param isTop
     */
    public void updateIsTop(String articleIds,String isTop);
    /**
     * 获取文章静态结果代码
     * @param ARTICLE_SIGN
     * @return
     */
    public String getArticeHtmlCode(String ARTICLE_SIGN,HttpServletRequest request);
    /**
     * 删除生成文章的静态代码
     * @param ARTICLE_ID
     */
    public void deleteGenHtmlCode(String ARTICLE_ID);
    /**
     * 获取后台门户新闻列表数据
     * @param param
     * @return
     */
    public List<Map<String,Object>> findPortalPicNews(String param);
}

/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service.impl;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.PagingBean;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.cms.dao.ArticleDao;
import com.stooges.platform.cms.service.ArticleService;

/**
 * 描述 文章信息业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-11 10:47:32
 */
@Service("articleService")
public class ArticleServiceImpl extends BaseServiceImpl implements ArticleService {

    /**
     * 所引入的dao
     */
    @Resource
    private ArticleDao dao;
    /**
     * 
     */
    @Resource
    private FileAttachService fileAttachService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 保存文章信息
     * @param articleInfo
     * @return
     */
    public Map<String,Object> saveCascadeImg(Map<String,Object> articleInfo){
        String oldArticleId = (String) articleInfo.get("ARTICLE_ID");
        if(StringUtils.isEmpty(oldArticleId)){
            int ARTICLE_SIGN = dao.getNewSignId();
            articleInfo.put("ARTICLE_SIGN", ARTICLE_SIGN);
            int ARTICLE_SN = dao.getNewArticleSn();
            articleInfo.put("ARTICLE_SN", ARTICLE_SN);
            //获取当前登录用户
            Map<String,Object> curLoginUser = PlatAppUtil.getBackPlatLoginUser();
            articleInfo.put("ARTICLE_PUBNAME", curLoginUser.get("SYSUSER_NAME"));
            articleInfo.put("ARTICLE_PUBID", curLoginUser.get("SYSUSER_ID"));
            articleInfo.put("ARTICLE_CLICKNUM",0);
            articleInfo.put("ARTICLE_EXTERNAL",-1);
            articleInfo.put("ARTICLE_ISTOP",-1);
        }
        articleInfo = dao.saveOrUpdate("PLAT_CMS_ARTICLE",
                articleInfo,AllConstants.IDGENERATOR_UUID,null);
        String ARTICLE_ID = (String) articleInfo.get("ARTICLE_ID");
        String PHOTO_JSON = (String) articleInfo.get("PHOTO_JSON");
        fileAttachService.saveFileAttachs(PHOTO_JSON,"PLAT_CMS_ARTICLE",ARTICLE_ID,null);
        return articleInfo;
    }
    
    /**
     * 获取文章类型列表
     * @param paramJson
     * @return
     */
    public List<Map<String,Object>> findTypeList(String paramJson){
        StringBuffer sql = new StringBuffer("SELECT T.DIC_VALUE AS VALUE,T.DIC_NAME AS LABEL ");
        sql.append(" FROM PLAT_SYSTEM_DICTIONARY T");
        sql.append(" WHERE T.DIC_DICTYPE_CODE=? ");
        sql.append(" ORDER BY T.DIC_SN ").append("ASC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), new Object[]{"articletype"}, null);
        for(Map<String,Object> map:list){
            map.put("GROUP_FONT", "fa fa-asterisk");
        }
        Map<String,Object> allType = new HashMap<String,Object>();
        allType.put("GROUP_FONT", "fa fa-home");
        allType.put("LABEL","全部类型");
        allType.put("VALUE","0");
        list.add(0, allType);
        return list;
    }
    
    /**
     * 获取后台通知公告数据
     * @param request
     * @return
     */
    public Map<String,Object> getPlatNoticeInfo(HttpServletRequest request){
        String ARTICLE_SIGN = request.getParameter("ARTICLE_SIGN");
        Map<String,Object> article = dao.getRecord("PLAT_CMS_ARTICLE",
                new String[]{"ARTICLE_SIGN"},new Object[]{ARTICLE_SIGN});
        return article;
    }
    
    /**
     * 更新文章是否置顶
     * @param articleIds
     * @param isTop
     */
    public void updateIsTop(String articleIds,String isTop){
        StringBuffer sql = new StringBuffer("UPDATE PLAT_CMS_ARTICLE");
        sql.append(" SET ARTICLE_ISTOP=? WHERE ARTICLE_ID IN ");
        sql.append(PlatStringUtil.getSqlInCondition(articleIds));
        dao.executeSql(sql.toString(), new Object[]{isTop});
    }
    
    /**
     * 获取文章静态结果代码
     * @param ARTICLE_SIGN
     * @return
     */
    public String getArticeHtmlCode(String ARTICLE_SIGN,HttpServletRequest request){
        Map<String,Object> article = dao.getRecord("PLAT_CMS_ARTICLE",
                new String[]{"ARTICLE_SIGN"},new Object[]{ARTICLE_SIGN});
        String ARTICLE_TPLID = (String) article.get("ARTICLE_TPLID");
        Map<String,Object> template = dao.getRecord("PLAT_CMS_TEMPLATE",
                new String[]{"TEMPLATE_ID"},new Object[]{ARTICLE_TPLID});
        String TEMPLATE_DATEINTER = (String) template.get("TEMPLATE_DATEINTER");
        Map<String,Object> resultDatas = new HashMap<String,Object>();
        if(StringUtils.isNotEmpty(TEMPLATE_DATEINTER)){
            String beanId = TEMPLATE_DATEINTER.split("[.]")[0];
            String method = TEMPLATE_DATEINTER.split("[.]")[1];
            Object serviceBean = PlatAppUtil.getBean(beanId);
            if (serviceBean != null) {
                Method invokeMethod;
                try {
                    invokeMethod = serviceBean.getClass().getDeclaredMethod(method,
                            new Class[] { HttpServletRequest.class });
                    resultDatas = (Map<String,Object>) invokeMethod.invoke(serviceBean,
                            new Object[] { request });
                } catch (Exception e) {
                    PlatLogUtil.printStackTrace(e);
                }
            }
        }
        String TEMPLATE_CODE = (String) template.get("TEMPLATE_CODE");
        String resultHtml = PlatStringUtil.getFreeMarkResult(TEMPLATE_CODE, resultDatas);
        return resultHtml;
    }
    
    /**
     * 删除生成文章的静态代码
     * @param ARTICLE_SIGN
     */
    public void deleteGenHtmlCode(String ARTICLE_ID){
        //获取文章信息
        Map<String,Object> articleInfo = dao.getRecord("PLAT_CMS_ARTICLE",
                new String[]{"ARTICLE_ID"},new Object[]{ARTICLE_ID});
        String ARTICLE_SIGN = articleInfo.get("ARTICLE_SIGN").toString();
        String appPath = PlatAppUtil.getAppAbsolutePath();
        StringBuffer htmlPath = new StringBuffer(appPath);
        htmlPath.append("webpages/website/genhtmls/article/").append(ARTICLE_SIGN);
        htmlPath.append(".jsp");
        File htmlFile = new File(htmlPath.toString());
        if(htmlFile.exists()){
            htmlFile.delete();
        }
    }
    
    /**
     * 获取后台门户新闻列表数据
     * @param param
     * @return
     */
    public List<Map<String,Object>> findPortalPicNews(String param){
        StringBuffer sql = new StringBuffer("select T.ARTICLE_ID,T.ARTICLE_TITLE");
        sql.append(",T.ARTICLE_SIGN from PLAT_CMS_ARTICLE T LEFT JOIN PLAT_CMS_COLUMN C ");
        sql.append("ON T.ARTICLE_COLUMNID=C.COLUMN_ID WHERE C.COLUMN_CODE=? ");
        sql.append("AND T.ARTICLE_ISPUB=? ORDER BY T.ARTICLE_ISTOP DESC,T.ARTICLE_PUBTIME DESC");
        // 获取附件的根路径
        String rootPath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFileUrl");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), new Object[]{"CPJ_001","1"},
                new PagingBean(0,3));
        for(Map<String,Object> article:list){
            String ARTICLE_ID = (String) article.get("ARTICLE_ID");
            String ARTICLE_SIGN =  article.get("ARTICLE_SIGN").toString();
            String ARTICLE_TITLE = (String) article.get("ARTICLE_TITLE");
            article.put("IMGVALUE", ARTICLE_SIGN);
            List<Map<String,Object>> fileList = fileAttachService.
                    findList("PLAT_CMS_ARTICLE",ARTICLE_ID,null);
            if(fileList!=null&&fileList.size()>0){
                String imgPath = (String) fileList.get(0).get("FILE_PATH");
                StringBuffer IMGSRC = new StringBuffer(rootPath);
                IMGSRC.append(imgPath);
                article.put("IMGSRC", IMGSRC.toString());
            }
            article.put("IMGTITLE", ARTICLE_TITLE);
        }
        return list;
    }
  
}

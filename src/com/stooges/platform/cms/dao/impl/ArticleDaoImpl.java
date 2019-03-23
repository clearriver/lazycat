/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.core.model.PagingBean;
import com.stooges.platform.cms.dao.ArticleDao;

/**
 * 描述文章信息业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-11 10:47:32
 */
@Repository
public class ArticleDaoImpl extends BaseDaoImpl implements ArticleDao {

    /**
     * 获取最新的标识ID
     * @return
     */
    public int getNewSignId(){
        StringBuffer sql = new StringBuffer("SELECT MAX(T.ARTICLE_SIGN)");
        sql.append(" FROM PLAT_CMS_ARTICLE T");
        int sign = this.getIntBySql(sql.toString(),null);
        return sign+1;
    }
    
    /**
     * 获取最新的排序值
     * @return
     */
    public int getNewArticleSn(){
        StringBuffer sql = new StringBuffer("SELECT MAX(T.ARTICLE_SN)");
        sql.append(" FROM PLAT_CMS_ARTICLE T");
        int articleSn = this.getIntBySql(sql.toString(),null);
        return articleSn+1;
    }
    
}

/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.cms;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.cms.service.ArticleService;

/**
 * @author 胡裕
 *
 * 
 */
public class ArticleTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private ArticleService articleService;
    
    @Test
    public void findPortalPicNews(){
        System.out.println(articleService.findPortalPicNews(null));
    }
}

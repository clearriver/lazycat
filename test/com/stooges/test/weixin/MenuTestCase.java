/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.weixin;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.weixin.service.MenuService;

/**
 * @author 胡裕
 *
 * 
 */
public class MenuTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private MenuService menuService;
    
    /**
     * 
     */
    @Test
    public void deleteMenus(){
        System.out.println(menuService.deleteMenus("gh_e898cf9f6221"));
    }
    
    /**
     * 
     */
    @Test
    public void updateAllMenuToWeixin(){
        String result = menuService.updateAllMenuToWeixin("402882a16068a9c4016068ae69c90066");
        System.out.println(result);
    }
}


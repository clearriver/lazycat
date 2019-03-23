/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.appmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.appmodel.service.AppEmailService;

/**
 * @author 胡裕
 *
 * 
 */
public class AppEmailTestBase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private AppEmailService appEmailService;
    
    /**
     * 
     */
    @Test
    public void sendSimpleMail(){
        String subject = "wax价格已经跌破0.00025398";
        String content = "测试邮件内容...";
        appEmailService.sendSimpleMail(subject,content,new String[]{
                "332437849@qq.com"});
    }
    
    /**
     * 
     */
    @Test
    public void sendAttachMail(){
        String subject = "价格提醒";
        String content = "测试邮件内容...d";
        List<File> fileList = new ArrayList<File>();
        File file = new File("d:/tx.jpg");
        fileList.add(file);
        appEmailService.sendAttachMail(subject, content, fileList,new String[]{"332437849@qq.com","huyu@STOOGES.net"});
    }
}

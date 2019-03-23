/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.io.File;
import java.util.List;

import com.stooges.core.service.BaseService;

/**
 * @author 胡裕
 *
 * 
 */
public interface AppEmailService extends BaseService {
    /**
     * 发送简单邮件
     * @param subject
     * @param content
     * @param toMails
     */
    public void sendSimpleMail(String subject,String content,String[] toMails);
    /**
     * 发送带附件的邮件
     * @param subject
     * @param content
     * @param file
     * @param toMails
     */
    public void sendAttachMail(String subject,String content,
            List<File> fileList,String[] toMails);
}
 
/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.appmodel.dao.AppEmailDao;
import com.stooges.platform.appmodel.service.AppEmailService;

/**
 * @author 胡裕
 *
 * 
 */
@Service("appEmailService")
public class AppEmailServiceImpl extends BaseServiceImpl implements AppEmailService {

    /**
     * 所引入的dao
     */
    @Resource
    private AppEmailDao dao;
    /**
     * 
     */
    private JavaMailSender mailSender;
    /* (non-Javadoc)
     * @see net.STOOGES.core.service.impl.BaseServiceImpl#getDao()
     */
    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 发送简单邮件
     * @param subject
     * @param content
     * @param toMails
     */
    public void sendSimpleMail(String subject,String content,String[] toMails){
        mailSender = (JavaMailSender) PlatAppUtil.getBean("mailSender");
        SimpleMailMessage mailMessage = (SimpleMailMessage) PlatAppUtil.getBean("mailMessage");
        mailMessage.setSubject(subject);
        mailMessage.setText(content);
        mailMessage.setTo(toMails);
        mailSender.send(mailMessage);
        String from = PlatPropUtil.getPropertyValue("conf/config.properties", "mail.from");
        Map<String,Object> emailLog = new HashMap<String,Object>();
        emailLog.put("EMAILLOG_SENDER", from);
        emailLog.put("EMAILLOG_TITLE", subject);
        emailLog.put("EMAILLOG_CONTENT", content);
        emailLog.put("EMAILLOG_TIME",PlatDateTimeUtil.
                formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        emailLog.put("EMAILLOG_REMAILS",PlatStringUtil.getSqlInCondition(toMails));
        dao.saveOrUpdate("PLAT_APPMODEL_EMAILLOG",emailLog,AllConstants.IDGENERATOR_UUID,null);
    }
    
    /**
     * 发送带附件的邮件
     * @param subject
     * @param content
     * @param file
     * @param toMails
     */
    public void sendAttachMail(String subject,String content,
            List<File> fileList,String[] toMails){
        mailSender = (JavaMailSender) PlatAppUtil.getBean("mailSender");
        MimeMessage mailMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage,true);
            String from = PlatPropUtil.getPropertyValue("conf/config.properties", "mail.from");
            messageHelper.setFrom(from);
            messageHelper.setTo(toMails);
            messageHelper.setSubject(subject);
            // true 表示启动HTML格式的邮件
            messageHelper.setText(content, true);
            for(File file:fileList){
                messageHelper.addAttachment(file.getName(), file);
            }
            mailSender.send(mailMessage);
            Map<String,Object> emailLog = new HashMap<String,Object>();
            emailLog.put("EMAILLOG_SENDER", from);
            emailLog.put("EMAILLOG_TITLE", subject);
            emailLog.put("EMAILLOG_CONTENT", content);
            emailLog.put("EMAILLOG_TIME",PlatDateTimeUtil.
                    formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            emailLog.put("EMAILLOG_REMAILS",PlatStringUtil.getSqlInCondition(toMails));
            dao.saveOrUpdate("PLAT_APPMODEL_EMAILLOG",emailLog,AllConstants.IDGENERATOR_UUID,null);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    

}

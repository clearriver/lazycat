/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 */
package com.stooges.core.util;

import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 * @author 胡裕
 *
 * 2017年8月7日
 */
public class PlatSmsUtil {

    /**
     * 发送短信
     * @param mobileNumber 手机号
     * @param msgContent  短信内容
     * @return 数组下标0存放发送序列号 下标1存放返回结果描述
     */
    public static String[] sendMobileMsg(String mobileNumber,String msgContent){
        String serialNumber = PlatDateTimeUtil.formatDate(new Date(), "yyyyMMddHHmmsss");
        String randomNum = PlatStringUtil.getFormatNumber(5, String.valueOf(PlatStringUtil.
                getRandomIntNumber(0, 10000)));
        serialNumber+=randomNum;
        String spCode = PlatPropUtil.getPropertyValue("conf/config.properties", "sms_spcode");
        String loginName = PlatPropUtil.getPropertyValue("conf/config.properties", "sms_loginname");
        String password = PlatPropUtil.getPropertyValue("conf/config.properties", "sms_password");
        String[] result = new String[2];
        String info = null;
        try{
            HttpClient httpclient = new HttpClient();
            PostMethod post = new PostMethod("https://api.ums86.com:9600/sms/Api/Send.do");
            post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"gbk");
            post.addParameter("SpCode", spCode);
            post.addParameter("LoginName", loginName);
            post.addParameter("Password", password);
            post.addParameter("MessageContent",msgContent);
            post.addParameter("UserNumber", mobileNumber);
            post.addParameter("SerialNumber",serialNumber);
            post.addParameter("ScheduleTime","");
            post.addParameter("ExtendAccessNum", "");
            post.addParameter("f", "1");
            httpclient.executeMethod(post);
            info = new String(post.getResponseBody(),"gbk");
        }catch (Exception e) {
            PlatLogUtil.printStackTrace(e);
        }
        result[0] = serialNumber;
        result[1] = info;
        return result;
    }
}

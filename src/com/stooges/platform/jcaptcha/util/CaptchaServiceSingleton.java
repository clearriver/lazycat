/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.jcaptcha.util;

import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * 
 * 描述
 * @author 李俊
 * @version 1.0
 * @created 2018-01-22 14:31:15
 */
public class CaptchaServiceSingleton {
    /**
     * 
     */
    private static ImageCaptchaService instance = new DefaultManageableImageCaptchaService(  
            new FastHashMapCaptchaStore(), new GMailEngine(), 180,  
            100000 , 75000);  
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月22日 上午11:04:33
     * @return
     */
     public static ImageCaptchaService getInstance(){  
         return instance;  
     }  
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.jcaptcha.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.jcaptcha.util.CaptchaServiceSingleton;

/**
 * 
 * 描述
 * @author 李俊
 * @version 1.0
 * @created 2018-01-22 14:31:15
 */
@Controller
@RequestMapping("/jcaptcha/ImageCaptchaController")
public class ImageCaptchaController extends BaseController {
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月22日 上午11:04:25
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/image")
    public void image(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream out = response.getOutputStream();
        try {
            String captchaId = request.getSession().getId();
            BufferedImage challenge = (BufferedImage) CaptchaServiceSingleton
                    .getInstance().getChallengeForID(captchaId,
                            request.getLocale());
            ImageIO.write(challenge, "jpg", out);
            out.flush();
        } catch (Exception e) {
        } finally {
            try{
                out.close();
            } catch (Exception e) {
            } 
        }
    }
}

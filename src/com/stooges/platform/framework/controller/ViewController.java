/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.framework.controller;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatFileUtil;
import com.stooges.core.util.PlatImageUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;

/**
 * 描述
 * @author 胡裕
 * @created 2017年1月24日 下午2:11:56
 */
@Controller
@RequestMapping("/framework/ViewController")
public class ViewController extends BaseController {

    /**
     * 跳转到后台框架主页
     * @param request
     * @return
     */
    @RequestMapping(params = "main")
    public ModelAndView main(HttpServletRequest request) {
        String subsyscode = request.getParameter("subsyscode");
        request.setAttribute("subsyscode", subsyscode);
        return new ModelAndView("background/framework/main");
    }
    
    
    /**
     * 跳转到目标界面
     * @param request
     * @return
     */
    @RequestMapping(params = "view")
    public ModelAndView view(HttpServletRequest request) {
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> params = PlatBeanUtil.getMapFromRequest(request);
        request.setAttribute("params", params);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 删除文件
     * @param request
     * @param response
     */
    @RequestMapping("/deleteUploadFile")  
    public void deleteUploadFile(HttpServletRequest request,HttpServletResponse response)  { 
        Map<String,Object> result = new HashMap<String,Object>();
        String filename = request.getParameter("filename");
        String filePath = request.getSession().getServletContext()
                .getRealPath("/Upload/")+filename;
        File fileDir = new File(filePath);
        if (fileDir.exists()) {
            PlatFileUtil.deleteFile(fileDir);
        }
        result.put("success", "true");
        this.printObjectJsonString(result, response); 
    }
    /**
     * 
     * @param request
     * @return
     */
    @RequestMapping(params = "browserDownload")
    public ModelAndView browserDownload(HttpServletRequest request) {
        return new ModelAndView("background/framework/browserDownload");
    }
}

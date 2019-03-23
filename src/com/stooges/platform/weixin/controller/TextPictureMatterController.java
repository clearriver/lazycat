/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.weixin.service.MediaMatterService;
import com.stooges.platform.weixin.service.TextPictureMatterService;

/**
 * 
 * 描述 图文素材业务相关Controller
 * @author 李俊
 * @version 1.0
 * @created 2018-02-22 16:09:23
 */
@Controller  
@RequestMapping("/weixin/TextPictureMatterController")  
public class TextPictureMatterController extends BaseController {
    /**
     * 
     */
    @Resource
    private TextPictureMatterService textPictureMatterService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 
     */
    @Resource
    private MediaMatterService mediaMatterService;
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年1月31日 上午10:33:36
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String PUBLIC_ID = request.getParameter("PUBLIC_ID");
        List<Map<String,Object>> list = textPictureMatterService.findListByPublicId(PUBLIC_ID);
        request.setAttribute("titelItem", list);
        request.setAttribute("PUBLIC_ID", PUBLIC_ID);
        return new ModelAndView("background/weixin/textPictureForm");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月1日 上午11:17:38
     * @param request
     * @return
     */
    @RequestMapping(params = "goImgSelectForm")
    public ModelAndView goImgConfigForm(HttpServletRequest request) {
        String allowCount = request.getParameter("allowCount");
        String publicId = request.getParameter("publicId");
        List<Map<String,Object>> imgList = mediaMatterService.findMatterListByType("image",
                publicId);
        request.setAttribute("imgList", imgList);
        request.setAttribute("allowCount", allowCount);
        return new ModelAndView("background/weixin/imgSelectForm");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月2日 下午3:41:07
     * @param request
     * @return
     */
    @RequestMapping(params = "goImgEasySelectForm")
    public ModelAndView goImgEasySelectForm(HttpServletRequest request) {
        String allowCount = request.getParameter("allowCount");
        List<Map<String,Object>> imgList = new ArrayList<Map<String,Object>>();
        String allSrc = request.getParameter("allSrc");
        if(StringUtils.isNotEmpty(allSrc)){
            String[] arr = allSrc.split(";");
            for (int i = 0; i < arr.length; i++) {
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("FILE_PATH", arr[i]);
                imgList.add(map);
            }
        }
        request.setAttribute("imgList", imgList);
        request.setAttribute("allowCount", allowCount);
        return new ModelAndView("background/weixin/imgEasySelectForm");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月2日 下午4:51:25
     * @param request
     * @return
     */
    @RequestMapping(params = "goVideoSelectForm")
    public ModelAndView goVideoSelectForm(HttpServletRequest request) {
        String allowCount = request.getParameter("allowCount");
        String publicId = request.getParameter("publicId");
        List<Map<String,Object>> imgList = mediaMatterService.findMatterListByType("video",
                publicId);
        request.setAttribute("imgList", imgList);
        request.setAttribute("allowCount", allowCount);
        return new ModelAndView("background/weixin/videoSelectForm");
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月22日 下午4:03:08
     * @param request
     * @return
     */
    @RequestMapping(params = "addItem")
    public ModelAndView addItem(HttpServletRequest request) {
        String publicId = request.getParameter("publicId");
        List<Map<String,Object>> list = textPictureMatterService.findListByPublicId(publicId);
        Map<String,Object> textPictureMatter = new HashMap<String,Object>();
        if(list!=null&&list.size()>0){
            int nextSn = Integer.parseInt(list.get(list.size()-1).get("TEXTPICTURE_SN").toString())+1;
            textPictureMatter.put("TEXTPICTURE_SN", nextSn);
            request.setAttribute("IS_FIRST", "false");
        }else{
            textPictureMatter.put("TEXTPICTURE_SN", 1);
            request.setAttribute("IS_FIRST", "true");
        }
        textPictureMatter.put("TEXTPICTURE_PUBID", publicId);
        textPictureMatter = textPictureMatterService.saveOrUpdate("PLAT_WEIXIN_TEXTPICTUREMATTER", textPictureMatter,
                AllConstants.IDGENERATOR_UUID,null);
        request.setAttribute("textPictureMatter", textPictureMatter);
        return new ModelAndView("background/weixin/textPictureItem");
        
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月22日 下午5:08:17
     * @param request
     * @param response
     */
    @RequestMapping(params = "delItem")
    public void delItem(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        textPictureMatterService.deleteRecords("PLAT_WEIXIN_TEXTPICTUREMATTER",
                "TEXTPICTURE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("用户与素材",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的图文素材", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月22日 下午5:33:38
     * @param request
     * @param response
     */
    @RequestMapping(params = "getContent")
    public void getContent(HttpServletRequest request,
            HttpServletResponse response) {
        String TEXTPICTURE_ID = request.getParameter("TEXTPICTURE_ID");
        Map<String,Object> result = this.textPictureMatterService.getRecord("PLAT_WEIXIN_TEXTPICTUREMATTER"
                ,new String[]{"TEXTPICTURE_ID"},new Object[]{TEXTPICTURE_ID});
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    /**
     * 
     * 描述
     * @author 李俊
     * @created 2018年2月23日 上午10:43:59
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> textMatter = PlatBeanUtil.getMapFromRequest(request);
        textMatter = textPictureMatterService.saveOrUpdate("PLAT_WEIXIN_TEXTPICTUREMATTER",
                textMatter,AllConstants.IDGENERATOR_UUID,null);
        textMatter.put("success", true);
        this.printObjectJsonString(textMatter, response);
    }
}

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
import com.stooges.platform.weixin.service.TextMatterService;

/**
 * 
 * 描述 文本素材业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 14:31:15
 */
@Controller  
@RequestMapping("/weixin/TextMatterController")  
public class TextMatterController extends BaseController {
    /**
     * 
     */
    @Resource
    private TextMatterService textMatterService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除文本素材数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        textMatterService.deleteRecords("PLAT_WEIXIN_TEXTMATTER","TEXTMATTER_ID",selectColValues.split(","));
        sysLogService.saveBackLog("用户与素材",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的文本素材", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改文本素材数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> textMatter = PlatBeanUtil.getMapFromRequest(request);
        String TEXTMATTER_ID = (String) textMatter.get("TEXTMATTER_ID");
        if(StringUtils.isEmpty(TEXTMATTER_ID)){
            textMatter.put("TEXTMATTER_TIME",PlatDateTimeUtil.
                    formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        textMatter = textMatterService.saveOrUpdate("PLAT_WEIXIN_TEXTMATTER",
                textMatter,AllConstants.IDGENERATOR_UUID,null);
        textMatter.put("success", true);
        this.printObjectJsonString(textMatter, response);
    }
    
    /**
     * 跳转到文本素材表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String TEXTMATTER_ID = request.getParameter("TEXTMATTER_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> textMatter = null;
        if(StringUtils.isNotEmpty(TEXTMATTER_ID)){
            textMatter = this.textMatterService.getRecord("PLAT_WEIXIN_TEXTMATTER"
                    ,new String[]{"TEXTMATTER_ID"},new Object[]{TEXTMATTER_ID});
        }else{
            textMatter = new HashMap<String,Object>();
        }
        request.setAttribute("textMatter", textMatter);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String TEXTMATTER_ID = request.getParameter("TEXTMATTER_ID");
        String TEXTMATTER_PARENTID = request.getParameter("TEXTMATTER_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> textMatter = null;
        if(StringUtils.isNotEmpty(TEXTMATTER_ID)){
            textMatter = this.textMatterService.getRecord("PLAT_WEIXIN_TEXTMATTER"
                    ,new String[]{"TEXTMATTER_ID"},new Object[]{TEXTMATTER_ID});
            TEXTMATTER_PARENTID = (String) textMatter.get("TextMatter_PARENTID");
        }
        Map<String,Object> parentTextMatter = null;
        if(TEXTMATTER_PARENTID.equals("0")){
            parentTextMatter = new HashMap<String,Object>();
            parentTextMatter.put("TEXTMATTER_ID",TEXTMATTER_PARENTID);
            parentTextMatter.put("TEXTMATTER_NAME","文本素材树");
        }else{
            parentTextMatter = this.textMatterService.getRecord("PLAT_WEIXIN_TEXTMATTER",
                    new String[]{"TEXTMATTER_ID"}, new Object[]{TEXTMATTER_PARENTID});
        }
        if(textMatter==null){
            textMatter = new HashMap<String,Object>();
        }
        textMatter.put("TEXTMATTER_PARENTID",parentTextMatter.get("TEXTMATTER_ID"));
        textMatter.put("TEXTMATTER_PARENTNAME",parentTextMatter.get("TEXTMATTER_NAME"));
        request.setAttribute("textMatter", textMatter);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

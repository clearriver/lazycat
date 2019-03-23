/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stooges.core.model.PagingBean;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.cms.service.VideoCourseService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 视频教程业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-08-07 16:27:05
 */
@Controller  
@RequestMapping("/cms/VideoCourseController")  
public class VideoCourseController extends BaseController {
    /**
     * 
     */
    @Resource
    private VideoCourseService videoCourseService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private FileAttachService fileAttachService;
    
    /**
     * 删除视频教程数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        videoCourseService.deleteRecords("PLAT_CMS_VIDEOCOURSE","VIDEOCOURSE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("视频教程管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的视频教程", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改视频教程数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> videoCourse = PlatBeanUtil.getMapFromRequest(request);
        String VIDEOCOURSE_ID = (String) videoCourse.get("VIDEOCOURSE_ID");
        if(StringUtils.isEmpty(VIDEOCOURSE_ID)){
            int nextSn = videoCourseService.getNextSn();
            videoCourse.put("VIDEOCOURSE_SN", nextSn);
            videoCourse.put("VIDEOCOURSE_CLICKCOUNT", 0);
        }
        videoCourse = videoCourseService.saveOrUpdate("PLAT_CMS_VIDEOCOURSE",
                videoCourse,AllConstants.IDGENERATOR_UUID,null);
        VIDEOCOURSE_ID = (String) videoCourse.get("VIDEOCOURSE_ID");
        String PHOTO_JSON = (String) videoCourse.get("PHOTO_JSON");
        fileAttachService.saveFileAttachs(PHOTO_JSON,"PLAT_CMS_VIDEOCOURSE",VIDEOCOURSE_ID,null);
        videoCourse.put("success", true);
        this.printObjectJsonString(videoCourse, response);
    }
    
    /**
     * 跳转到视频教程表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String VIDEOCOURSE_ID = request.getParameter("VIDEOCOURSE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> videoCourse = null;
        if(StringUtils.isNotEmpty(VIDEOCOURSE_ID)){
            videoCourse = this.videoCourseService.getRecord("PLAT_CMS_VIDEOCOURSE"
                    ,new String[]{"VIDEOCOURSE_ID"},new Object[]{VIDEOCOURSE_ID});
        }else{
            videoCourse = new HashMap<String,Object>();
        }
        request.setAttribute("videoCourse", videoCourse);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 
     * @param request
     * @return
     */
    @RequestMapping("/playonline")
    public ModelAndView playonline(HttpServletRequest request){
        String videoCourseId = request.getParameter("videoCourseId");
        Map<String,Object> videoCourse = this.videoCourseService.getRecord("PLAT_CMS_VIDEOCOURSE"
                ,new String[]{"VIDEOCOURSE_ID"},new Object[]{videoCourseId});
        if(videoCourse!=null){
            int clickCount = Integer.parseInt(videoCourse.get("VIDEOCOURSE_CLICKCOUNT").toString());
            clickCount+=1;
            videoCourse.put("VIDEOCOURSE_CLICKCOUNT", clickCount);
            videoCourseService.saveOrUpdate("PLAT_CMS_VIDEOCOURSE",videoCourse,
                    AllConstants.IDGENERATOR_UUID,null);
            request.setAttribute("videoCourse", videoCourse);
            SqlFilter filter = new SqlFilter(request);
            filter.addFilter("O_T.VIDEOCOURSE_LEVEL", "ASC", 2);
            filter.addFilter("O_T.VIDEOCOURSE_SN", "ASC", 2);
            filter.setPagingBean(new PagingBean(0, 1000));
            List<Map<String, Object>> list = videoCourseService.getVideoCourse(filter);
            String preVideoCourseId = "";
            String nextVideoCourseId = "";
            for (int i = 0; i < list.size(); i++) {
                String vId = (String)list.get(i).get("VIDEOCOURSE_ID");
                if(videoCourseId.equals(vId)){
                    if(i>0){
                        preVideoCourseId = (String)list.get(i-1).get("VIDEOCOURSE_ID");
                    }
                    if(i<(list.size()-1)){
                        nextVideoCourseId = (String)list.get(i+1).get("VIDEOCOURSE_ID");
                    }
                }
            }
            request.setAttribute("preVideoCourseId", preVideoCourseId);
            request.setAttribute("nextVideoCourseId", nextVideoCourseId);
        }
        return new ModelAndView("website/official/playonline");
    }
    /**
     * 
     * 描述
     * @created 2017年8月11日 下午6:00:10
     * @param request
     * @param response
     */
    @RequestMapping("/getVideoCourse")
    public void getVideoCourse(HttpServletRequest request,
            HttpServletResponse response) {
        SqlFilter filter = new SqlFilter(request);
        filter.addFilter("O_T.VIDEOCOURSE_LEVEL", "ASC", 2);
        filter.addFilter("O_T.VIDEOCOURSE_SN", "ASC", 2);
        List<Map<String, Object>> list = videoCourseService.getVideoCourse(filter);
        this.printListJsonString(filter.getPagingBean(), list, response);
    }
}

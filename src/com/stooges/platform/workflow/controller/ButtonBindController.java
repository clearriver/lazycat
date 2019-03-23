/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.controller;

import java.util.ArrayList;
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
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.workflow.service.ButtonBindService;
import com.stooges.platform.workflow.service.NodeBindService;

/**
 * 
 * 描述 按钮绑定业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-07 09:49:51
 */
@Controller  
@RequestMapping("/workflow/ButtonBindController")  
public class ButtonBindController extends BaseController {
    /**
     * 
     */
    @Resource
    private ButtonBindService buttonBindService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private NodeBindService nodeBindService;
    
    /**
     * 更新排序值
     * @param request
     * @param response
     */
    @RequestMapping(params = "updateSn")
    public void updateSn(HttpServletRequest request,
            HttpServletResponse response) {
        String BTNBIND_IDS = request.getParameter("BTNBIND_IDS");
        buttonBindService.updateSn(BTNBIND_IDS);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 删除按钮绑定数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        buttonBindService.deleteCacadeBind(selectColValues);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改按钮绑定数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> buttonBind = PlatBeanUtil.getMapFromRequest(request);
        buttonBind = buttonBindService.saveCascadeBind(buttonBind);
        buttonBind.put("success", true);
        this.printObjectJsonString(buttonBind, response);
    }
    
    /**
     * 跳转到按钮绑定表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String BTNBIND_ID = request.getParameter("BTNBIND_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        String FLOWDEF_ID = request.getParameter("FLOWDEF_ID");
        String FLOWDEF_VERSION = request.getParameter("FLOWDEF_VERSION");
        Map<String,Object> buttonBind = null;
        if(StringUtils.isNotEmpty(BTNBIND_ID)){
            buttonBind = this.buttonBindService.getRecord("JBPM6_BUTTONBIND"
                    ,new String[]{"BTNBIND_ID"},new Object[]{BTNBIND_ID});
            List<Map<String,Object>> bindNodeList = nodeBindService.findBindNodeList(FLOWDEF_ID,
                    Integer.parseInt(FLOWDEF_VERSION),BTNBIND_ID,NodeBindService.BINDTYPE_BUTTON);
            StringBuffer NODE_KEYS = new StringBuffer("");
            StringBuffer NODE_NAMES = new StringBuffer("");
            for(int i=0;i<bindNodeList.size();i++){
                if(i>0){
                    NODE_KEYS.append(",");
                    NODE_NAMES.append(",");
                }
                NODE_KEYS.append(bindNodeList.get(i).get("NODE_KEY"));
                NODE_NAMES.append(bindNodeList.get(i).get("NODE_NAME"));
            }
            buttonBind.put("BTNBIND_NODEKEYS", NODE_KEYS.toString());
            buttonBind.put("BTNBIND_NODENAMES", NODE_NAMES.toString());
        }else{
            buttonBind = new HashMap<String,Object>();
            buttonBind.put("BTNBIND_FLOWDEFID", FLOWDEF_ID);
            buttonBind.put("BTNBIND_FLOWVERSION", FLOWDEF_VERSION);
        }
        buttonBind.put("FLOWDEF_ID", FLOWDEF_ID);
        buttonBind.put("FLOWDEF_VERSION", FLOWDEF_VERSION);
        request.setAttribute("buttonBind", buttonBind);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
}

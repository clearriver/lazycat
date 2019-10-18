/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.task.controller;

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
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.task.service.TaskService;

/**
 * 
 * 描述 申请流程任务表业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-08-04 17:14:44
 */
@Controller  
@RequestMapping("/task/TaskController")  
public class TaskController extends BaseController {
    /**
     * 
     */
    @Resource
    private TaskService taskService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除申请流程任务表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        taskService.deleteRecords("JBPM6_TASK","TASK_ID",selectColValues.split(","));
        sysLogService.saveBackLog("简易个人申请",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的申请流程任务表", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改申请流程任务表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> task = PlatBeanUtil.getMapFromRequest(request);
        String TASK_ID = (String) task.get("TASK_ID");
        task = taskService.saveOrUpdate("JBPM6_TASK",
                task,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //task = taskService.saveOrUpdateTreeData("JBPM6_TASK",
        //        task,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(TASK_ID)){
            sysLogService.saveBackLog("简易个人申请",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+TASK_ID+"]申请流程任务表", request);
        }else{
            TASK_ID = (String) task.get("TASK_ID");
            sysLogService.saveBackLog("简易个人申请",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+TASK_ID+"]申请流程任务表", request);
        }
        task.put("success", true);
        this.printObjectJsonString(task, response);
    }
    
    /**
     * 跳转到申请流程任务表表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String TASK_ID = request.getParameter("TASK_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> task = null;
        if(StringUtils.isNotEmpty(TASK_ID)){
            task = this.taskService.getRecord("JBPM6_TASK"
                    ,new String[]{"TASK_ID"},new Object[]{TASK_ID});
        }else{
            task = new HashMap<String,Object>();
        }
        request.setAttribute("task", task);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String TASK_ID = request.getParameter("TASK_ID");
        String TASK_PARENTID = request.getParameter("TASK_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> task = null;
        if(StringUtils.isNotEmpty(TASK_ID)){
            task = this.taskService.getRecord("JBPM6_TASK"
                    ,new String[]{"TASK_ID"},new Object[]{TASK_ID});
            TASK_PARENTID = (String) task.get("Task_PARENTID");
        }
        Map<String,Object> parentTask = null;
        if(TASK_PARENTID.equals("0")){
            parentTask = new HashMap<String,Object>();
            parentTask.put("TASK_ID",TASK_PARENTID);
            parentTask.put("TASK_NAME","申请流程任务表树");
        }else{
            parentTask = this.taskService.getRecord("JBPM6_TASK",
                    new String[]{"TASK_ID"}, new Object[]{TASK_PARENTID});
        }
        if(task==null){
            task = new HashMap<String,Object>();
        }
        task.put("TASK_PARENTID",parentTask.get("TASK_ID"));
        task.put("TASK_PARENTNAME",parentTask.get("TASK_NAME"));
        request.setAttribute("task", task);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

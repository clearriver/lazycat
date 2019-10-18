/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.workflow.controller;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.util.UUIDGenerator;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.workflow.model.FlowNextStep;
import com.stooges.platform.workflow.model.FlowNode;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.model.NodeAssigner;
import com.stooges.platform.workflow.service.ExecutionService;
import com.stooges.platform.workflow.service.FlowDefService;
import com.stooges.platform.workflow.service.FlowFormService;
import com.stooges.platform.workflow.service.FlowNodeService;
import com.stooges.platform.workflow.service.FormFieldService;
import com.stooges.platform.workflow.service.JbpmService;
import com.stooges.platform.workflow.service.JbpmTaskService;
import com.stooges.platform.workflow.service.NodeBindService;

/**
 * 
 * 描述 流程实例业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-04 14:34:24
 */
@Controller  
@RequestMapping("/workflow/ExecutionController")  
public class ExecutionController extends BaseController {
    /**
     * 
     */
    @Resource
    private ExecutionService executionService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    /**
     * 
     */
    @Resource
    private JbpmService jbpmService;
    /**
     * 
     */
    @Resource
    private FlowDefService flowDefService;
    /**
     * 
     */
    @Resource
    private FlowNodeService flowNodeService;
    /**
     * 
     */
    @Resource
    private NodeBindService nodeBindService;
    /**
     * 
     */
    @Resource
    private FlowFormService flowFormService;
    /**
     * 
     */
    @Resource
    private FormFieldService formFieldService;
    /**
     * 
     */
    @Resource
    private JbpmTaskService jbpmTaskService;
    
    /**
     * 终结流程实例数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "endExe")
    public void endExe(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        executionService.endExecution(selectColValues.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 删除流程实例数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        executionService.delCascadeSubTables(selectColValues.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 
     * @param request
     */
    private JbpmFlowInfo setJbpmInfo(HttpServletRequest request,JbpmFlowInfo jbpmFlowInfo){
        String jbpmHandleOpinion = request.getParameter("jbpmHandleOpinion");
        String jbpmAssignJson = request.getParameter("jbpmAssignJson");
        String jbpmIsTempSave = request.getParameter("jbpmIsTempSave");
        String jbpmToEndStatus = request.getParameter("jbpmToEndStatus");
        String jbpmHandleTaskStatus = request.getParameter("jbpmHandleTaskStatus");
        String jbpmEndOpinion = request.getParameter("jbpmEndOpinion");
        if(StringUtils.isNotEmpty(jbpmAssignJson)){
            jbpmFlowInfo.setJbpmAssignJson(jbpmAssignJson);
        }
        if(StringUtils.isNotEmpty(jbpmHandleOpinion)){
            jbpmFlowInfo.setJbpmHandleOpinion(jbpmHandleOpinion);
        }
        if(StringUtils.isNotEmpty(jbpmIsTempSave)){
            jbpmFlowInfo.setJbpmIsTempSave(jbpmIsTempSave);
        }
        if(StringUtils.isNotEmpty(jbpmToEndStatus)){
            jbpmFlowInfo.setJbpmToEndStatus(jbpmToEndStatus);
        }
        if(StringUtils.isNotEmpty(jbpmHandleTaskStatus)){
            jbpmFlowInfo.setJbpmHandleTaskStatus(jbpmHandleTaskStatus);
        }
        if(StringUtils.isNotEmpty(jbpmEndOpinion)){
            jbpmFlowInfo.setJbpmEndOpinion(jbpmEndOpinion);
        }
        return jbpmFlowInfo;
    }
    
    /**
     * 提交流程
     * @param request
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(params = "exeFlow")
    public void exeFlow(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> paramsMap = null;
        String flowToken = request.getParameter("flowToken");
        if(StringUtils.isNotEmpty(flowToken)){
            paramsMap = (Map<String, Object>) PlatAppUtil.getSessionCache(flowToken);
            Map<String,Object> postParams = PlatBeanUtil.getMapFromRequest(request);
            paramsMap.putAll(postParams);
        }else{
            paramsMap = PlatBeanUtil.getMapFromRequest(request);
        }
        JbpmFlowInfo jbpmFlowInfo = (JbpmFlowInfo) PlatBeanUtil.mapToBean(new JbpmFlowInfo(), paramsMap);
        jbpmFlowInfo = this.setJbpmInfo(request, jbpmFlowInfo);
        Map<String,Object> result = this.executionService.exeFlowResult(paramsMap, jbpmFlowInfo);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 跳转到流程办理弹出框
     * @param request
     * @return
     */
    @RequestMapping(params = "goFlowHandleWindow")
    public ModelAndView goFlowHandleWindow(HttpServletRequest request) {
        String flowToken = request.getParameter("flowToken");
        String nextAuditType = "0";
        Map<String,Object> flowVars = (Map<String, Object>) PlatAppUtil.getSessionCache(flowToken);
        JbpmFlowInfo jbpmFlowInfo = (JbpmFlowInfo) PlatBeanUtil.mapToBean(new JbpmFlowInfo(),flowVars);
        if(flowVars.containsKey("needQuery")){
        	List<Map<String, Object>> list=jbpmService.findBySql("SELECT * FROM jbpm6_task where TASK_MAINTABLENAME=? and TASK_MAINRECORDID=? order by TASK_CREATETIME desc", 
        			new String[]{jbpmFlowInfo.getJbpmMainTableName(),jbpmFlowInfo.getJbpmMainTableRecordId()}, null);
        	if(list!=null&&list.size()>0){
        		Map<String, Object> map=list.get(0);
            	jbpmFlowInfo=jbpmService.getJbpmFlowInfo(map.get("TASK_EXEID").toString(),null,"false",map.get("TASK_ID").toString());
            	flowVars.putAll(PlatBeanUtil.beanToMap(jbpmFlowInfo));
        	}
        }
        List<FlowNextStep> nextStepList = null;
        //获取当前办理人任务数据
        String operingTaskId = jbpmFlowInfo.getJbpmOperingTaskId();
        if(StringUtils.isNotEmpty(operingTaskId)){
            Map<String,Object> operingTask = executionService.getRecord("JBPM6_TASK",
                    new String[]{"TASK_ID"}, new Object[]{jbpmFlowInfo.getJbpmOperingTaskId()});
            //获取原路返回任务ID
            String TASK_BACKTRANID = (String) operingTask.get("TASK_BACKTRANID");
            if(operingTask.get("TASK_ORDERSN")!=null&&operingTask.get("TASK_ORDERPARENTID")!=null){
                //获取下一任务排序值
                int nextSn = Integer.parseInt(operingTask.get("TASK_ORDERSN").toString())+1;
                String TASK_ORDERPARENTID = (String) operingTask.get("TASK_ORDERPARENTID");
                Map<String,Object> nextTask = executionService.getRecord("JBPM6_TASK",
                        new String[]{"TASK_EXEID","TASK_NODEKEY","TASK_ORDERSN","TASK_ORDERPARENTID"},
                        new Object[]{jbpmFlowInfo.getJbpmExeId(),jbpmFlowInfo.getJbpmOperingNodeKey(),
                        nextSn,TASK_ORDERPARENTID
                });
                if(nextTask!=null){
                    jbpmFlowInfo.setJbpmIsParallel("false");
                    nextStepList = new ArrayList<FlowNextStep>();
                    FlowNextStep nextStep = new FlowNextStep();
                    nextStep.setIsBranch("-1");
                    nextStep.setNodeKeys(nextTask.get("TASK_NODEKEY").toString());
                    nextStep.setNodeNames(nextTask.get("TASK_NODENAME").toString());
                    List<NodeAssigner> nodeAssignerList = new ArrayList<NodeAssigner>();
                    NodeAssigner assigner = new NodeAssigner();
                    assigner.setAssignerIds(nextTask.get("TASK_HANDLERIDS").toString());
                    assigner.setAssignerNames(nextTask.get("TASK_HANDLENAMES").toString());
                    assigner.setNodeKey(nextTask.get("TASK_NODEKEY").toString());
                    assigner.setNodeName(nextTask.get("TASK_NODENAME").toString());
                    assigner.setNextAuditType("1");
                    assigner.setHandlerType("1");
                    nodeAssignerList.add(assigner);
                    nextStep.setNodeAssignerList(nodeAssignerList);
                    nextStepList.add(nextStep);
                }else{
                    nextStepList = jbpmService.findNextStepList(flowVars, jbpmFlowInfo);
                }
            }else if(StringUtils.isNotEmpty(TASK_BACKTRANID)){
                nextStepList = jbpmTaskService.getOldNextSteps(TASK_BACKTRANID);
            }else{
                nextStepList = jbpmService.findNextStepList(flowVars, jbpmFlowInfo);
            }
        }else{
            nextStepList = jbpmService.findNextStepList(flowVars, jbpmFlowInfo);
        }
        if(nextStepList.size()==1){
            FlowNextStep step = nextStepList.get(0);
            if(step.getNodeAssignerList().size()==1){
                nextAuditType = step.getNodeAssignerList().get(0).getNextAuditType();
            }else{
                String selectNodeKey = step.getNodeKeys().split(",")[0];
                step.setBranchSelectKey(selectNodeKey);
                String flowStepToken = UUIDGenerator.getUUID();
                PlatAppUtil.setSessionCache(flowStepToken, step);
                request.setAttribute("flowStepToken", flowStepToken);
            }
        }else{
            String jbpmIsParallel = jbpmFlowInfo.getJbpmIsParallel();
            if(jbpmIsParallel.equals("true")){
                nextAuditType = "4";
            }
        }
        if(StringUtils.isNotEmpty(jbpmFlowInfo.getJbpmParentDefId())){
            request.setAttribute("jbpmRestartParentExe", "true");
        }
        request.setAttribute("flowToken", flowToken);
        request.setAttribute("nextAuditType", nextAuditType);
        request.setAttribute("flowVars",flowVars);
        request.setAttribute("nextStepList", nextStepList);
        return new ModelAndView("background/workflow/execution/submitflow");
    }
    
    /**
     * 跳转到流程转办弹出框
     * @param request
     * @return
     */
    @RequestMapping(params = "goForwardWindow")
    public ModelAndView goForwardWindow(HttpServletRequest request) {
        String flowToken = request.getParameter("flowToken");
        Map<String,Object> flowVars = (Map<String, Object>) PlatAppUtil.getSessionCache(flowToken);
        JbpmFlowInfo jbpmFlowInfo = (JbpmFlowInfo) PlatBeanUtil.mapToBean(new JbpmFlowInfo(),flowVars);
        String operingNodeKey = jbpmFlowInfo.getJbpmOperingNodeKey();
        String operingNodeName = jbpmFlowInfo.getJbpmOperingNodeName();
        List<FlowNextStep> nextStepList = new ArrayList<FlowNextStep>();
        FlowNextStep nextStep = new FlowNextStep();
        nextStep.setNodeKeys(operingNodeKey);
        nextStep.setNodeNames(operingNodeName);
        List<NodeAssigner> nodeAssignerList = new ArrayList<NodeAssigner>();
        NodeAssigner nodeAssigner = new NodeAssigner();
        nodeAssigner.setIsOrder("-1");
        nodeAssigner.setHandlerNature("1");
        nodeAssigner.setNodeKey(operingNodeKey);
        nodeAssigner.setNodeName(operingNodeName);
        nodeAssignerList.add(nodeAssigner);
        nextStep.setNodeAssignerList(nodeAssignerList);
        nextStepList.add(nextStep);
        request.setAttribute("flowToken", flowToken);
        request.setAttribute("nextStepList", nextStepList);
        return new ModelAndView("background/workflow/execution/forwardflow");
    }
    
    /**
     * 跳转到审核不通过弹出框
     * @param request
     * @return
     */
    @RequestMapping(params = "goAuditNoAgreeWindow")
    public ModelAndView goAuditNoAgreeWindow(HttpServletRequest request) {
        String flowToken = request.getParameter("flowToken");
        request.setAttribute("flowToken", flowToken);
        return PlatUICompUtil.goDesignUI("auditnoagreeflow", request);
    }
    
    /**
     * 跳转到流程退回弹出框
     * @param request
     * @return
     */
    @RequestMapping(params = "goReturnFlowWindow")
    public ModelAndView goReturnFlowWindow(HttpServletRequest request) {
        String flowToken = request.getParameter("flowToken");
        Map<String,Object> flowVars = (Map<String, Object>) PlatAppUtil.getSessionCache(flowToken);
        JbpmFlowInfo jbpmFlowInfo = (JbpmFlowInfo) PlatBeanUtil.mapToBean(new JbpmFlowInfo(),flowVars);
        List<FlowNextStep> nextStepList = jbpmTaskService.findReturnStepList(jbpmFlowInfo);
        request.setAttribute("flowToken", flowToken);
        request.setAttribute("nextStepList", nextStepList);
        return new ModelAndView("background/workflow/execution/returnflow");
    }
    
    /**
     * 跳转到固定环节退回弹出框
     * @param request
     * @return
     */
    @RequestMapping(params = "goBackAssignNodeWindow")
    public ModelAndView goBackAssignNodeWindow(HttpServletRequest request) {
        String flowToken = request.getParameter("flowToken");
        String backAssignNodeKey = request.getParameter("backAssignNodeKey");
        Map<String,Object> flowVars = (Map<String, Object>) PlatAppUtil.getSessionCache(flowToken);
        JbpmFlowInfo jbpmFlowInfo = (JbpmFlowInfo) PlatBeanUtil.mapToBean(new JbpmFlowInfo(),flowVars);
        FlowNextStep nextStep = jbpmTaskService.getBackAssignNode(backAssignNodeKey, jbpmFlowInfo);
        request.setAttribute("flowToken", flowToken);
        request.setAttribute("nextStep", nextStep);
        return new ModelAndView("background/workflow/execution/backtoassignnode");
    }
    
    /**
     * 跳转到流程办理界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goHandle")
    public ModelAndView goHandle(HttpServletRequest request) {
        Map<String,Object> params = PlatBeanUtil.getMapFromRequest(request);
        JbpmFlowInfo jbpmFlowInfo = (JbpmFlowInfo) PlatBeanUtil.mapToBean(new JbpmFlowInfo(), params);
        jbpmFlowInfo = jbpmService.getJbpmFlowInfo(jbpmFlowInfo.getJbpmExeId(),null,
                jbpmFlowInfo.getJbpmIsQuery(),jbpmFlowInfo.getJbpmOperingTaskId());
        return jbpmService.getFlowDesignUI(request, jbpmFlowInfo);
    }
    
    /**
     * 缓存流程信息数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "cacheFlowInfo")
    public void cacheFlowInfo(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> params = PlatBeanUtil.getMapFromRequest(request);
        String flowToken = UUIDGenerator.getUUID();
        if(params.containsKey("jbpmRunningNodeKeys")) {
            if(!params.containsKey("jbpmOperingNodeKey")||params.get("jbpmOperingNodeKey")==null) {
            	params.put("jbpmOperingNodeKey",params.get("jbpmRunningNodeKeys"));
            }
        }
        PlatAppUtil.setSessionCache(flowToken,params);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        result.put("flowToken", flowToken);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 跳转到任务挂起弹出框
     * @param request
     * @return
     */
    @RequestMapping(params = "goHandupWindow")
    public ModelAndView goHandupWindow(HttpServletRequest request) {
        String flowToken = request.getParameter("flowToken");
        Map<String,Object> flowVars = (Map<String, Object>) PlatAppUtil.getSessionCache(flowToken);
        request.setAttribute("flowToken", flowToken);
        request.setAttribute("flowVars",flowVars);
        return new ModelAndView("background/workflow/execution/handupflow");
    }
    
    /**
     * 跳转到办件查询提示界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goDataQueryTip")
    public ModelAndView goDataQueryTip(HttpServletRequest request) {
        return new ModelAndView("background/workflow/execution/dataquerytip");
    }
    
    /**
     * 跳转到跨环节退回弹出框
     * @param request
     * @return
     */
    @RequestMapping(params = "goJumpBackWindow")
    public ModelAndView goJumpBackWindow(HttpServletRequest request) {
        String flowToken = request.getParameter("flowToken");
        request.setAttribute("flowToken", flowToken);
        return new ModelAndView("background/workflow/execution/jumpbackflow");
    }
    
    /**
     * 跳转到跳转指定环节的跳转弹出窗
     * @param request
     * @return
     */
    @RequestMapping(params = "goAssignNodeWindow")
    public ModelAndView goAssignNodeWindow(HttpServletRequest request) {
        String flowToken = request.getParameter("flowToken");
        String goAssignNodeKey = request.getParameter("goAssignNodeKey");
        String BIND_ID = request.getParameter("BIND_ID");
        Map<String,Object> flowVars = (Map<String, Object>) PlatAppUtil.getSessionCache(flowToken);
        JbpmFlowInfo jbpmFlowInfo = (JbpmFlowInfo) PlatBeanUtil.mapToBean(new JbpmFlowInfo(),flowVars);
        FlowNextStep nextStep = jbpmTaskService.getAppointAssignNode(goAssignNodeKey, jbpmFlowInfo);
        
        request.setAttribute("flowToken", flowToken);
        request.setAttribute("nextStep", nextStep);
        request.setAttribute("BIND_ID", BIND_ID);
        request.setAttribute("BASEFORM_ID", jbpmFlowInfo.getJbpmMainTableRecordId());
        return new ModelAndView("background/workflow/execution/gotoassignnode");
    }
    
    /**
     * 批量提交流程
     * @param request
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(params = "batchExeFlow")
    public void batchExeFlow(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> params = PlatBeanUtil.getMapFromRequest(request);
        this.jbpmService.doBatchFlow(params);
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 将流程实例版本更新到最新版本
     * @param request
     * @param response
     */
    @RequestMapping(params = "updateToNewest")
    public void updateToNewest(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        executionService.updateExeVersionToNewest(selectColValues.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
}

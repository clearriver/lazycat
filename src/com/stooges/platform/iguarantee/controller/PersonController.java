/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.controller;

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
import com.stooges.platform.iguarantee.service.PersonService;

/**
 * 
 * 描述 个人资料业务相关Controller
 * @author river
 * @version 1.0
 * @created 2019-09-05 00:31:15
 */
@Controller  
@RequestMapping("/iguarantee/PersonController")  
public class PersonController extends BaseController {
    /**
     * 
     */
    @Resource
    private PersonService personService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除个人资料数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        personService.deleteRecords("PLAT_IGUARANTEE_PERSON","PERSON_ID",selectColValues.split(","));
        sysLogService.saveBackLog("个人客户",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的个人资料", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改个人资料数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> person = PlatBeanUtil.getMapFromRequest(request);
        String PERSON_ID = (String) person.get("PERSON_ID");
        person = personService.saveOrUpdate("PLAT_IGUARANTEE_PERSON",
                person,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //person = personService.saveOrUpdateTreeData("PLAT_IGUARANTEE_PERSON",
        //        person,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(PERSON_ID)){
            sysLogService.saveBackLog("个人客户",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+PERSON_ID+"]个人资料", request);
        }else{
            PERSON_ID = (String) person.get("PERSON_ID");
            sysLogService.saveBackLog("个人客户",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+PERSON_ID+"]个人资料", request);
        }
        person.put("success", true);
        this.printObjectJsonString(person, response);
    }
    
    /**
     * 跳转到个人资料表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String PERSON_ID = request.getParameter("PERSON_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> person = null;
        if(StringUtils.isNotEmpty(PERSON_ID)){
            person = this.personService.getRecord("PLAT_IGUARANTEE_PERSON"
                    ,new String[]{"PERSON_ID"},new Object[]{PERSON_ID});
        }else{
            person = new HashMap<String,Object>();
        }
        request.setAttribute("person", person);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String PERSON_ID = request.getParameter("PERSON_ID");
        String PERSON_PARENTID = request.getParameter("PERSON_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> person = null;
        if(StringUtils.isNotEmpty(PERSON_ID)){
            person = this.personService.getRecord("PLAT_IGUARANTEE_PERSON"
                    ,new String[]{"PERSON_ID"},new Object[]{PERSON_ID});
            PERSON_PARENTID = (String) person.get("Person_PARENTID");
        }
        Map<String,Object> parentPerson = null;
        if(PERSON_PARENTID.equals("0")){
            parentPerson = new HashMap<String,Object>();
            parentPerson.put("PERSON_ID",PERSON_PARENTID);
            parentPerson.put("PERSON_NAME","个人资料树");
        }else{
            parentPerson = this.personService.getRecord("PLAT_IGUARANTEE_PERSON",
                    new String[]{"PERSON_ID"}, new Object[]{PERSON_PARENTID});
        }
        if(person==null){
            person = new HashMap<String,Object>();
        }
        person.put("PERSON_PARENTID",parentPerson.get("PERSON_ID"));
        person.put("PERSON_PARENTNAME",parentPerson.get("PERSON_NAME"));
        request.setAttribute("person", person);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

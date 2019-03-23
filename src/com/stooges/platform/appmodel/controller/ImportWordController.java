/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

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
import com.stooges.platform.appmodel.service.ImportWordService;

/**
 * 
 * 描述 word导入业务相关Controller
 * @author 李俊
 * @version 1.0
 * @created 2018-04-10 17:26:07
 */
@Controller  
@RequestMapping("/appmodel/ImportWordController")  
public class ImportWordController extends BaseController {
    /**
     * 
     */
    @Resource
    private ImportWordService importWordService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除word导入数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        importWordService.deleteRecords("PLAT_APPMODEL_RACING","RACING_ID",selectColValues.split(","));
        sysLogService.saveBackLog("初级例子",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的word导入", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改word导入数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> importWord = PlatBeanUtil.getMapFromRequest(request);
        String RACING_ID = (String) importWord.get("RACING_ID");
        importWord = importWordService.saveOrUpdate("PLAT_APPMODEL_RACING",
                importWord,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //importWord = importWordService.saveOrUpdateTreeData("PLAT_APPMODEL_RACING",
        //        importWord,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(RACING_ID)){
            sysLogService.saveBackLog("初级例子",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+RACING_ID+"]word导入", request);
        }else{
            RACING_ID = (String) importWord.get("RACING_ID");
            sysLogService.saveBackLog("初级例子",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+RACING_ID+"]word导入", request);
        }
        importWord.put("success", true);
        this.printObjectJsonString(importWord, response);
    }
    
    /**
     * 跳转到word导入表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String RACING_ID = request.getParameter("RACING_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> importWord = null;
        if(StringUtils.isNotEmpty(RACING_ID)){
            importWord = this.importWordService.getRecord("PLAT_APPMODEL_RACING"
                    ,new String[]{"RACING_ID"},new Object[]{RACING_ID});
        }else{
            importWord = new HashMap<String,Object>();
        }
        request.setAttribute("importWord", importWord);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    /**
     * 
     * @param request
     * @return
     */
    @RequestMapping(params = "goSelectWordForm")
    public ModelAndView goSelectWordForm(HttpServletRequest request) {
      //获取设计的界面编码
        return new ModelAndView("background/appmodel/importword/importword");
    }
    
    /**
     * 
     * @param request
     * @param response
     */
    @RequestMapping(params = "impWord")
    public void impWord(HttpServletRequest request,
            HttpServletResponse response) {
        String dbfilepath = request.getParameter("dbfilepath");
        String wordHtmlContent = this.importWordService.getWordHtmlContent(dbfilepath);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        result.put("wordHtmlContent", wordHtmlContent);
        this.printObjectJsonString(result, response);
    }
    
}

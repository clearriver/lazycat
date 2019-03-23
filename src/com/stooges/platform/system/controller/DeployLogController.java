/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.io.FileUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatFileUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.DevManService;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.system.service.DeployLogService;

/**
 * 
 * 描述 部署日志业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2018-04-19 17:16:44
 */
@Controller  
@RequestMapping("/system/DeployLogController")  
public class DeployLogController extends BaseController {
    /**
     * 
     */
    @Resource
    private DeployLogService deployLogService;
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
     * 
     */
    @Resource
    private DevManService devManService;
    
    /**
     * 删除部署日志数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        deployLogService.deleteRecords("PLAT_SYSTEM_DEPLOYLOG","DEPLOYLOG_ID",selectColValues.split(","));
        sysLogService.saveBackLog("自动化部署管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的部署日志", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改部署日志数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String DEPLOYLOG_EMAIL = request.getParameter("DEPLOYLOG_EMAIL");
        String DEPLOYLOG_PASS = request.getParameter("DEPLOYLOG_PASS");
        String encodePass = PlatStringUtil.getMD5Encode(DEPLOYLOG_PASS, null, 1);
        Map<String,Object> devMan = deployLogService.getRecord("PLAT_SYSTEM_DEVMAN",
                new String[]{"DEVMAN_EMAIL","DEVMAN_PASS"},
                new Object[]{DEPLOYLOG_EMAIL,encodePass});
        if(devMan!=null){
            String name = (String) devMan.get("DEVMAN_NAME");
            String nodes = request.getParameter("DEPLOYLOG_NODES");
            String FILES_JSON= request.getParameter("FILES_JSON");
            List<Map> fileList = JSON.parseArray(FILES_JSON,Map.class);
            String jarPath = (String) fileList.get(0).get("dbfilepath");
            deployLogService.saveDeployLog(DEPLOYLOG_EMAIL,name,DEPLOYLOG_PASS,
                    nodes, jarPath,FILES_JSON);
            result.put("success", true);
        }else{
            result.put("success", false);
            result.put("msg", "开发者账号信息错误!");
        }
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 跳转到部署日志表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String DEPLOYLOG_ID = request.getParameter("DEPLOYLOG_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> deployLog = null;
        if(StringUtils.isNotEmpty(DEPLOYLOG_ID)){
            deployLog = this.deployLogService.getRecord("PLAT_SYSTEM_DEPLOYLOG"
                    ,new String[]{"DEPLOYLOG_ID"},new Object[]{DEPLOYLOG_ID});
        }else{
            deployLog = new HashMap<String,Object>();
        }
        request.setAttribute("deployLog", deployLog);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 文件上传
     * 
     * @param request
     * @param response
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    @RequestMapping("/uploaddeploy")
    public void uploaddeploy(HttpServletRequest request, HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String devManEmail = request.getParameter("DEVMAN_EMAIL");
        String devPass = request.getParameter("DEVMAN_PASS");
        devPass = PlatStringUtil.getMD5Encode(devPass, null, 1);
        Map<String,Object> devMan = devManService.getRecord("PLAT_SYSTEM_DEVMAN",
                new String[]{"DEVMAN_EMAIL","DEVMAN_PASS"},new Object[]{devManEmail,devPass});
        if(devMan!=null){
            result = fileAttachService.uploadFile(request,false);
            String dbfilepath= result.get("dbfilepath").toString();
            String rootPath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFilePath");
            String jarFilePath = rootPath+dbfilepath;
            String currentFolderPath = PlatAppUtil.getAppAbsolutePath();
            String webAppPath = currentFolderPath;
            String deployTempPath = webAppPath+"deployTempPath";
            //将上线的文件进行解压
            PlatFileUtil.deCompressJar(jarFilePath,deployTempPath,true);
            File deployTempFolder = new File(deployTempPath);
            for(File childFolder:deployTempFolder.listFiles()){
                String folderName = childFolder.getName();
                String folderPath = childFolder.getPath();
                if(!folderName.equals("WebRoot")&&!folderName.equals("META-INF")){
                    //将class文件进行部署
                    PlatFileUtil.cutFileDir(folderPath, webAppPath+
                            File.separator+"WEB-INF"+File.separator+"classes");
                }else if(folderName.equals("WebRoot")){
                    try {
                        FileUtil.copyDir(folderPath, webAppPath);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            //清除目录
            PlatFileUtil.deleteFileDir(deployTempPath);
        }else{
            result.put("success", false);
            result.put("msg", "开发者信息错误！");
        }
        PlatStringUtil.printObjectJsonString(result, response);
    }
}

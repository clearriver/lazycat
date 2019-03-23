/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.UUIDGenerator;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.system.dao.DeployLogDao;
import com.stooges.platform.system.service.DeployLogService;
import com.stooges.platform.system.service.DeployNodeService;

/**
 * 描述 部署日志业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2018-04-19 17:16:44
 */
@Service("deployLogService")
public class DeployLogServiceImpl extends BaseServiceImpl implements DeployLogService {

    /**
     * 所引入的dao
     */
    @Resource
    private DeployLogDao dao;
    /**
     * 
     */
    @Resource
    private DeployNodeService deployNodeService;
    /**
     * 
     */
    @Resource
    private FileAttachService fileAttachService;
    

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 保存部署记录信息
     * @param email
     * @param name
     * @param pass
     * @param nodeIds
     * @param jarPath
     */
    public void saveDeployLog(String email,String name,String pass,
            String nodeIds,String jarPath,String fileJson){
        for(String nodeId:nodeIds.split(",")){
            Map<String,Object> deployNode = deployNodeService.
                    getRecord("PLAT_SYSTEM_DEPLOYNODE",
                            new String[]{"DEPLOYNODE_ID"},
                            new Object[]{nodeId});
            String DEPLOYNODE_URL = (String) deployNode.get("DEPLOYNODE_URL");
            String deployUrl = DEPLOYNODE_URL+"/system/DeployLogController/uploaddeploy.do";
            Map<String,Object> postParams= new HashMap<String,Object>();
            postParams.put("guid", UUIDGenerator.getUUID());
            postParams.put("uploadRootFolder","deploylog");
            postParams.put("DEVMAN_EMAIL",email);
            postParams.put("DEVMAN_PASS",pass);
            String rootPath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFilePath");
            PlatHttpUtil.uploadFile(deployUrl,rootPath+jarPath, postParams);
        }
        //保存上线记录
        Map<String,Object> deployLog = new HashMap<String,Object>();
        deployLog.put("DEPLOYLOG_EMAIL", email);
        deployLog.put("DEPLOYLOG_NAME", name);
        deployLog.put("DEPLOYLOG_TIME",PlatDateTimeUtil.
                formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        deployLog.put("DEPLOYLOG_NODES", nodeIds);
        deployLog= this.saveOrUpdate("PLAT_SYSTEM_DEPLOYLOG",deployLog,
                AllConstants.IDGENERATOR_UUID,null);
        String busRecordId= (String) deployLog.get("DEPLOYLOG_ID");
        fileAttachService.saveFileAttachs(fileJson,"PLAT_SYSTEM_DEPLOYLOG", busRecordId,null);
    }
  
}

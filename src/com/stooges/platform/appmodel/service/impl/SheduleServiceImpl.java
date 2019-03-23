/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.listener.QuarzJobListener;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.appmodel.dao.SheduleDao;
import com.stooges.platform.appmodel.service.SheduleService;

/**
 * 描述 定时任务业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-27 16:37:05
 */
@Service("sheduleService")
public class SheduleServiceImpl extends BaseServiceImpl implements SheduleService {

    /**
     * 日志工具
     */
    private static Log logger = LogFactory.getLog(SheduleServiceImpl.class);
    
    /**
     * 所引入的dao
     */
    @Resource
    private SheduleDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据编码判断是否存在记录
     * @param sheduleCode
     * @return
     */
    public boolean isExistsShedule(String sheduleCode){
        int count = dao.getCountBySheduleCode(sheduleCode);
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 级联删除定时调度
     * @param sheduleIds
     */
    public void deleteCascadeJob(String sheduleIds){
        String[] sheduleIdArray = sheduleIds.split(",");
        Scheduler scheduler = PlatAppUtil.getScheduler();
        for(String sheduleId:sheduleIdArray){
            Map<String,Object> sheduleInfo = dao.getRecord("PLAT_SYSTEM_SHEDULE", 
                    new String[]{"SHEDULE_ID"}, new String[]{sheduleId});
            String SHEDULE_CODE = (String) sheduleInfo.get("SHEDULE_CODE");
            try {
                scheduler.deleteJob(SHEDULE_CODE, Scheduler.DEFAULT_GROUP);
            } catch (SchedulerException e) {
                PlatLogUtil.printStackTrace(e);
            }
        }
        dao.deleteRecords("PLAT_SYSTEM_SHEDULE","SHEDULE_ID",sheduleIds.split(","));
    }
    
    /**
     * 更新定时器的状态
     * @param sheduleIds
     * @param status
     */
    public void updateStatus(String sheduleIds,int status){
        String[] sheduleIdArray = sheduleIds.split(",");
        Scheduler scheduler = PlatAppUtil.getScheduler();
        for(String sheduleId:sheduleIdArray){
            Map<String,Object> sheduleInfo = dao.getRecord("PLAT_SYSTEM_SHEDULE", 
                    new String[]{"SHEDULE_ID"}, new String[]{sheduleId});
            int SHEDULE_STATUS = Integer.parseInt(sheduleInfo.
                    get("SHEDULE_STATUS").toString());
            String SHEDULE_CODE = (String) sheduleInfo.get("SHEDULE_CODE");
            if(SHEDULE_STATUS!=status){
                if(status==1){
                    this.addJob(sheduleInfo, scheduler);
                }else{
                    try {
                        scheduler.deleteJob(SHEDULE_CODE, Scheduler.DEFAULT_GROUP);
                    } catch (SchedulerException e) {
                        PlatLogUtil.printStackTrace(e);
                    }
                }
            }
        }
        StringBuffer sql = new StringBuffer("UPDATE PLAT_SYSTEM_SHEDULE");
        sql.append(" SET SHEDULE_STATUS=? WHERE SHEDULE_ID IN");
        sql.append(" ").append(PlatStringUtil.getSqlInCondition(sheduleIds));
        dao.executeSql(sql.toString(), new Object[]{status});
    }
    
    /**
     * 根据状态获取列表数据
     * @param status
     * @return
     */
    public List<Map<String,Object>> findByByStatus(int status){
        StringBuffer sql = new StringBuffer("SELECT * FROM PLAT_SYSTEM_SHEDULE");
        sql.append(" S WHERE S.SHEDULE_STATUS=? ORDER BY S.SHEDULE_CREATETIME ASC");
        return dao.findBySql(sql.toString(), new Object[]{status}, null);
    }
    
    /**
     * 
     * @param sheduleInfo
     * @return
     */
    public Map<String,Object> saveOrUpdateCascadeJob(Map<String,Object> sheduleInfo){
        Scheduler scheduler = PlatAppUtil.getScheduler();
        String SHEDULE_ID = (String) sheduleInfo.get("SHEDULE_ID");
        if(StringUtils.isNotEmpty(SHEDULE_ID)){
            Map<String,Object> oldShedule = dao.getRecord("PLAT_SYSTEM_SHEDULE",
                    new String[]{"SHEDULE_ID"},new Object[]{SHEDULE_ID});
            String SHEDULE_CODE = (String) oldShedule.get("SHEDULE_CODE");
            try {
                scheduler.deleteJob(SHEDULE_CODE, Scheduler.DEFAULT_GROUP);
            } catch (SchedulerException e) {
                PlatLogUtil.printStackTrace(e);
            }
        }
        sheduleInfo = dao.saveOrUpdate("PLAT_SYSTEM_SHEDULE",
                sheduleInfo,AllConstants.IDGENERATOR_UUID,null);
        SHEDULE_ID = (String) sheduleInfo.get("SHEDULE_ID");
        Map<String,Object> info = dao.getRecord("PLAT_SYSTEM_SHEDULE",
                new String[]{"SHEDULE_ID"},new Object[]{SHEDULE_ID});
        String status = info.get("SHEDULE_STATUS").toString();
        int SHEDULE_STATUS = Integer.parseInt(status);
        if(SHEDULE_STATUS==1){
            this.addJob(sheduleInfo, scheduler);
        }
        return sheduleInfo;
    }
    
    /**
     * 创建调度任务
     * @param shedule
     */
    public void addJob(Map<String,Object> shedule,Scheduler scheduler){
        String localHostIp = PlatAppUtil.getLocalHostIp();
        String SHEDULE_CODE = (String) shedule.get("SHEDULE_CODE");
        String SHEDULE_CLASSNAME = (String) shedule.get("SHEDULE_CLASSNAME");
        String SHEDULE_CRON = (String) shedule.get("SHEDULE_CRON");
        String SHEDULE_BINDIP = (String) shedule.get("SHEDULE_BINDIP");
        boolean isAdd = false;
        if(StringUtils.isNotEmpty(SHEDULE_BINDIP)&&SHEDULE_BINDIP.equals(localHostIp)){
            isAdd = true;
        }else if(StringUtils.isEmpty(SHEDULE_BINDIP)){
            isAdd = true;
        }
        if(isAdd){
            try {
                JobDetail jobDetail = new JobDetail(SHEDULE_CODE,
                        Scheduler.DEFAULT_GROUP,Class.forName(SHEDULE_CLASSNAME));
                // 创建一个每5秒执行的触发器
                CronTrigger trigger = new CronTrigger(SHEDULE_CODE+"trigger",
                        Scheduler.DEFAULT_GROUP,SHEDULE_CRON);   
                // 设置触发器马上执行
                trigger.setStartTime(new Date());
                // 将这个定时器加入到定时调度池当中
                scheduler.scheduleJob(jobDetail, trigger);
                if(!scheduler.isStarted()){
                    scheduler.start();
                }
            } catch (ParseException e) {
                PlatLogUtil.printStackTrace(e);
            } catch (SchedulerException e) {
                PlatLogUtil.printStackTrace(e);
            } catch (ClassNotFoundException e) {
                PlatLogUtil.printStackTrace(e);
            }       
        }
        
    }
    
    /***
     * 启动定时调度池
     */
    public void startAllShedule(){
        List<Map<String,Object>> list = this.findByByStatus(1);
        if(list.size()>0){
         // 获取一个定时调度池    
            Scheduler scheduler = PlatAppUtil.getScheduler();
            for(Map<String,Object> she:list){
                this.addJob(she, scheduler);
            }
            // 启动这个定时调度池
            try {
                JobListener jobListener =  new QuarzJobListener();   
                scheduler.addGlobalJobListener(jobListener);
                scheduler.start();
            } catch (SchedulerException e) {
                PlatLogUtil.printStackTrace(e);
            }
            
        }
    }
  
}

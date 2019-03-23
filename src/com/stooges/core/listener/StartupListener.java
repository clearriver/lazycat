/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.listener;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContextEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ContextLoaderListener;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.platform.appmodel.service.GlobalUrlService;
import com.stooges.platform.appmodel.service.SheduleService;
import com.stooges.platform.common.service.CommonService;
import com.stooges.platform.system.service.DictionaryService;
import com.stooges.platform.system.service.ResService;

/**
 * 描述 继承spring的contextLoad监听器，以便启动的时候初始化系统
 * @author 胡裕
 * @created 2017年1月8日 上午10:39:47
 */
public class StartupListener extends ContextLoaderListener {

    /**
     * 日志工具
     */
    private static Log logger = LogFactory.getLog(StartupListener.class);
    
    /**
     * 
     * 描述 容器初始化
     * 
     * @author 胡裕
     * @created 2014年9月29日 上午11:05:12
     * @param event
     */
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        //初始化APP工具类和servlet上下文对象
        PlatAppUtil.init(event.getServletContext());
        //加载客户端验证规则数据
        DictionaryService dictionaryService = (DictionaryService) PlatAppUtil.getBean("dictionaryService");
        PlatAppUtil.setValidRules(dictionaryService.getJsValidRules());
        //加载所配置的URL集合
        ResService resService = (ResService) PlatAppUtil.getBean("resService");
        PlatAppUtil.setAllResUrlSet(resService.getAllResUrl());
        //加载所有匿名访问的URL集合
        GlobalUrlService globalUrlService = (GlobalUrlService) PlatAppUtil.getBean("globalUrlService");
        PlatAppUtil.setAllAnonUrlSet(globalUrlService.getAllAnonUrl());
        //启动定时调度池
        SheduleService sheduleService = (SheduleService) PlatAppUtil.getBean("sheduleService");
        sheduleService.startAllShedule();
        //获取主键列表
        CommonService commonService = (CommonService) PlatAppUtil.getBean("commonService");
        Map<String,List<String>> pkNameList = commonService.getAllPkNames();
        PlatAppUtil.setPrimaryKeyMap(pkNameList);
        //获取表字段列表
        PlatAppUtil.setTableColumnMap(commonService.getAllTableColumnNames());
    }
}

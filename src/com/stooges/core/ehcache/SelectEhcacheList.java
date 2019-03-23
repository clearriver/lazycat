/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.ehcache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;

import com.stooges.core.util.PlatLogUtil;
import com.stooges.platform.appmodel.service.SysEhcacheService;
/**
 * 
 * 描述
 * @author 李俊
 * @created 2017年5月3日 下午3:01:23
 */
public class SelectEhcacheList extends RegexpMethodPointcutAdvisor {
    /**
     * log4j声明
     */
    private static Log log = LogFactory.getLog(SelectEhcacheList.class);
    /**
     * 引入Service
     */
    @Resource
    private SysEhcacheService sysEhcacheService;
    
    /**
     * 
     */
    public void setPatterns(String[] patterns) {
        log.debug("开始获取缓存列表----------------------");
        List<String> list = new ArrayList<String>();
        try {
            List<Map<String, Object>> elist = sysEhcacheService.findByStatue("1");
            if (elist != null & elist.size() > 0) {
                for (int i = 0; i < elist.size(); i++) {
                    String estr = elist.get(i).get("EHCACHE_CLASS_NAME").toString().trim();
                    list.add(estr);
                    log.debug("缓存" + i + ":" + estr);
                }
            }
        } catch (Exception e) {
            PlatLogUtil.printStackTrace(e);
        }
        if (list.size() > 0) {
            patterns = list.toArray(new String[list.size()]);
        }
        super.setPatterns(patterns);
    }
}

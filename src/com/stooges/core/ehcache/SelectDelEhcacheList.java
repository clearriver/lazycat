/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.ehcache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;

import com.stooges.core.util.PlatLogUtil;
import com.stooges.platform.appmodel.service.SysEhcacheService;
/**
 * 
 * 描述 获取刷新缓存器
 * @author 李俊
 * @created 2017年5月3日 下午3:09:57
 */
public class SelectDelEhcacheList extends RegexpMethodPointcutAdvisor {
    /**
     * log4j声明
     */
    private static Log log = LogFactory.getLog(SelectDelEhcacheList.class);
    /**
     * 引入Service
     */
    @Resource
    private SysEhcacheService sysEhcacheService;
    /**
     * 
     */
    public void setPatterns(String[] patterns) {
        log.debug("开始获取删除缓存列表----------------------");
        List<String> list = new ArrayList<String>();
        try{
            Set<String> elist = sysEhcacheService.findDelListByStatue("1");
            if (elist != null & elist.size() > 0) {
                int i= 0 ;
                for (String str : elist) {  
                    //String estr = elist.get(i).get("EHCACHE_CLASS_NAME").toString().trim();
                    list.add(str);
                    log.debug("删除缓存" + i + ":" + str);
                    i++;
                }
            }
        }catch(Exception e){
            PlatLogUtil.printStackTrace(e);
        }
        if (list.size() > 0) {
            patterns = list.toArray(new String[list.size()]);
        }else{
            patterns = new String[]{""};
        }
        super.setPatterns(patterns);
    }
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.system;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.system.service.SysUserService;

/**
 * @author 胡裕
 *
 * 
 */
public class SysUserServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private SysUserService sysUserService;
    
    /**
     * 
     */
    @Test
    public void updateUserRightJson(){
        sysUserService.updateUserRightJson("402848a55b6547ec015b6547ec760000");
    }
    
    /**
     * 
     */
    @Test
    public void testFind(){
        
        String sql = "select * from PLAT_SYSTEM_SYSLOG T WHERE T.OPER_MODULENAME LIKE ? ";
        List<Map<String,Object>> list = sysUserService.findBySql(sql, new Object[]{"%系统用户%"},null);
        System.out.println("大小:"+list.size());
    }
}

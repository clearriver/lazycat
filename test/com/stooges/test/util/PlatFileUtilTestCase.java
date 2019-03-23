/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.util;

import java.io.File;
import java.io.IOException;

import jodd.io.FileUtil;

import com.stooges.core.util.PlatFileUtil;

/**
 * @author 胡裕
 *
 * 
 */
public class PlatFileUtilTestCase {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String content = PlatFileUtil.readFileString("d:/CodeDao.ftl");
        if(content.contains("net.evecom.")){
            System.out.println("有..");
        }else{
            System.out.println("不行有..");
        }
    }

}

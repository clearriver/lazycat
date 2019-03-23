/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.security;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * 描述
 * @author 胡裕
 * @created 2017年4月4日 下午1:26:07
 */
public class MyWebApplicationContext extends XmlWebApplicationContext {
    @Override  
    protected DefaultListableBeanFactory createBeanFactory() {  
        DefaultListableBeanFactory beanFactory =  super.createBeanFactory();  
        beanFactory.setAllowRawInjectionDespiteWrapping(true);  
        return beanFactory;  
    }  
}

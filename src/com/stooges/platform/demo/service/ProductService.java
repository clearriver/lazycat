/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.demo.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.service.BaseService;

/**
 * 描述 产品信息业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-10 13:48:25
 */
public interface ProductService extends BaseService {
    /**
     * 
     * @param params
     * @return
     */
    public List<Map<String,Object>> findList(String params);
    
    /**
     * 
     * @param productIds
     */
    public void updateIsShow(String productIds,String isShow);
    /**
     * 测试调用接口
     * @param request
     * @param postParams
     * @return
     */
    public Map<String,Object>  testInvokeServer(HttpServletRequest request,
            Map<String,Object> postParams);
    
    /**
     * 保存数据,面向请求服务测试
     * @param request
     * @return
     */
    public Map<String,Object> saveInfoForDataSer(HttpServletRequest request);
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.demo.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.platform.demo.dao.ProductDao;
import com.stooges.platform.demo.service.ProductService;
import com.stooges.platform.metadata.service.DataSerService;
import com.stooges.platform.system.service.DicTypeService;

/**
 * 描述 产品信息业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-07-10 13:48:25
 */
@Service("productService")
public class ProductServiceImpl extends BaseServiceImpl implements ProductService {

    /**
     * 所引入的dao
     */
    @Resource
    private ProductDao dao;
    /**
     * 
     */
    @Resource
    private DicTypeService dicTypeService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 
     * @param params
     * @return
     */
    public List<Map<String,Object>> findList(String params){
        List<Map<String,Object>> list  =new ArrayList<Map<String,Object>>();
        Map<String,Object> map1 =new HashMap<String,Object>();
        map1.put("VALUE", "1");
        map1.put("LABEL", "测试");
        list.add(map1);
        return list;
    }
    
    /**
     * 
     * @param productIds
     */
    public void updateIsShow(String productIds,String isShow){
        dao.updateIsShow(productIds, isShow);
    }
    
    /**
     * 测试调用接口
     * @param request
     * @param postParams
     * @return
     */
    public Map<String,Object>  testInvokeServer(HttpServletRequest request,
            Map<String,Object> postParams){
        System.out.println("调用了该接口..."+PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        String treeJson = dicTypeService.getTreeJson(request);
        Map<String,Object> result =new HashMap<String,Object>();
        result.put("success",true);
        result.put("treeJson", treeJson);
        return result;
    }
    
    /**
     * 保存数据,面向请求服务测试
     * @param request
     * @return
     */
    public Map<String,Object> saveInfoForDataSer(HttpServletRequest request){
        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> product = PlatBeanUtil.getMapFromRequest(request);
        this.saveOrUpdate("PLAT_DEMO_PRODUCT",product,AllConstants.IDGENERATOR_UUID,null);
        result.put("success", true);
        result.put("invokeResultCode",DataSerService.CODE_SUCCESS);
        result.put("PRODUCT_ID", product.get("PRODUCT_ID"));
        return result;
    }
  
}

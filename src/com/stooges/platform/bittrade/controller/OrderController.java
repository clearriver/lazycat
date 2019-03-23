/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.bittrade.service.OrderService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;

/**
 * 
 * 描述 委托单业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-16 18:12:13
 */
@Controller  
@RequestMapping("/bittrade/OrderController")  
public class OrderController extends BaseController {
    /**
     * 
     */
    @Resource
    private OrderService orderService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
    /**
     * 删除委托单数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        orderService.deleteRecords("PLAT_BITTRADE_ORDER","ORDER_ID",selectColValues.split(","));
        sysLogService.saveBackLog("行情管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的委托单", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改委托单数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> order = PlatBeanUtil.getMapFromRequest(request);
        orderService.genOrders(request);
        order.put("success", true);
        this.printObjectJsonString(order, response);
    }
    
    /**
     * 跳转到委托单表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String ORDER_ID = request.getParameter("ORDER_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> order = null;
        if(StringUtils.isNotEmpty(ORDER_ID)){
            order = this.orderService.getRecord("PLAT_BITTRADE_ORDER"
                    ,new String[]{"ORDER_ID"},new Object[]{ORDER_ID});
        }else{
            order = new HashMap<String,Object>();
        }
        request.setAttribute("order", order);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String ORDER_ID = request.getParameter("ORDER_ID");
        String ORDER_PARENTID = request.getParameter("ORDER_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> order = null;
        if(StringUtils.isNotEmpty(ORDER_ID)){
            order = this.orderService.getRecord("PLAT_BITTRADE_ORDER"
                    ,new String[]{"ORDER_ID"},new Object[]{ORDER_ID});
            ORDER_PARENTID = (String) order.get("Order_PARENTID");
        }
        Map<String,Object> parentOrder = null;
        if(ORDER_PARENTID.equals("0")){
            parentOrder = new HashMap<String,Object>();
            parentOrder.put("ORDER_ID",ORDER_PARENTID);
            parentOrder.put("ORDER_NAME","委托单树");
        }else{
            parentOrder = this.orderService.getRecord("PLAT_BITTRADE_ORDER",
                    new String[]{"ORDER_ID"}, new Object[]{ORDER_PARENTID});
        }
        if(order==null){
            order = new HashMap<String,Object>();
        }
        order.put("ORDER_PARENTID",parentOrder.get("ORDER_ID"));
        order.put("ORDER_PARENTNAME",parentOrder.get("ORDER_NAME"));
        request.setAttribute("order", order);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
}

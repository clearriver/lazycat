/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

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
import com.stooges.platform.appmodel.service.FieldConfigService;
import com.stooges.platform.common.controller.BaseController;

/**
 * 
 * 描述 FieldConfig业务相关Controller
 * @author 胡裕
 * @version 1.0
 * @created 2017-03-02 10:54:42
 */
@Controller  
@RequestMapping("/appmodel/FieldConfigController")  
public class FieldConfigController extends BaseController {
    /**
     * 
     */
    @Resource
    private FieldConfigService fieldConfigService;
    
}

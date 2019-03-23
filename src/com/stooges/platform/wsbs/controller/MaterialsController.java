/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.controller;

import java.text.ParseException;
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
import com.stooges.core.model.SqlFilter;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.wsbs.service.MaterialsService;

/**
 * 
 * 描述 事项申报材料列表业务相关Controller
 * @author 李俊
 * @version 1.0
 * @created 2017-05-18 11:02:38
 */
@Controller  
@RequestMapping("/wsbs/MaterialsController")  
public class MaterialsController extends BaseController {
    /**
     * 
     */
    @Resource
    private MaterialsService materialsService;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;
    
}

/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.workflow;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.stooges.core.test.BaseTestCase;
import com.stooges.platform.workflow.model.FlowNode;
import com.stooges.platform.workflow.service.FlowNodeService;

/**
 * @author 胡裕
 *
 * 
 */
public class FlowNodeServiceTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private FlowNodeService flowNodeService;
    
    /**
     * 
     */
    @Test
    public void findDefNodeList(){
        List<Map> list= flowNodeService.
                findDefNodeList("4028d0815f13a781015f13aa8ad10010",2,FlowNode.NODETYPE_TASK);
        System.out.println(JSON.toJSON(list));
    }
}

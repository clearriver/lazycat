/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.web.tag;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang3.StringEscapeUtils;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatFileUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.service.ButtonBindService;

/**
 * 描述 流程按钮工具栏
 * @author 胡裕
 * @created 2017年5月14日 上午8:50:23
 */
public class FlowBtnToolbarTag extends BaseCompTag {

    /**
     * 方法doEndTag
     * 
     * @return 返回值int
     */
    public int doEndTag() throws JspException {
        JspWriter out = this.pageContext.getOut();
        Map<String,Object> paramMap = PlatBeanUtil.getSonAndSuperClassField(this);
        String tplPath = PlatAppUtil.getAppAbsolutePath()+"webpages/common/plattagtpl/flowbtntoolbar_tag.jsp";
        String tplString = PlatFileUtil.readFileString(tplPath);
        JbpmFlowInfo jbpmFlowInfo = (JbpmFlowInfo) PlatAppUtil.getRequest().getAttribute("jbpmFlowInfo");
        ButtonBindService buttonBindService = (ButtonBindService) PlatAppUtil.getBean("buttonBindService");
        List<Map<String,Object>> buttonList = buttonBindService.findButtonBind(jbpmFlowInfo);
        paramMap.put("buttonList", buttonList);
        String jbpmFlowInfoJson = JSON.toJSONString(jbpmFlowInfo);
        paramMap.put("JbpmFlowInfoJson", StringEscapeUtils.escapeHtml3(jbpmFlowInfoJson));
        String htmlContent = PlatStringUtil.getFreeMarkResult(tplString, paramMap);
        try {
            out.print(htmlContent);
        } catch (IOException e) {
            PlatLogUtil.printStackTrace(e);
        }
        return EVAL_PAGE;
    }
}

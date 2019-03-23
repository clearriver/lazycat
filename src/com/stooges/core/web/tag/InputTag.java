/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.web.tag;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatUICompUtil;

/**
 * 描述 自定义输入框
 * @author 胡裕
 * @created 2017年2月6日 上午10:15:16
 */
public class InputTag extends BaseCompTag {
   
    /**
     * 最大输入长度值
     */
    private String maxlength;
    /**
     * 验证规则
     */
    private String datarule;
    /**
     * 允许输入最大值
     */
    private String max;
    /**
     * 允许输入最小值
     */
    private String min;
    /**
     * 是否密码输入框(true,false)
     */
    private String ispass;
    
    /**
     * @return the ispass
     */
    public String getIspass() {
        return ispass;
    }

    /**
     * @param ispass the ispass to set
     */
    public void setIspass(String ispass) {
        this.ispass = ispass;
    }

    /**
     * 方法doStartTag
     * 
     * @return 返回值int
     */
    public int doStartTag() throws JspException {
        return EVAL_PAGE;
    }
    
    /**
     * 方法doEndTag
     * 
     * @return 返回值int
     */
    public int doEndTag() throws JspException {
        JspWriter out = this.pageContext.getOut();
        Map<String,Object> paramMap = PlatBeanUtil.getSonAndSuperClassField(this);
        String htmlStr = PlatUICompUtil.getInputTagHtml(paramMap);
        try {
            out.print(htmlStr);
        } catch (IOException e) {
            PlatLogUtil.printStackTrace(e);
        }
        return EVAL_PAGE;
    }
    
    /**
     * @return the maxlength
     */
    public String getMaxlength() {
        return maxlength;
    }
    /**
     * @param maxlength the maxlength to set
     */
    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }
    /**
     * @return the datarule
     */
    public String getDatarule() {
        return datarule;
    }
    /**
     * @param datarule the datarule to set
     */
    public void setDatarule(String datarule) {
        this.datarule = datarule;
    }
    /**
     * @return the max
     */
    public String getMax() {
        return max;
    }
    /**
     * @param max the max to set
     */
    public void setMax(String max) {
        this.max = max;
    }
    /**
     * @return the min
     */
    public String getMin() {
        return min;
    }
    /**
     * @param min the min to set
     */
    public void setMin(String min) {
        this.min = min;
    }
}

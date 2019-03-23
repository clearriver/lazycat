/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.web.tag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatUICompUtil;

/**
 * 描述 自定义下拉框
 * @author 胡裕
 * @created 2017年2月5日 下午12:38:05
 */
public class SelectTag extends BaseCompTag {
    
    /**
     * 是否树形下拉框:true,false
     */
    private String istree;
    /**
     * 是否可多选:multiple
     */
    private String multiple;
    /**
     * 最大可选数量
     */
    private String max_select_num;
    /**
     * 当是树形下拉框的时候,是否只能选择叶子节点
     * true,false
     */
    private String onlyselectleaf;

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
        String htmlStr = PlatUICompUtil.getSelectTagHtml(paramMap);
        try {
            out.print(htmlStr);
        } catch (IOException e) {
           PlatLogUtil.printStackTrace(e);
        }
        return EVAL_PAGE;
    }

    /**
     * @return the multiple
     */
    public String getMultiple() {
        return multiple;
    }

    /**
     * @param multiple the multiple to set
     */
    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    /**
     * @return the max_select_num
     */
    public String getMax_select_num() {
        return max_select_num;
    }

    /**
     * @param max_select_num the max_select_num to set
     */
    public void setMax_select_num(String max_select_num) {
        this.max_select_num = max_select_num;
    }
    /**
     * @return the istree
     */
    public String getIstree() {
        return istree;
    }

    /**
     * @param istree the istree to set
     */
    public void setIstree(String istree) {
        this.istree = istree;
    }
    

    /**
     * @return the onlyselectleaf
     */
    public String getOnlyselectleaf() {
        return onlyselectleaf;
    }

    /**
     * @param onlyselectleaf the onlyselectleaf to set
     */
    public void setOnlyselectleaf(String onlyselectleaf) {
        this.onlyselectleaf = onlyselectleaf;
    }

}

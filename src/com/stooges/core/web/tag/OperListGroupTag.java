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
 * 描述 带操作按钮列表组标签
 * @author 胡裕
 * @created 2017年2月20日 下午2:53:14
 */
public class OperListGroupTag extends BaseCompTag {

    /**
     * 列表组标题
     */
    private String grouptitle;
    /**
     * 列表点击事件的实现
     */
    private String onclickfn;
    /**
     * 新增函数名称
     */
    private String addclickfn;
    /**
     * 编辑函数名称
     */
    private String editclickfn;
    /**
     * 
     * 删除函数名称
     */
    private String delclickfn;
    
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
        String htmlStr = PlatUICompUtil.getOperListGroupTagHtml(paramMap);
        try {
            out.print(htmlStr);
        } catch (IOException e) {
            PlatLogUtil.printStackTrace(e);
        }
        return EVAL_PAGE;
    }
    
    /**
     * @return the grouptitle
     */
    public String getGrouptitle() {
        return grouptitle;
    }

    /**
     * @param grouptitle the grouptitle to set
     */
    public void setGrouptitle(String grouptitle) {
        this.grouptitle = grouptitle;
    }

    /**
     * @return the onclickfn
     */
    public String getOnclickfn() {
        return onclickfn;
    }

    /**
     * @param onclickfn the onclickfn to set
     */
    public void setOnclickfn(String onclickfn) {
        this.onclickfn = onclickfn;
    }
    

    /**
     * @return the addclickfn
     */
    public String getAddclickfn() {
        return addclickfn;
    }

    /**
     * @param addclickfn the addclickfn to set
     */
    public void setAddclickfn(String addclickfn) {
        this.addclickfn = addclickfn;
    }

    /**
     * @return the editclickfn
     */
    public String getEditclickfn() {
        return editclickfn;
    }

    /**
     * @param editclickfn the editclickfn to set
     */
    public void setEditclickfn(String editclickfn) {
        this.editclickfn = editclickfn;
    }

    /**
     * @return the delclickfn
     */
    public String getDelclickfn() {
        return delclickfn;
    }

    /**
     * @param delclickfn the delclickfn to set
     */
    public void setDelclickfn(String delclickfn) {
        this.delclickfn = delclickfn;
    }
}
